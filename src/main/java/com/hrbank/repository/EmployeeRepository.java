package com.hrbank.repository;

import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.entity.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
  Optional<Employee> findByEmail(String email);

  // EmployeeCsvGenerator에서 사용하는 메서드
  @Query(
      "select e "
          + "from Employee e "
          + "left join fetch e.department "
          + "where e.id > :lastId "
          + "order by e.id asc"
  )
  List<Employee> findNextChunk(@Param("lastId") Long lastId, Pageable pageable);

  //EmployeeTrend 에서 사용하는 메서드
  @Query("SELECT new com.hrbank.dto.employee.EmployeeTrendDto(" +
      "FUNCTION('DATE', e.hireDate), " +
      "COUNT(e.employeeNumber), " +
      "(COUNT(e.employeeNumber) - COUNT(e.employeeNumber)) AS change, " +
      "(COUNT(e.employeeNumber) - COUNT(e.employeeNumber)) * 100 / COUNT(e.employeeNumber) AS changeRate) " +
      "FROM Employee e " +
      "GROUP BY FUNCTION('DATE', e.hireDate) " +
      "ORDER BY e.hireDate DESC")
  Page<EmployeeTrendDto> findEmployeeTrends(EmployeeSearchCondition condition, Pageable pageable);

  Page<Employee> findAllWithFilter(EmployeeSearchCondition condition, Pageable pageable);
}
