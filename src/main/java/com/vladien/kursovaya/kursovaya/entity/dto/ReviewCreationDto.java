package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Data;

@Data
public class ReviewCreationDto {
    private int rating;
    private String comment;
}
