package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class EditProfileDto {
    private String firstName;
    private String lastName;
    private String patronymic;
    private List<String> coreSkills;
    private String aboutMe;
}
