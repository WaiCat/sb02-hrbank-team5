package com.hrbank.entity;

import com.hrbank.enums.BackupStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@Table(name = "backups")
@AllArgsConstructor
public class Backup {

  public Backup() {  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String worker;

  @Column(name = "started_at", nullable = false)
  private OffsetDateTime startedAt;

  @Column(name = "ended_at")
  private OffsetDateTime endedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BackupStatus status;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id")
  private BinaryContent file;

  public void completeBackup(BinaryContent file) {
    this.status = BackupStatus.COMPLETED;
    this.endedAt = OffsetDateTime.now();
    this.file = file;
  }

  public void failBackup(BinaryContent logFile) {
    this.status = BackupStatus.FAILED;
    this.endedAt = OffsetDateTime.now();
    this.file = logFile;
  }

}
