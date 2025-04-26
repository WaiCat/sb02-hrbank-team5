package com.hrbank.service.basic;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.dto.binarycontent.BinaryContentDto;
import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDistributionDto;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.dto.employee.EmployeeUpdateRequest;
import com.hrbank.entity.BinaryContent;
import com.hrbank.entity.Department;
import com.hrbank.entity.Employee;
import com.hrbank.enums.EmployeeStatus;
import com.hrbank.exception.ErrorCode;
import com.hrbank.exception.RestException;
import com.hrbank.mapper.BinaryContentMapper;
import com.hrbank.mapper.EmployeeMapper;
import com.hrbank.repository.DepartmentRepository;
import com.hrbank.repository.EmployeeRepository;
import com.hrbank.service.BinaryContentService;
import com.hrbank.service.EmployeeChangeLogService;
import com.hrbank.service.EmployeeService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final BinaryContentService binaryContentService;
  private final EmployeeMapper employeeMapper;
  private final EmployeeChangeLogService changeLogService;
  private final BinaryContentMapper binaryContentMapper;


  @Override
  public CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition) {
    // 페이지네이션 처리
    Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize(), Sort.by(condition.getSortField()));

    // 직원 목록 조회
    Page<Employee> employees = employeeRepository.findAllWithFilter(condition, pageable);

    List<EmployeeDto> employeeDtos = employeeMapper.toDtoList(employees.getContent());

    // 커서와 페이지네이션 정보 계산
    Long nextIdAfter = employees.getContent().isEmpty() ? null : employees.getContent().get(employees.getContent().size() - 1).getId();
    String nextCursor = (nextIdAfter != null) ? nextIdAfter.toString() : null;  // nextCursor를 String으로 설정

    int size = employees.getSize();
    Long totalElements = employees.getTotalElements();
    boolean hasNext = employees.hasNext();

    // 응답 DTO 반환
    return new CursorPageResponseEmployeeDto(employeeDtos, nextCursor, nextIdAfter, size, totalElements, hasNext);
  }

  // 직원 수정 메서드 (update)
  @Override
  @Transactional
  public EmployeeDto update(Long id, EmployeeUpdateRequest request, String ip) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RestException(ErrorCode.EMPLOYEE_NOT_FOUND));

    // 이메일 중복 체크
    if (employeeRepository.findByEmail(request.email()).isPresent()) {
      throw new RestException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    // 부서 조회
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new RestException(ErrorCode.DEPARTMENT_NOT_FOUND));

    BinaryContent profileImage = null;
    if (request.profileImageId() != null) {
        BinaryContentDto profileImageDto = binaryContentService.findById(request.profileImageId());
        profileImage = binaryContentMapper.toEntity(profileImageDto);

        // 기존 프로필 이미지 삭제
        if (employee.getProfileImage() != null) {
            binaryContentService.delete(employee.getProfileImage().getId());
        }
    }

    // 변경 전 상태 보존
    Employee before = new Employee(
        employee.getName(), employee.getEmail(), employee.getEmployeeNumber(),
        employee.getDepartment(), employee.getPosition(), employee.getHireDate(),
        employee.getStatus()
    );
    before.changeProfileImage(employee.getProfileImage());

    // 값 변경
    employee.changeDepartment(department);
    employee.changePosition(request.position());
    employee.changeStatus(request.status());
    employee.changeProfileImage(profileImage);
    employee.updateName(request.name());
    employee.updateEmail(request.email());
    employee.updateHireDate(request.hireDate());

    // 이력 로그 저장
    changeLogService.saveChangeLog(before, employee, request.memo(), ip);

    return employeeMapper.toDto(employee);
  }

  // 직원 생성 메서드 (create)
  @Override
  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage, String ip) {
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new RestException(ErrorCode.DEPARTMENT_NOT_FOUND));

    BinaryContent profileImageEntity = null;
    if (profileImage != null && !profileImage.isEmpty()) {
      BinaryContentCreateRequest profileImageCreateRequest = new BinaryContentCreateRequest(profileImage);
      profileImageEntity = binaryContentService.create(profileImageCreateRequest);
    }

    // 사원번호 생성
    String employeeNumber = generateEmployeeNumber();

    // 새로운 직원 생성
    Employee employee = new Employee(
        request.name(),
        request.email(),
        employeeNumber,
        department,
        request.position(),
        request.hireDate(),
        EmployeeStatus.ACTIVE
    );

    if (profileImageEntity != null) {
        employee.changeProfileImage(profileImageEntity);
    }

    Employee before = new Employee(
        null, null, null, null, null, null, null
    );
    changeLogService.saveChangeLog(before, employee, request.memo(), ip);

    return employeeMapper.toDto(employee);
  }

  // 직원 삭제 메서드(delete)
  @Override
  @Transactional
  public void delete(Long id, String ip) {
    Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RestException(ErrorCode.EMPLOYEE_NOT_FOUND));
    Employee before = new Employee(employee.getName(), employee.getEmail(), employee.getEmployeeNumber(), employee.getDepartment(), employee.getPosition(), employee.getHireDate(), employee.getStatus());

    if (employee.getProfileImage() != null) {
      binaryContentService.delete(employee.getProfileImage().getId());
    }

    employeeRepository.delete(employee);
    changeLogService.saveChangeLog(before, null, "직원 삭제", ip);
  }

  @Override
  public EmployeeDto findById(Long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RestException(ErrorCode.EMPLOYEE_NOT_FOUND));
    return employeeMapper.toDto(employee);
  }

  @Override
  public Page<EmployeeTrendDto> findEmployeeTrends(EmployeeSearchCondition condition,
      Pageable pageable) {
    return employeeRepository.findEmployeeTrends(condition, pageable);
  }

  @Override
  public long getEmployeeCount(String status, LocalDate fromDate, LocalDate toDate) {
    EmployeeStatus employeeStatus =
        status != null ? EmployeeStatus.valueOf(status) : EmployeeStatus.ACTIVE;

    if(fromDate == null) {
      return employeeRepository.countByStatus(employeeStatus);
    }
    if (toDate == null) {
      toDate = LocalDate.now();
    }
    return employeeRepository.countByStatusAndHireDate(employeeStatus, fromDate, toDate);
  }

  // 사원번호 생성 함수
  private String generateEmployeeNumber() {
    return "EMP-" + LocalDate.now().getYear() + "-" + System.currentTimeMillis();
  }

  @Override
  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, String status) {

    EmployeeStatus employeeStatus = null;
    if (status != null && !status.isEmpty()) {
      employeeStatus = EmployeeStatus.valueOf(status);
    }

    List<Object[]> distributionData;
    if ("department".equals(groupBy)) {
      distributionData = employeeRepository.countByDepartmentAndStatus(employeeStatus);
    } else if ("position".equals(groupBy)) {
      distributionData = employeeRepository.countByPositionAndStatus(employeeStatus);
    } else {
      throw new IllegalArgumentException("지원하지 않는 그룹화 기준: " + groupBy);
    }

    long totalCount = distributionData.stream()
            .mapToLong(row -> (Long)row[1])
            .sum();

    return distributionData.stream()
            .map(row -> {
              String key = (String)row[0];
              Long count = (Long)row[1];
              double percentage = totalCount > 0 ? (count * 100.0) / totalCount : 0.0;
              return new EmployeeDistributionDto(key, count, percentage);
            })
            .collect(Collectors.toList());
  }
}