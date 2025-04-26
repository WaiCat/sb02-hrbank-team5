package com.hrbank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "established_date", nullable = false)
    private LocalDate establishedDate;

    // 단방향 1:N 관계 (Department -> Employee)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private List<Employee> employees= new ArrayList<>();

    public Department(String name, String description, LocalDate establishedDate) {
        this.name = name;
        this.description = description;
        this.establishedDate = establishedDate;
    }

    public void update(String name, String description, LocalDate establishedDate) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
        }
        if (description != null && !description.equals(this.description)) {
            this.description = description;
        }
        if (establishedDate != null && !establishedDate.equals(this.establishedDate)) {
            this.establishedDate = establishedDate;
        }
    }
}
