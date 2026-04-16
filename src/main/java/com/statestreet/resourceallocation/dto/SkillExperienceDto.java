package com.statestreet.resourceallocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillExperienceDto {
    private String skillName;
    private Integer years;
    private Integer months;
}
