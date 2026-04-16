package com.statestreet.resourceallocation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.statestreet.resourceallocation.dto.CreateProjectRequest;
import com.statestreet.resourceallocation.dto.ProjectResponse;
import com.statestreet.resourceallocation.entity.Project;
import com.statestreet.resourceallocation.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectResponse createProject(CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .clientName(request.getClientName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(true)
                .build();

        project = projectRepository.save(project);
        return mapToResponse(project);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .clientName(project.getClientName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .active(project.isActive())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
