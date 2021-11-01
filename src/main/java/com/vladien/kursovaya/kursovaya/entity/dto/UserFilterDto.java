package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserFilterDto {
    private Integer rating;
    private List<String> coreSkills;
}
