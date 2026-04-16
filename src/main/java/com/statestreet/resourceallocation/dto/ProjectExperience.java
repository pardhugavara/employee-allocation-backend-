package com.statestreet.resourceallocation.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectExperience {
    private Long projectId;
    private String projectName;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer allocationPercentage;
    private boolean active;
}
