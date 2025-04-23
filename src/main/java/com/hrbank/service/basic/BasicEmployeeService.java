package com.hrbank.service.basic;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.entity.BinaryContent;
import com.hrbank.entity.Department;
import com.hrbank.entity.Employee;
import com.hrbank.enums.EmployeeStatus;
import com.hrbank.exception.ErrorCode;
import com.hrbank.exception.RestException;
import com.hrbank.mapper.EmployeeMapper;
import com.hrbank.repository.DepartmentRepository;
import com.hrbank.repository.EmployeeRepository;
import com.hrbank.service.BinaryContentService;
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

  @Override
  public CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition) {
    return employeeRepository.findAllWithFilter(condition);
  }

  @Override
  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request) {
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new RestException(ErrorCode.DEPARTMENT_NOT_FOUND));

    BinaryContent profileImage = null;
    if (request.profileImage() != null) {
        profileImage = binaryContentService.create(new BinaryContentCreateRequest(request.profileImage()));
    }

    String employeeNumber = generateEmployeeNumber();

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

    employeeRepository.save(employee);
    return employeeMapper.toDto(employee);
  }

  // 사원번호 생성 함수
  private String generateEmployeeNumber() {
    return "E" + System.currentTimeMillis();
  }
  // **{사원 번호}** 는 자동으로 부여되어야 합니다.
}