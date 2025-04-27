package com.hrbank.repository;

import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.ArrayList;
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
    String jpql = "SELECT e FROM Employee e JOIN FETCH e.department d WHERE 1=1";

    // 필터링 조건 추가
    if (condition.getNameOrEmail() != null) {
      jpql += " AND (e.name LIKE :nameOrEmail OR e.email LIKE :nameOrEmail)"; // 이름 또는 이메일 부분일치
    }

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
    if (condition.getNameOrEmail() != null) {
      query.setParameter("nameOrEmail", "%" + condition.getNameOrEmail() + "%");
    }
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
  public List<EmployeeTrendDto> findEmployeeTrends(LocalDate from, LocalDate to, String unit) {
    // 날짜(date) 저장 -> count세기 -> change세기 -> change Rate 구하기(직전의 count, 이번의 change 필요)
    // 제일 옛날의 날짜(13개중 1번째) count와 change는 0과 0으로. changeRate도 0.0으로. 이전 cnt가 0이면 증감률 0.0으로 나와야 함.
    // from이 null이면 unit기준 12개 이전부터 구해야 하고, 아니라면 to까지 unit기준으로 구해야 함. 어렵다;
    if(to == null){
      to = LocalDate.now();
    }
    if (from == null) {
      switch (unit) {
        case "year":
          from = to.minusYears(12);
          break;
        case "quarter":
          from = to.minusMonths(36);
          break;
        case "month":
          from = to.minusMonths(12);
          break;
        case "week":
          from = to.minusWeeks(12);
          break;
        case "day":
        default:
          from = to.minusDays(12);
          break;
      }
    }

    List<EmployeeTrendDto> employeeTrendDtos = new ArrayList<>();
    long prevCnt = 0L;
    LocalDate current = from;
    while(!current.isAfter(to)){
      long currCnt = countEmployeesUntil(current);
      long change = currCnt - prevCnt;
      Double changeRate = (prevCnt == 0L) ? 0.0 : (change * 100.0 / prevCnt);

      employeeTrendDtos.add(EmployeeTrendDto.of(current, currCnt, change, changeRate));

      prevCnt = currCnt;
      switch (unit) {
        case "year":
          current = current.plusYears(1);
          break;
        case "quarter":
          current = current.plusMonths(3);
          break;
        case "month":
          current = current.plusMonths(1);
          break;
        case "week":
          current = current.plusWeeks(1);
          break;
        case "day":
        default:
          current = current.plusDays(1);
          break;
      }
    }
    return employeeTrendDtos;
  }

  private long countEmployeesUntil(LocalDate date){
    String jpql = "SELECT COUNT(e) FROM Employee e WHERE e.hireDate <= :date";
    return entityManager.createQuery(jpql, Long.class)
        .setParameter("date", date)
        .getSingleResult();
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