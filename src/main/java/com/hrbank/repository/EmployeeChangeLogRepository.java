package com.hrbank.repository;

import com.hrbank.entity.EmployeeChangeLog;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeChangeLogRepository extends JpaRepository<EmployeeChangeLog, Long>, JpaSpecificationExecutor<EmployeeChangeLog> {
  // 특정 시점 이후 변경된 내용이 있는지 로그를 체크하는 메서드
  // "SELECT EXISTS(SELECT 1 FROM change_logs WHERE at > :at)"
  boolean existsByAtAfter(OffsetDateTime after);

  // N + 1 문제를 해결하기 위한 메서드. id와 관련된 details를 한 번에 가져온다
  @Query("SELECT c FROM EmployeeChangeLog c LEFT JOIN FETCH c.details WHERE c.id = :id")
  Optional<EmployeeChangeLog> findWithDetailsById(@Param("id") Long id);
}
