package com.hrbank.repository;

import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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

  @Override
  public Page<EmployeeTrendDto> findEmployeeTrends(EmployeeSearchCondition condition, Pageable pageable) {
    // 1. 그룹화 단위 및 기간 기본값 처리
    String groupByUnit = (condition.getUnit() == null) ? "month" : condition.getUnit();
    LocalDate now = LocalDate.now();
    LocalDate from = condition.getFrom();
    LocalDate to = condition.getTo();

    if (from == null && to == null && "month".equals(groupByUnit)) {
      from = now.minusMonths(11).withDayOfMonth(1); // 최근 12개월의 첫날
      to = now.withDayOfMonth(now.lengthOfMonth());  // 이번달 마지막날
    }

    // 2. H2용 날짜 포맷 (FORMATDATETIME)
    String dateFormat;
    switch (groupByUnit) {
      case "year": dateFormat = "FORMATDATETIME(e.hireDate, 'yyyy')"; break;
      case "month": dateFormat = "FORMATDATETIME(e.hireDate, 'yyyy-MM')"; break;
      default: dateFormat = "FORMATDATETIME(e.hireDate, 'yyyy-MM-dd')"; // day
    }

    // 3. JPQL 동적 생성
    String jpql = "SELECT new com.hrbank.dto.employee.EmployeeTrendDto(" +
        dateFormat + ", " +
        "COUNT(e.id), 0L, 0.0) " +
        "FROM Employee e WHERE e.hireDate IS NOT NULL ";

    if (from != null) jpql += "AND e.hireDate >= :from ";
    if (to != null) jpql += "AND e.hireDate <= :to ";

    jpql += "GROUP BY " + dateFormat + " ORDER BY " + dateFormat + " DESC";

    TypedQuery<EmployeeTrendDto> query = entityManager.createQuery(jpql, EmployeeTrendDto.class);
    if (from != null) query.setParameter("from", from);
    if (to != null) query.setParameter("to", to);

    query.setFirstResult((int) pageable.getOffset());
    query.setMaxResults(pageable.getPageSize());

    List<EmployeeTrendDto> trends = query.getResultList();

    // 4. 증감/증감률 후처리
    for (int i = 0; i < trends.size(); i++) {
      EmployeeTrendDto current = trends.get(i);
      Long prevCount = (i + 1 < trends.size()) ? trends.get(i + 1).getCount() : 0L;
      long change = current.getCount() - prevCount;
      double changeRate = prevCount == 0 ? 0.0 : (change * 100.0) / prevCount;
      current.setChange(change);
      current.setChangeRate(changeRate);
    }

    // 5. 전체 개수 구하기
    String countJpql = "SELECT COUNT(DISTINCT " + dateFormat + ") FROM Employee e WHERE e.hireDate IS NOT NULL ";
    if (from != null) countJpql += "AND e.hireDate >= :from ";
    if (to != null) countJpql += "AND e.hireDate <= :to ";

    TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
    if (from != null) countQuery.setParameter("from", from);
    if (to != null) countQuery.setParameter("to", to);
    Long totalElements = countQuery.getSingleResult();

    return new PageImpl<>(trends, pageable, totalElements);
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