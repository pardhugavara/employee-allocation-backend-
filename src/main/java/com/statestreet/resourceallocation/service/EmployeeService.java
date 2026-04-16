package com.statestreet.resourceallocation.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.statestreet.resourceallocation.dto.CertificationDto;
import com.statestreet.resourceallocation.dto.CreateEmployeeRequest;
import com.statestreet.resourceallocation.dto.EmployeeResponse;
import com.statestreet.resourceallocation.dto.PreviousExperienceDto;
import com.statestreet.resourceallocation.dto.ProjectExperience;
import com.statestreet.resourceallocation.dto.SkillExperienceDto;
import com.statestreet.resourceallocation.dto.UpdateProfileRequest;
import com.statestreet.resourceallocation.entity.Allocation;
import com.statestreet.resourceallocation.entity.Employee;
import com.statestreet.resourceallocation.entity.EmployeeProfileToken;
import com.statestreet.resourceallocation.enums.EmployeeStatus;
import com.statestreet.resourceallocation.exception.DuplicateResourceException;
import com.statestreet.resourceallocation.exception.InvalidTokenException;
import com.statestreet.resourceallocation.exception.ResourceNotFoundException;
import com.statestreet.resourceallocation.exception.TokenExpiredException;
import com.statestreet.resourceallocation.repository.AllocationRepository;
import com.statestreet.resourceallocation.repository.EmployeeProfileTokenRepository;
import com.statestreet.resourceallocation.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeProfileTokenRepository tokenRepository;
    private final AllocationRepository allocationRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Value("${app.token.expiry-hours:48}")
    private int tokenExpiryHours;

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        Employee employee = Employee.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .createdBy(request.getCreatedBy())
                .status(EmployeeStatus.PENDING)
                .profileCompleted(false)
                .build();

        employee = employeeRepository.save(employee);

        return mapToResponse(employee);
    }

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<EmployeeResponse> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    @Transactional
    public EmployeeResponse validateToken(String token) {
        EmployeeProfileToken profileToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (profileToken.isUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }

        if (profileToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token has expired");
        }

        return mapToResponse(profileToken.getEmployee());
    }

    @Transactional
    public EmployeeResponse updateProfile(UpdateProfileRequest request) {
        EmployeeProfileToken profileToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (profileToken.isUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }

        if (profileToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token has expired");
        }

        Employee employee = profileToken.getEmployee();
        employee.setMobile(request.getMobile());
        employee.setAddress(request.getAddress());
        employee.setExperienceYears(request.getExperienceYears());
        employee.setProfileCompleted(true);
        employee.setStatus(EmployeeStatus.IN_PROGRESS);

        // Save JSON fields
        try {
            if (request.getPreviousExperience() != null) {
                employee.setPreviousExperience(objectMapper.writeValueAsString(request.getPreviousExperience()));
            }
            if (request.getSkillExperiences() != null) {
                employee.setSkillExperiences(objectMapper.writeValueAsString(request.getSkillExperiences()));
            }
            if (request.getCertifications() != null) {
                employee.setCertifications(objectMapper.writeValueAsString(request.getCertifications()));
            }
        } catch (Exception e) {
            log.error("Failed to serialize skill experiences or certifications", e);
        }

        employee = employeeRepository.save(employee);

        // Mark token as used
        profileToken.setUsed(true);
        tokenRepository.save(profileToken);

        return mapToResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateStatus(Long id, EmployeeStatus status) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setStatus(status);
        employee = employeeRepository.save(employee);
        return mapToResponse(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (employee.getStatus() != EmployeeStatus.PENDING) {
            throw new IllegalStateException("Only employees in PENDING status can be deleted");
        }

        // Delete related tokens and allocations
        tokenRepository.deleteByEmployeeId(id);
        allocationRepository.deleteByEmployeeId(id);

        // Delete the employee
        employeeRepository.delete(employee);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        // Find active allocation for current project
        String currentProjectName = null;
        Optional<Allocation> activeAllocation = allocationRepository.findByEmployeeIdAndActiveTrue(employee.getId());
        if (activeAllocation.isPresent()) {
            currentProjectName = activeAllocation.get().getProject().getName();
        }

        // Build full project history (all allocations – past and present)
        List<Allocation> allAllocations = allocationRepository.findByEmployeeId(employee.getId());
        List<ProjectExperience> projectHistory = allAllocations.stream()
                .map(a -> ProjectExperience.builder()
                        .projectId(a.getProject().getId())
                        .projectName(a.getProject().getName())
                        .clientName(a.getProject().getClientName())
                        .startDate(a.getStartDate())
                        .endDate(a.getEndDate())
                        .allocationPercentage(a.getAllocationPercentage())
                        .active(a.isActive())
                        .build())
                .collect(Collectors.toList());

        return EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .mobile(employee.getMobile())
                .address(employee.getAddress())
                .experienceYears(employee.getExperienceYears())
                .profileCompleted(employee.isProfileCompleted())
                .status(employee.getStatus())
                .createdBy(employee.getCreatedBy())
                .createdAt(employee.getCreatedAt())
                .previousExperience(parsePreviousExperience(employee.getPreviousExperience()))
                .skillExperiences(parseSkillExperiences(employee.getSkillExperiences()))
                .certifications(parseCertifications(employee.getCertifications()))
                .currentProjectName(currentProjectName)
                .projectHistory(projectHistory)
                .build();
    }

    private List<SkillExperienceDto> parseSkillExperiences(String json) {
        if (json == null || json.isBlank()) {
			return Collections.emptyList();
		}
        try {
            return objectMapper.readValue(json, new TypeReference<List<SkillExperienceDto>>() {});
        } catch (Exception e) {
            log.error("Failed to parse skill experiences JSON", e);
            return Collections.emptyList();
        }
    }

    private List<CertificationDto> parseCertifications(String json) {
        if (json == null || json.isBlank()) {
			return Collections.emptyList();
		}
        try {
            return objectMapper.readValue(json, new TypeReference<List<CertificationDto>>() {});
        } catch (Exception e) {
            log.error("Failed to parse certifications JSON", e);
            return Collections.emptyList();
        }
    }

    private List<PreviousExperienceDto> parsePreviousExperience(String json) {
        if (json == null || json.isBlank()) {
			return Collections.emptyList();
		}
        try {
            return objectMapper.readValue(json, new TypeReference<List<PreviousExperienceDto>>() {});
        } catch (Exception e) {
            log.error("Failed to parse previous experience JSON", e);
            return Collections.emptyList();
        }
    }
}
