package com.statestreet.resourceallocation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProjectRequest {
    @NotBlank(message = "Project name is required")
    private String name;
    private String description;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
}
