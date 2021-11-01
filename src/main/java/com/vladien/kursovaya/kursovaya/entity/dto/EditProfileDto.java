package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class EditProfileDto {
    private String username;
    private List<String> coreSkills;
    private String aboutMe;
}
