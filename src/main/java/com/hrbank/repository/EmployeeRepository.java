package com.hrbank.repository;

import com.hrbank.entity.Employee;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
  Optional<Employee> findByEmail(String email);
}
