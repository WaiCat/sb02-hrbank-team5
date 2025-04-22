package com.hrbank.repository;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

  private final EntityManager em;

  @Override
  public CursorPageResponseEmployeeDto findAllWithFilter(EmployeeSearchCondition condition) {
    // TODO: Querydsl 또는 JPQL 로직 구현 예정 나중에 정할 예정
    return null;
  }
}