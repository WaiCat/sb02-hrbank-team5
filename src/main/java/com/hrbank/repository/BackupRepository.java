package com.hrbank.repository;

import com.hrbank.entity.Backup;
import com.hrbank.enums.BackupStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long>,
    JpaSpecificationExecutor<Backup> {

  //가장 최근에 status한 백업 조회
  Optional<Backup> findTopByStatusOrderByEndedAtDesc(BackupStatus status);

  boolean existsByStatus(BackupStatus status);

}
