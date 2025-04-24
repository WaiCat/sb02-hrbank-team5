package com.hrbank.repository;

import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

  @Autowired
  private EntityManager entityManager;

  @Override
  public Page<Employee> findAllWithFilter(EmployeeSearchCondition condition, Pageable pageable) {
    // 기본적인 쿼리 시작
    String jpql = "SELECT e FROM Employee e LEFT JOIN e.department d WHERE 1=1";

    // 필터링 조건 추가
    if (condition.getNameOrEmail() != null) {
      jpql += " AND (e.name LIKE :nameOrEmail OR e.email LIKE :nameOrEmail)";
    }
    if (condition.getDepartment() != null) {
      jpql += " AND d.name = :department";
    }
    if (condition.getStatus() != null) {
      jpql += " AND e.status = :status";
    }

    // 쿼리 실행
    TypedQuery<Employee> query = entityManager.createQuery(jpql, Employee.class);

    // 파라미터 바인딩
    if (condition.getNameOrEmail() != null) {
      query.setParameter("nameOrEmail", "%" + condition.getNameOrEmail() + "%");
    }
    if (condition.getDepartment() != null) {
      query.setParameter("department", condition.getDepartment());
    }
    if (condition.getStatus() != null) {
      query.setParameter("status", condition.getStatus());
    }

    // 페이징 처리
    query.setFirstResult((int) pageable.getOffset());  // getOffset()을 사용하여 시작 위치 설정
    query.setMaxResults(pageable.getPageSize());  // getPageSize()를 사용하여 한 페이지 크기 설정

    // 결과 목록
    List<Employee> employees = query.getResultList();

    // 페이지 객체 반환 (총 결과 수는 따로 계산)
    long totalElements = getTotalElements(condition);  // 총 직원 수를 구하는 별도 메서드 호출

    return new PageImpl<>(employees, pageable, totalElements);
  }

  // 전체 결과 수를 계산하는 별도 메서드 (필터링된 직원 수 계산)
  private long getTotalElements(EmployeeSearchCondition condition) {
    String jpql = "SELECT COUNT(e) FROM Employee e LEFT JOIN e.department d WHERE 1=1";

    // 필터링 조건 추가
    if (condition.getNameOrEmail() != null) {
      jpql += " AND (e.name LIKE :nameOrEmail OR e.email LIKE :nameOrEmail)";
    }
    if (condition.getDepartment() != null) {
      jpql += " AND d.name = :department";
    }
    if (condition.getStatus() != null) {
      jpql += " AND e.status = :status";
    }

    TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);

    // 파라미터 바인딩
    if (condition.getNameOrEmail() != null) {
      query.setParameter("nameOrEmail", "%" + condition.getNameOrEmail() + "%");
    }
    if (condition.getDepartment() != null) {
      query.setParameter("department", condition.getDepartment());
    }
    if (condition.getStatus() != null) {
      query.setParameter("status", condition.getStatus());
    }

    // 총 직원 수 반환
    return query.getSingleResult();
  }
}