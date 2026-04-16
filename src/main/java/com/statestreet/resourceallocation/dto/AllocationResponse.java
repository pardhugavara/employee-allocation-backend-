package com.statestreet.resourceallocation.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllocationResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long projectId;
    private String projectName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private Integer allocationPercentage;
}
