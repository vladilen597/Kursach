package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TrainingCourseRepresentation {
    private Long id;
    private String courseName;
    private String description;
    private String skillLevel;
    private UserRepresentationDto mentorName;
    private List<UserRepresentationDto> students;
}
