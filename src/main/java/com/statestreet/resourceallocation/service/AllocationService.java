package com.statestreet.resourceallocation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.statestreet.resourceallocation.dto.AllocationResponse;
import com.statestreet.resourceallocation.dto.CreateAllocationRequest;
import com.statestreet.resourceallocation.entity.Allocation;
import com.statestreet.resourceallocation.entity.Employee;
import com.statestreet.resourceallocation.entity.Project;
import com.statestreet.resourceallocation.enums.EmployeeStatus;
import com.statestreet.resourceallocation.exception.ResourceNotFoundException;
import com.statestreet.resourceallocation.entity.EmployeeProfileToken;
import com.statestreet.resourceallocation.repository.AllocationRepository;
import com.statestreet.resourceallocation.repository.EmployeeProfileTokenRepository;
import com.statestreet.resourceallocation.repository.EmployeeRepository;
import com.statestreet.resourceallocation.repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeProfileTokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${app.token.expiry-hours:48}")
    private int tokenExpiryHours;

    @Transactional
    public AllocationResponse createAllocation(CreateAllocationRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Deactivate existing allocation
        allocationRepository.findByEmployeeIdAndActiveTrue(employee.getId())
                .ifPresent(existing -> {
                    existing.setActive(false);
                    allocationRepository.save(existing);
                });

        Allocation allocation = Allocation.builder()
                .employee(employee)
                .project(project)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .allocationPercentage(request.getAllocationPercentage() != null ? request.getAllocationPercentage() : 100)
                .active(true)
                .build();

        // Update employee status to IN_PROGRESS if PENDING
        if (employee.getStatus() == EmployeeStatus.PENDING) {
            employee.setStatus(EmployeeStatus.IN_PROGRESS);
            employeeRepository.save(employee);
        }

        allocation = allocationRepository.save(allocation);

        // Generate token and send profile email with project name
        String token = UUID.randomUUID().toString();
        EmployeeProfileToken profileToken = EmployeeProfileToken.builder()
                .token(token)
                .employee(employee)
                .expiryDate(LocalDateTime.now().plusHours(tokenExpiryHours))
                .used(false)
                .build();
        tokenRepository.save(profileToken);

        emailService.sendProfileLink(employee.getEmail(), employee.getFullName(), token, project.getName());

        return mapToResponse(allocation);
    }

    public List<AllocationResponse> getAllAllocations() {
        return allocationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AllocationResponse> getAllocationsByEmployee(Long employeeId) {
        return allocationRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AllocationResponse mapToResponse(Allocation allocation) {
        return AllocationResponse.builder()
                .id(allocation.getId())
                .employeeId(allocation.getEmployee().getId())
                .employeeName(allocation.getEmployee().getFullName())
                .projectId(allocation.getProject().getId())
                .projectName(allocation.getProject().getName())
                .startDate(allocation.getStartDate())
                .endDate(allocation.getEndDate())
                .active(allocation.isActive())
                .allocationPercentage(allocation.getAllocationPercentage())
                .build();
    }
}
