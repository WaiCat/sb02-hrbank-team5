package com.hrbank.repository.specification;

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
        type == null ? null : cb.equal(root.get("type"), type);
  }

  public static Specification<EmployeeChangeLog> atBetween(LocalDateTime start, LocalDateTime end) {
    return (root, query, cb) ->
        (start == null || end == null)
            ? null
            : cb.between(root.get("at"), start, end);
  }
}