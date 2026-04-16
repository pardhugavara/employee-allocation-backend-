package com.statestreet.resourceallocation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.statestreet.resourceallocation.entity.Employee;
import com.statestreet.resourceallocation.enums.EmployeeStatus;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByStatus(EmployeeStatus status);
    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);
}
