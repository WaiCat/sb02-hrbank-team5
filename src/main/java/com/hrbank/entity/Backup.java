package com.hrbank.entity;

import com.hrbank.enums.BackupStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@Table(name = "backups")
@AllArgsConstructor
public class Backup {

  public Backup() {

  }

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String worker;

  @Column(name = "started_at", nullable = false)
  private Instant startedAt;

  @Column(name = "ended_at")
  private Instant endedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BackupStatus status;

  @OneToOne
  @JoinColumn(name = "file_id")
  private BinaryContent file;


}
