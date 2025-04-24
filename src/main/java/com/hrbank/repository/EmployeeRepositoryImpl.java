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
    if (condition.getDepartment() != null) {
      jpql += " AND d.name LIKE :department"; // 부분 일치로 변경
    }

    if (condition.getPosition() != null) {
      jpql += " AND e.position LIKE :position";  // 포지션 부분 일치
    }

    if (condition.getEmployeeNumber() != null) {
      jpql += " AND e.employeeNumber LIKE :employeeNumber";  // 사원번호 부분 일치
    }

    if (condition.getHireDateFrom() != null) {
      jpql += " AND e.hireDate >= :hireDateFrom"; // 입사일 이후
    }
    if (condition.getHireDateTo() != null) {
      jpql += " AND e.hireDate <= :hireDateTo"; // 입사일 이전
    }

    // 쿼리 실행
    TypedQuery<Employee> query = entityManager.createQuery(jpql, Employee.class);

    // 파라미터 바인딩
    if (condition.getDepartment() != null) {
      query.setParameter("department", "%" + condition.getDepartment() + "%");  // 부분 일치
    }
    if (condition.getPosition() != null) {
      query.setParameter("position", "%" + condition.getPosition() + "%");  // 부분 일치
    }
    if (condition.getEmployeeNumber() != null) {
      query.setParameter("employeeNumber", "%" + condition.getEmployeeNumber() + "%");  // 부분 일치
    }
    if (condition.getHireDateFrom() != null) {
      query.setParameter("hireDateFrom", condition.getHireDateFrom());
    }
    if (condition.getHireDateTo() != null) {
      query.setParameter("hireDateTo", condition.getHireDateTo());
    }

    // 페이징 처리
    query.setMaxResults(pageable.getPageSize());

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
    if (condition.getDepartment() != null) {
      jpql += " AND d.name LIKE :department"; // 부서 필터 부분 일치
    }
    if (condition.getPosition() != null) {
      jpql += " AND e.position LIKE :position"; // 포지션 필터 부분 일치
    }
    if (condition.getEmployeeNumber() != null) {
      jpql += " AND e.employeeNumber LIKE :employeeNumber"; // 사원번호 필터 부분 일치
    }
    if (condition.getHireDateFrom() != null) {
      jpql += " AND e.hireDate >= :hireDateFrom"; // 입사일 이후 필터
    }
    if (condition.getHireDateTo() != null) {
      jpql += " AND e.hireDate <= :hireDateTo"; // 입사일 이전 필터
    }

    TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);

    // 파라미터 바인딩
    if (condition.getDepartment() != null) {
      query.setParameter("department", "%" + condition.getDepartment() + "%"); // 부서 필터 부분 일치
    }
    if (condition.getPosition() != null) {
      query.setParameter("position", "%" + condition.getPosition() + "%"); // 포지션 필터 부분 일치
    }
    if (condition.getEmployeeNumber() != null) {
      query.setParameter("employeeNumber", "%" + condition.getEmployeeNumber() + "%"); // 사원번호 필터 부분 일치
    }
    if (condition.getHireDateFrom() != null) {
      query.setParameter("hireDateFrom", condition.getHireDateFrom());
    }
    if (condition.getHireDateTo() != null) {
      query.setParameter("hireDateTo", condition.getHireDateTo());
    }

    // 총 직원 수 반환
    return query.getSingleResult();
  }
}