package com.hrbank.entity;

import com.hrbank.enums.EmployeeChangeLogType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "change_logs")
public class EmployeeChangeLog {

  @Id
  @GeneratedValue
  private Long id;

  // 유형
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmployeeChangeLogType type;

  // 사번
  @Column(name="employee_number", nullable = false)
  private String employeeNumber;

  // 메모
  @Column(columnDefinition = "TEXT")
  private String memo;

  // IP 주소
  @Column(nullable = false)
  private String ipAddress;

  // 시간
  private LocalDateTime at;

  // change_log_diffs, 변경 상세 정보 저장
  @OneToMany(mappedBy = "changeLog", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EmployeeChangeLogDetail> details = new ArrayList<>();

  // 명시적 생성자
  public EmployeeChangeLog(EmployeeChangeLogType type, String employeeNumber, String memo, String ipAddress, LocalDateTime at) {
    this.type = type;
    this.employeeNumber = employeeNumber;
    this.memo = memo;
    this.ipAddress = ipAddress;
    this.at = at;
  }

  // 필요한 경우 detail 추가 메서드
  public void addDetail(EmployeeChangeLogDetail detail) {
    details.add(detail);
    detail.setChangeLog(this); // 양방향 연관관계 유지
  }

}