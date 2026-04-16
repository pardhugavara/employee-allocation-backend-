package com.statestreet.resourceallocation.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.statestreet.resourceallocation.enums.EmployeeStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeResponse {
    private Long id;
    private String fullName;
    private String email;
    private String mobile;
    private String address;
    private Integer experienceYears;
    private boolean profileCompleted;
    private EmployeeStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private List<PreviousExperienceDto> previousExperience;
    private List<SkillExperienceDto> skillExperiences;
    private List<CertificationDto> certifications;
    private String currentProjectName;
    private List<ProjectExperience> projectHistory;
    private String profileToken; // returned only on creation for testing
}
