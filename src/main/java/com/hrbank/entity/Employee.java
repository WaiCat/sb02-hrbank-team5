package com.hrbank.entity;

import com.hrbank.enums.EmployeeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(unique = true, nullable = false)
  private String employeeNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  @Column(nullable = false)
  private String position;

  @Column(nullable = false)
  private LocalDate hireDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmployeeStatus status;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_image_id")
  private BinaryContent profileImage;

  public Long getProfileImageId() {
    return this.profileImage != null ? this.profileImage.getId() : null;
  }

  // 필수 필드를 초기화하는 생성자
  public Employee(String name, String email, String employeeNumber, Department department, String position, LocalDate hireDate, EmployeeStatus status) {
    this.name = name;
    this.email = email;
    this.employeeNumber = employeeNumber;
    this.department = department;
    this.position = position;
    this.hireDate = hireDate;
    this.status = status;
  }

  // 상태 변경 메서드
  public void updateName(String name) {
    this.name = name;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updateHireDate(LocalDate hireDate) {
    this.hireDate = hireDate;
  }

  public void changePosition(String newPosition) {
    this.position = newPosition;
  }

  public void changeStatus(EmployeeStatus newStatus) {
    this.status = newStatus;
  }

  public void changeProfileImage(BinaryContent newProfileImage) {
    this.profileImage = newProfileImage;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public void changeDepartment(Department newDepartment) {
    //기존 부서에서 제거
    if (this.department != null) {
      this.department.getEmployees().remove(this);
    }

    // 새 부서 설정
    if (newDepartment != null) {
      newDepartment.getEmployees().add(this);
    }
    this.department = newDepartment;
  }

}