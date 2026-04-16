package com.statestreet.resourceallocation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.statestreet.resourceallocation.dto.CreateEmployeeRequest;
import com.statestreet.resourceallocation.dto.EmployeeResponse;
import com.statestreet.resourceallocation.dto.TokenValidationRequest;
import com.statestreet.resourceallocation.dto.UpdateProfileRequest;
import com.statestreet.resourceallocation.dto.UpdateStatusRequest;
import com.statestreet.resourceallocation.enums.EmployeeStatus;
import com.statestreet.resourceallocation.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(request));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByStatus(@PathVariable EmployeeStatus status) {
        return ResponseEntity.ok(employeeService.getEmployeesByStatus(status));
    }

    @PostMapping("/profile/validate-token")
    public ResponseEntity<EmployeeResponse> validateToken(@RequestBody TokenValidationRequest request) {
        return ResponseEntity.ok(employeeService.validateToken(request.getToken()));
    }

    @PutMapping("/profile/update")
    public ResponseEntity<EmployeeResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(employeeService.updateProfile(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EmployeeResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(employeeService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
