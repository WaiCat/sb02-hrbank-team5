package com.hrbank.repository;

import com.hrbank.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    //이름으로 부서 조회(중복 검사용)
    Optional<Department> findByName(String name);

    //이름이 다른 부서 중에서 동일한 이름이 있는지 확인(수정 시 사용)
    Optional<Department> findByNameExcludingId(String name, Long id);

    // 이름 또는 설명으로 부분 일치 검색 및 정렬
    @Query(value = "SELECT * FROM departments d WHERE " +
            "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) OR " +
            "(:description IS NULL OR LOWER(d.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:lastId IS NULL OR d.id > :lastId) " +
            "ORDER BY " +
            "CASE WHEN :sortField = 'name' AND :sortDirection = 'asc' THEN d.name END ASC, " +
            "CASE WHEN :sortField = 'name' AND :sortDirection = 'desc' THEN d.name END DESC, " +
            "CASE WHEN :sortField = 'establishedDate' AND :sortDirection = 'asc' THEN d.established_date END ASC, " +
            "CASE WHEN :sortField = 'establishedDate' AND :sortDirection = 'desc' THEN d.established_date END DESC, " +
            "d.id ASC " +
            "LIMIT :limit",
            nativeQuery = true)
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

    // 전체 개수 조회 쿼리
    @Query("SELECT COUNT(d) FROM Department d WHERE " +
            "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) OR " +
            "(:description IS NULL OR LOWER(d.description) LIKE LOWER(CONCAT('%', :description, '%')))")
    Long countByNameOrDescription(
            @Param("name") String name,
            @Param("description") String description
    );
}
