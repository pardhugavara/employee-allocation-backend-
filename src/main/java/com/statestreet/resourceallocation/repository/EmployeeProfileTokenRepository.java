package com.statestreet.resourceallocation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.statestreet.resourceallocation.entity.EmployeeProfileToken;

@Repository
public interface EmployeeProfileTokenRepository extends JpaRepository<EmployeeProfileToken, Long> {
    Optional<EmployeeProfileToken> findByToken(String token);
    Optional<EmployeeProfileToken> findByEmployeeId(Long employeeId);

    @Modifying
    void deleteByEmployeeId(Long employeeId);
}
