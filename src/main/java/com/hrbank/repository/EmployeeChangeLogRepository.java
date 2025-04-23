package com.hrbank.repository;

import com.hrbank.entity.EmployeeChangeLog;
import com.hrbank.entity.EmployeeChangeLogDetail;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeChangeLogRepository extends JpaRepository<EmployeeChangeLog, Long>, JpaSpecificationExecutor<EmployeeChangeLog> {
  // 특정 시점 이후 변경된 내용이 있는지 로그를 체크하는 메서드
  // "SELECT EXISTS(SELECT 1 FROM change_logs WHERE at > :at)"
  boolean existsByAtAfter(LocalDateTime after);
}
