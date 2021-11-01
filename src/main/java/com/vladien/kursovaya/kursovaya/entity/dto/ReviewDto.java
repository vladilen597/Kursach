package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private int rating;
    private String comment;
    private String reviewAuthorUsername;
}
