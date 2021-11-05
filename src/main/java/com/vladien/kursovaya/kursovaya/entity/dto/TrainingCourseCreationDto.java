package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Data;

@Data
public class TrainingCourseCreationDto {
    private String courseName;
    private String description;
    private String skillLevel;
}
