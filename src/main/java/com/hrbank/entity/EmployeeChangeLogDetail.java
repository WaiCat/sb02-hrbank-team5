package com.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "change_log_diffs")
public class EmployeeChangeLogDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "change_log_id")
  @Setter(AccessLevel.PACKAGE) // or PROTECTED
  private EmployeeChangeLog changeLog;

  @Column(name = "property_name", nullable = false)
  private String propertyName;

  @Column(columnDefinition = "TEXT")
  private String before;

  @Column(columnDefinition = "TEXT")
  private String after;

  public EmployeeChangeLogDetail(EmployeeChangeLog changeLog, String propertyName, String before, String after) {
    this.changeLog = changeLog;
    this.propertyName = propertyName;
    this.before = before;
    this.after = after;
  }
}