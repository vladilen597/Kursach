package com.vladien.kursovaya.kursovaya.service.util;

import com.vladien.kursovaya.kursovaya.entity.CoreSkill;
import com.vladien.kursovaya.kursovaya.entity.Review;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.UserRepresentationDto;
import com.vladien.kursovaya.kursovaya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepresentationUtil {
    private final UserRepository userRepository;

    public UserRepresentationDto defineUserRepresentation(String username) {
        User user = userRepository.findByUsername(username);
        double middleMark = getMiddleMark(user);
        List<CoreSkill> coreSkills = user.getCoreSkills();
        if (coreSkills == null) {
            coreSkills = new ArrayList<>();
        }
        UserRepresentationDto representationDto = new UserRepresentationDto();
        representationDto.setFirstName(user.getFirstName());
        representationDto.setLastName(user.getLastName());
        representationDto.setPatronymic(user.getPatronymic());
        representationDto.setAverageRating(middleMark);
        representationDto.setId(user.getId());
        representationDto.setUsername(username);
        representationDto.setProfilePicture(user.getProfilePicture());
        representationDto.setSkills(coreSkills.stream().map(CoreSkill::getName).collect(Collectors.toSet()));
        return representationDto;
    }

    public double getMiddleMark(User user) {
        Set<Review> reviews = user.getReceivedReviews();
        if (reviews == null) {
            reviews = new HashSet<>();
        }
        OptionalDouble optMiddleMark = reviews.stream().mapToInt(Review::getRating).average();
        double middleMark;
        if (optMiddleMark.isEmpty()) {
            middleMark = 0;
        } else {
            middleMark = optMiddleMark.getAsDouble();
        }
        return middleMark;
    }
}
