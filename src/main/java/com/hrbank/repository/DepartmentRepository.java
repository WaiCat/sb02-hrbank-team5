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

    //이름 또는 설명으로 부분 일치 검색
    @Query("SELECT d FROM Department d WHERE " +
            "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(d.description) LIKE LOWER(CONCAT('%', :description, '%')))")
    List<Department> findByNameContainingOrDescriptionContaining(
            @Param("name") String name,
            @Param("description") String description);

    // 이름으로 정렬하여 커서 기반 페이지네이션 - Native Query로 변경
    @Query(value = "SELECT * FROM departments d WHERE " +
            "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(d.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:lastId IS NULL OR d.name > (SELECT d2.name FROM departments d2 WHERE d2.id = :lastId)) " +
            "ORDER BY d.name ASC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Department> findByNameContainingOrDescriptionContainingOrderByNameAsc(
            @Param("name") String name,
            @Param("description") String description,
            @Param("lastId") Long lastId,
            @Param("limit") int limit);

    // 설립일로 정렬하여 커서 기반 페이지네이션
    @Query(value = "SELECT * FROM departments d WHERE " +
            "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(d.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:lastId IS NULL OR " +
            "(d.established_date > (SELECT d2.established_date FROM departments d2 WHERE d2.id = :lastId)) OR " +
            "(d.established_date = (SELECT d2.established_date FROM departments d2 WHERE d2.id = :lastId) AND " +
            "d.id > :lastId)) " +
            "ORDER BY d.established_date ASC, d.id ASC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Department> findByNameContainingOrDescriptionContainingOrderByEstablishedDateAsc(
            @Param("name") String name,
            @Param("description") String description,
            @Param("lastId") Long lastId,
            @Param("limit") int limit);

    // 부서에 소속된 직원이 있는지 확인
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.department.id = :departmentId")
    boolean hasEmployees(@Param("departmentId") Long departmentId);

}
