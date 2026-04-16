package com.statestreet.resourceallocation.dto;

import com.statestreet.resourceallocation.enums.EmployeeStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotNull(message = "Status is required")
    private EmployeeStatus status;
}
