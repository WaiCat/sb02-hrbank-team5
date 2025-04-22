package com.hrbank.service.basic;

import com.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.hrbank.dto.department.DepartmentCreateRequest;
import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.dto.department.DepartmentUpdateRequest;
import com.hrbank.entity.Department;
import com.hrbank.mapper.DepartmentMapper;
import com.hrbank.repository.DepartmentRepository;
import com.hrbank.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "부서 이름이 이미 존재합니다: " + request.name());
        }

        // 부서 생성
        Department department = new Department(
                request.name(),
                request.description(),
                request.establishedDate()
        );
        Department savedDepartment = departmentRepository.save(department);

        return departmentMapper.toDto(savedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 부서입니다: " + id));

        return departmentMapper.toDto(department);
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 부서입니다: " + id));

        // 이름 변경시 중복 검사
        if (request.name() != null && !request.name().equals(department.getName())) {
            if (departmentRepository.findByNameExcludingId(request.name(), id).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "부서 이름이 이미 존재합니다: " + request.name());
            }
        }

        // 부서 정보 업데이트
        department.update(request.name(), request.description(), request.establishedDate());
        Department updatedDepartment = departmentRepository.save(department);

        return departmentMapper.toDto(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 부서입니다: " + id));

        // 소속된 직원이 있는지 확인
        if (departmentRepository.hasEmployees(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "소속된 직원이 있는 부서는 삭제할 수 없습니다");
        }

        departmentRepository.delete(department);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseDepartmentDto getDepartmentsByNameSorting(
            String nameKeyword, String descriptionKeyword, Long cursorId, int limit) {

        int pageSize = limit > 0 ? limit : DEFAULT_PAGE_SIZE;

        List<Department> departments = departmentRepository
                .findByNameContainingOrDescriptionContainingOrderByNameAsc(
                        nameKeyword, descriptionKeyword, cursorId, pageSize + 1);

        return createPageResponse(departments, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseDepartmentDto getDepartmentsByEstablishedDateSorting(
            String nameKeyword, String descriptionKeyword, Long cursorId, int limit) {

        int pageSize = limit > 0 ? limit : DEFAULT_PAGE_SIZE;

        List<Department> departments = departmentRepository
                .findByNameContainingOrDescriptionContainingOrderByEstablishedDateAsc(
                        nameKeyword, descriptionKeyword, cursorId, pageSize + 1);

        return createPageResponse(departments, pageSize);
    }

    // 커서 기반 페이지네이션 응답 생성 헬퍼 메서드
    private CursorPageResponseDepartmentDto createPageResponse(List<Department> departments, int pageSize) {
        boolean hasNext = departments.size() > pageSize;

        // 다음 페이지가 있으면 마지막 요소 제거
        if (hasNext) {
            departments = departments.subList(0, pageSize);
        }

        List<DepartmentDto> departmentDtos = departments.stream()
                .map(departmentMapper::toDto)
                .toList();

        Long lastId = !departments.isEmpty() ? departments.get(departments.size() - 1).getId() : null;

        return new CursorPageResponseDepartmentDto(departmentDtos, lastId, hasNext);
    }
}
