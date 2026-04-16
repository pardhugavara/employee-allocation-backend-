package com.statestreet.resourceallocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviousExperienceDto {
    private String companyName;
    private String projectName;
    private String fromDate;
    private String toDate;
    private String description;
}
