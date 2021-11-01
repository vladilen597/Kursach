package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.entity.Review;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.ReviewCreationDto;
import com.vladien.kursovaya.kursovaya.entity.dto.ReviewRepresentationDto;
import com.vladien.kursovaya.kursovaya.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public ReviewRepresentationDto rateUser(String reviewedUsername, ReviewCreationDto reviewDto, User author) {
        User reviewedUser = userService.loadUserByUsername(reviewedUsername);
        Review review = modelMapper.map(reviewDto, Review.class);
        review.setAuthorOfRating(author);
        review.setPersonRated(reviewedUser);
        return modelMapper.map(reviewRepository.save(review), ReviewRepresentationDto.class);
    }
}
