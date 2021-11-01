package com.vladien.kursovaya.kursovaya.entity.dto;

import com.vladien.kursovaya.kursovaya.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileRepresentation {
    private Long id;
    private String profilePicture;
    private String username;
    private Set<String> skillsNames;
    private Set<ReviewDto> receivedReviewsDtos;
    private Set<ReviewDto> givenReviewsDtos;
    private String aboutMe;
}
