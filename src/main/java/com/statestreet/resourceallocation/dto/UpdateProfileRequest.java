package com.statestreet.resourceallocation.dto;

import java.util.List;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String token;
    private String mobile;
    private String address;
    private Integer experienceYears;
    private List<SkillExperienceDto> skillExperiences;
    private List<CertificationDto> certifications;
    private List<PreviousExperienceDto> previousExperience;
}
