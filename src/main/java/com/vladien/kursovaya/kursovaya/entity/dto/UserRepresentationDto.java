package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRepresentationDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String profilePicture;
    private String username;
    private Set<String> skills;
    private double averageRating;
}
