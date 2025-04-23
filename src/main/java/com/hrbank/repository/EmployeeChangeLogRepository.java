package com.hrbank.repository;

import com.hrbank.entity.EmployeeChangeLog;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeChangeLogRepository extends JpaRepository<EmployeeChangeLog, UUID>, JpaSpecificationExecutor<EmployeeChangeLog> {
  // 특정 시점 이후 변경된 내용이 있는지 로그를 체크하는 메서드
  // "SELECT EXISTS(SELECT 1 FROM change_logs WHERE at > :at)"
  boolean existsByAtAfter(LocalDateTime after);
}
