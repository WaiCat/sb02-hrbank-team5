package com.hrbank.service.basic;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.dto.binarycontent.BinaryContentDto;
import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    return employeeRepository.findAllWithFilter(condition);
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
  public EmployeeDto create(EmployeeCreateRequest request) {
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new RestException(ErrorCode.DEPARTMENT_NOT_FOUND));

    BinaryContent profileImage = null;
    if (request.profileImage() != null) {
        profileImage = binaryContentService.create(new BinaryContentCreateRequest(request.profileImage()));
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

    if (profileImage != null) {
        employee.changeProfileImage(profileImage);
    }

    // 직원 저장
    employeeRepository.save(employee);
    return employeeMapper.toDto(employee);
  }

  // 사원번호 생성 함수
  private String generateEmployeeNumber() {
    return "E" + System.currentTimeMillis();
  }


  @Override
  @Transactional
  public EmployeeDto delete(Long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RestException(ErrorCode.EMPLOYEE_NOT_FOUND));

    if (employee.getProfileImage() != null) {
      binaryContentService.delete(employee.getProfileImage().getId());
    }

    employeeRepository.delete(employee);

    return employeeMapper.toDto(employee);
  }
}