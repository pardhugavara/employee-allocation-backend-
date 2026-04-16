package com.statestreet.resourceallocation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAllocationRequest {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer allocationPercentage;
}
