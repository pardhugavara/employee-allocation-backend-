package com.statestreet.resourceallocation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.statestreet.resourceallocation.dto.CreateSkillRequest;
import com.statestreet.resourceallocation.dto.SkillResponse;
import com.statestreet.resourceallocation.entity.Skill;
import com.statestreet.resourceallocation.exception.DuplicateResourceException;
import com.statestreet.resourceallocation.repository.SkillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillResponse createSkill(CreateSkillRequest request) {
        if (skillRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Skill already exists: " + request.getName());
        }

        Skill skill = Skill.builder()
                .name(request.getName())
                .category(request.getCategory())
                .build();

        skill = skillRepository.save(skill);
        return mapToResponse(skill);
    }

    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .build();
    }
}
