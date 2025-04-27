package com.hrbank.repository;

import com.hrbank.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

    //이름으로 부서 조회(중복 검사용)
    Optional<Department> findByName(String name);

    //이름이 다른 부서 중에서 동일한 이름이 있는지 확인(수정 시 사용)
    @Query("SELECT d FROM Department d WHERE d.name = :name AND d.id <> :id")
    Optional<Department> findByNameExcludingId(@Param("name") String name, @Param("id") Long id);

    // 부서에 소속된 직원이 있는지 확인
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.department.id = :departmentId")
    boolean hasEmployees(@Param("departmentId") Long departmentId);

    // 각 부서별 직원 수 조회 메서드
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    int countEmployeesByDepartmentId(@Param("departmentId") Long departmentId);

    // 여러 부서의 직원 수를 한번에 조회하는 메서드
    @Query("SELECT d.id, COUNT(e) FROM Department d LEFT JOIN Employee e ON d.id = e.department.id WHERE d.id IN :departmentIds GROUP BY d.id")
    List<Object[]> countEmployeesByDepartmentIds(@Param("departmentIds") List<Long> departmentIds);

}
