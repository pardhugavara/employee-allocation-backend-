package com.statestreet.resourceallocation.entity;

import com.statestreet.resourceallocation.enums.EmployeeStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String mobile;
    private String address;
    private Integer experienceYears;

    @Builder.Default
    private boolean profileCompleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.PENDING;

    @Column(nullable = false)
    private String createdBy;

    @Column(columnDefinition = "JSON")
    private String previousExperience;

    @Column(columnDefinition = "TEXT")
    private String skillExperiences;

    @Column(columnDefinition = "TEXT")
    private String certifications;
}
