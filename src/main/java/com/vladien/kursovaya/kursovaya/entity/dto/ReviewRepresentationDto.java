package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Data;

@Data
public class ReviewRepresentationDto {
    private String comment;
    private int rating;
    private String authorName;
    private String receiverName;
}
