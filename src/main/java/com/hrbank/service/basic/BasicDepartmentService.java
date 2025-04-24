package com.hrbank.service.basic;

import com.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.hrbank.dto.department.DepartmentCreateRequest;
import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.dto.department.DepartmentUpdateRequest;
import com.hrbank.entity.Department;
import com.hrbank.exception.ErrorCode;
import com.hrbank.exception.RestException;
import com.hrbank.mapper.DepartmentMapper;
import com.hrbank.repository.DepartmentRepository;
import com.hrbank.repository.specification.DepartmentSpecifications;
import com.hrbank.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicDepartmentService implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final int DEFAULT_PAGE_SIZE = 10;

    @Override
    @Transactional
    public DepartmentDto createDepartment(DepartmentCreateRequest request) {

        // 이름 중복 검사
        if (departmentRepository.findByName(request.name()).isPresent()) {
            throw new RestException(ErrorCode.DEPARTMENT_NAME_DUPLICATED);
        }

        // 부서 생성
        Department department = new Department(
                request.name(),
                request.description(),
                request.establishedDate()
        );
        Department savedDepartment = departmentRepository.save(department);

        return departmentMapper.toDto(savedDepartment, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RestException(ErrorCode.DEPARTMENT_NOT_FOUND));

        // 직원 수 조회 - N+1 문제 해결
        int employeeCount = departmentRepository.countEmployeesByDepartmentId(id);

        return departmentMapper.toDto(department, employeeCount);
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RestException(ErrorCode.DEPARTMENT_NOT_FOUND));

        // 이름 변경시 중복 검사
        if (request.name() != null && !request.name().equals(department.getName())) {
            if (departmentRepository.findByNameExcludingId(request.name(), id).isPresent()) {
                throw new RestException(ErrorCode.DEPARTMENT_NAME_DUPLICATED);
            }
        }

        // 부서 정보 업데이트
        department.update(request.name(), request.description(), request.establishedDate());
        Department updatedDepartment = departmentRepository.save(department);

        // 직원 수 조회 - N+1 문제 해결
        int employeeCount = departmentRepository.countEmployeesByDepartmentId(id);

        return departmentMapper.toDto(updatedDepartment, employeeCount);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RestException(ErrorCode.DEPARTMENT_NOT_FOUND));

        // 소속된 직원이 있는지 확인
        if (departmentRepository.hasEmployees(id)) {
            throw new RestException(ErrorCode.DEPARTMENT_HAS_EMPLOYEES);
        }

        departmentRepository.delete(department);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseDepartmentDto getDepartments(
            String nameOrDescription,
            Long idAfter,
            String cursor,
            Integer size,
            String sortField,
            String sortDirection) {

        int pageSize = (size != null && size > 0) ? size : DEFAULT_PAGE_SIZE;

        // sortField 검증 로직 - name 또는 establishedDate, 기본값: establishedDate
        if (sortField == null || (!sortField.equals("name") && !sortField.equals("establishedDate"))) {
            sortField = "establishedDate"; // 기본값으로 설정
        }

        // sortDirection 검증 - asc 또는 desc, 기본값: asc
        if (sortDirection == null || (!sortDirection.equals("asc") && !sortDirection.equals("desc"))) {
            sortDirection = "asc";  // 기본값은 asc
        }

        // Sort 생성
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortField).and(Sort.by(Sort.Direction.ASC, "id"));

        // 페이지네이션 (커서 기반이지만 한 번에 가져올 수량 제한)
        Pageable pageable = PageRequest.of(0, pageSize + 1, sort);

        // Specification 조립 - 커서 기반 조건 포함
        Specification<Department> spec = DepartmentSpecifications.buildSearchSpecification(
                nameOrDescription, idAfter, cursor, sortField, sortDirection
        );

        // 쿼리 실행
        List<Department> departments = departmentRepository.findAll(spec, pageable).getContent();

        // 전체 개수 조회 (커서 조건 제외한 필터 조건만으로 카운트)
        long totalElements = departmentRepository.count(
                DepartmentSpecifications.nameOrDescriptionContains(nameOrDescription)
        );

// N+1 문제 해결: 부서 ID 목록 추출
        List<Long> departmentIds = departments.stream()
                .map(Department::getId)
                .collect(Collectors.toList());

        // 모든 부서의 직원 수를 한 번의 쿼리로 조회
        Map<Long, Integer> employeeCountMap = new HashMap<>();
        if (!departmentIds.isEmpty()) {
            List<Object[]> counts = departmentRepository.countEmployeesByDepartmentIds(departmentIds);
            for (Object[] count : counts) {
                Long departmentId = (Long) count[0];
                Integer employeeCount = ((Number) count[1]).intValue();
                employeeCountMap.put(departmentId, employeeCount);
            }
        }
        return createPageResponse(departments, employeeCountMap, pageSize, sortField, sortDirection, totalElements);
    }

    // 커서 기반 페이지네이션 응답 생성 헬퍼 메서드 수정
    private CursorPageResponseDepartmentDto createPageResponse(
            List<Department> departments,
            Map<Long, Integer> employeeCountMap,
            int pageSize,
            String sortField,
            String sortDirection,
            Long totalElements) {

        boolean hasNext = departments.size() > pageSize;

        if (hasNext) {
            departments = departments.subList(0, pageSize);
        }

        // N+1 문제 해결: 각 부서의 직원 수를 미리 조회한 Map에서 가져옴
        List<DepartmentDto> departmentDtos = departments.stream()
                .map(dept -> {
                    int employeeCount = employeeCountMap.getOrDefault(dept.getId(), 0);
                    return departmentMapper.toDto(dept, employeeCount);
                })
                .collect(Collectors.toList());

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext && !departments.isEmpty()) {
            Department lastDepartment = departments.get(departments.size() - 1);
            nextIdAfter = lastDepartment.getId();

            // 정렬 필드에 따라 적절한 커서 값 설정
            if ("name".equals(sortField)) {
                nextCursor = lastDepartment.getName();
            } else if ("establishedDate".equals(sortField)) {
                nextCursor = lastDepartment.getEstablishedDate() != null ?
                        lastDepartment.getEstablishedDate().toString() : null;
            } else {
                nextCursor = String.valueOf(lastDepartment.getId());
            }
        }

        return new CursorPageResponseDepartmentDto(
                departmentDtos,
                nextCursor,
                nextIdAfter,
                pageSize,
                totalElements,
                hasNext
        );
    }
}