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

    List<Department> findByNameOrDescriptionWithSorting(
            @Param("name") String name,
            @Param("description") String description,
            @Param("lastId") Long lastId,
            @Param("limit") int limit,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection);

    // 부서에 소속된 직원이 있는지 확인
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.department.id = :departmentId")
    boolean hasEmployees(@Param("departmentId") Long departmentId);

}
