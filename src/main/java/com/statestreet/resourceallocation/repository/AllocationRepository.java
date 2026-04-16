package com.statestreet.resourceallocation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.statestreet.resourceallocation.entity.Allocation;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    List<Allocation> findByEmployeeId(Long employeeId);
    List<Allocation> findByProjectId(Long projectId);
    Optional<Allocation> findByEmployeeIdAndActiveTrue(Long employeeId);

    @Modifying
    void deleteByEmployeeId(Long employeeId);
}
