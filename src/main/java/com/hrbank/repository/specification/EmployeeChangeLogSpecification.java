package com.hrbank.repository.specification;

import com.hrbank.dto.employeeChangeLog.EmployeeChangeLogSearchRequest;
import com.hrbank.entity.EmployeeChangeLog;
import com.hrbank.enums.EmployeeChangeLogType;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeChangeLogSpecification {

  public static Specification<EmployeeChangeLog> employeeNumberContains(String keyword) {
    return (root, query, cb) ->
        keyword == null || keyword.isBlank()
            ? null
            : cb.like(root.get("employeeNumber"), "%" + keyword + "%");
  }

  public static Specification<EmployeeChangeLog> memoContains(String keyword) {
    return (root, query, cb) ->
        keyword == null || keyword.isBlank()
            ? null
            : cb.like(root.get("memo"), "%" + keyword + "%");
  }

  public static Specification<EmployeeChangeLog> ipAddressContains(String keyword) {
    return (root, query, cb) ->
        keyword == null || keyword.isBlank()
            ? null
            : cb.like(root.get("ipAddress"), "%" + keyword + "%");
  }

  public static Specification<EmployeeChangeLog> typeEquals(EmployeeChangeLogType type) {
    return (root, query, cb) ->
        type == null ? null : cb.equal(root.get("type"), cb.literal(type));  // enum 타입으로 처리할 수 있도록
  }

  public static Specification<EmployeeChangeLog> atBetween(LocalDateTime start, LocalDateTime end) {
    return (root, query, cb) ->
        (start == null || end == null)
            ? null
            : cb.between(root.get("at"), start, end);
  }

  // 검색을 위한 메서드
  public static Specification<EmployeeChangeLog> search(EmployeeChangeLogSearchRequest request) {
    return Specification.where(employeeNumberContains(request.employeeNumber()))
        .and(memoContains(request.memo()))
        .and(ipAddressContains(request.ipAddress()))
        .and(typeEquals(request.type()))
        .and(atBetween(request.atFrom(), request.atTo()));
  }
}