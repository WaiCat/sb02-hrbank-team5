package com.hrbank.repository;

import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.entity.Employee;
import java.util.List;
import java.util.Optional;

import com.hrbank.enums.EmployeeStatus;
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

  Page<EmployeeTrendDto> findEmployeeTrends(EmployeeSearchCondition condition, Pageable pageable);

  Page<Employee> findAllWithFilter(EmployeeSearchCondition condition, Pageable pageable);
  @Query("SELECT COUNT(e) FROM Employee e " +
      "WHERE e.status = :status " +
      "AND e.hireDate BETWEEN :fromDate AND :toDate")
  long countByStatusAndHireDate(
      @Param("status") EmployeeStatus status,
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate
  );

  // 전체 직원 수 집계 쿼리
  long countByStatus(EmployeeStatus status);

  // 부서별 직원 수 집계 쿼리
  @Query("SELECT d.name as groupKey, COUNT(e) as count FROM Employee e JOIN e.department d " +
          "WHERE (:status IS NULL OR e.status = :status) GROUP BY d.name")
  List<Object[]> countByDepartmentAndStatus(@Param("status") EmployeeStatus status);

  // 직무별 직원 수 집계 쿼리
  @Query("SELECT e.position as groupKey, COUNT(e) as count FROM Employee e " +
          "WHERE (:status IS NULL OR e.status = :status) GROUP BY e.position")
  List<Object[]> countByPositionAndStatus(@Param("status") EmployeeStatus status);

}
