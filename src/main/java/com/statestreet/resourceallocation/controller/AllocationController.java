package com.statestreet.resourceallocation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.statestreet.resourceallocation.dto.AllocationResponse;
import com.statestreet.resourceallocation.dto.CreateAllocationRequest;
import com.statestreet.resourceallocation.service.AllocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    @PostMapping
    public ResponseEntity<AllocationResponse> createAllocation(@Valid @RequestBody CreateAllocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(allocationService.createAllocation(request));
    }

    @GetMapping
    public ResponseEntity<List<AllocationResponse>> getAllAllocations() {
        return ResponseEntity.ok(allocationService.getAllAllocations());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AllocationResponse>> getAllocationsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(allocationService.getAllocationsByEmployee(employeeId));
    }
}
