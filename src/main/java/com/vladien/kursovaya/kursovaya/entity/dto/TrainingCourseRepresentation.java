package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class TrainingCourseRepresentation {
    private Long id;
    private String courseName;
    private String description;
    private String skillLevel;
    private UserRepresentationDto mentorName;
    private Set<UserRepresentationDto> students;
}
