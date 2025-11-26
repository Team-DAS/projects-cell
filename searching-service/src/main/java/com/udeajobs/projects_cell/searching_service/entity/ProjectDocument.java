package com.udeajobs.projects_cell.searching_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDocument {

    private String id;

    private String projectId;

    private String employerId;

    private String title;

    private String description;

    private String status;

    private Double minSalary;

    private Double maxSalary;

    private String currency;

    private String location;

    private Boolean isRemote;

    private List<String> requiredSkills;

    private String jobLevel;

    private String mainCategory;

    private List<String> tags;

    private LocalDateTime indexedAt;
}
