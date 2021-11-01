package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.service.util.UserRepresentationUtil;
import com.vladien.kursovaya.kursovaya.entity.CoreSkill;
import com.vladien.kursovaya.kursovaya.entity.Review;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.UserRole;
import com.vladien.kursovaya.kursovaya.entity.dto.EditProfileDto;
import com.vladien.kursovaya.kursovaya.entity.dto.ProfileRepresentation;
import com.vladien.kursovaya.kursovaya.entity.dto.ReviewDto;
import com.vladien.kursovaya.kursovaya.entity.dto.UserRepresentationDto;
import com.vladien.kursovaya.kursovaya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final CoreSkillsService coreSkillsService;
    private final ModelMapper modelMapper;
    private final UserRepresentationUtil representationUtil;

    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public ProfileRepresentation findByUsername(String username) {
        if (isUsernameFree(username)) {
            throw new IllegalArgumentException("No user with such name exists");
        }
        User user = userRepository.findByUsername(username);
        ProfileRepresentation profile = modelMapper.map(user, ProfileRepresentation.class);
        profile.setReceivedReviewsDtos(
                user.getReceivedReviews().stream().map(this::transformReviewToNonDatabaseDto).collect(Collectors.toSet())
        );
        profile.setGivenReviewsDtos(
                user.getGivenReviews().stream().map(this::transformReviewToNonDatabaseDto).collect(Collectors.toSet())
        );
        profile.setSkillsNames(user.getCoreSkills().stream().map(CoreSkill::getName).collect(Collectors.toSet()));
        return profile;
    }

    public ProfileRepresentation updateUserImage(User user, String fileName) {
        user.setProfilePicture(fileName);
        user = userRepository.save(user);
        ProfileRepresentation profile = modelMapper.map(user, ProfileRepresentation.class);
        profile.setReceivedReviewsDtos(
                user.getReceivedReviews().stream().map(this::transformReviewToNonDatabaseDto).collect(Collectors.toSet())
        );
        profile.setGivenReviewsDtos(
                user.getGivenReviews().stream().map(this::transformReviewToNonDatabaseDto).collect(Collectors.toSet())
        );
        return profile;
    }

    public ProfileRepresentation editProfile(String usernameOfEdited, EditProfileDto dto) {
        User user = userRepository.findByUsername(usernameOfEdited);
        List<CoreSkill> coreSkills = coreSkillsService.transformSkillNamesToSkillsWithSaving(dto.getCoreSkills(), user);
        user.setAboutMe(dto.getAboutMe());
        if (!user.getUsername().equals(dto.getUsername())) {
            if (isUsernameFree(dto.getUsername())) {
                user.setUsername(dto.getUsername());
            } else {
                throw new IllegalArgumentException("This username is taken!");
            }
        }
        user.setCoreSkills(coreSkills);
        userRepository.save(user);
        ProfileRepresentation profile = modelMapper.map(user, ProfileRepresentation.class);
        profile.setReceivedReviewsDtos(
                user.getReceivedReviews().stream().map(this::transformReviewToNonDatabaseDto).collect(Collectors.toSet())
        );
        profile.setGivenReviewsDtos(
                user.getGivenReviews().stream().map(this::transformReviewToNonDatabaseDto).collect(Collectors.toSet())
        );
        profile.setSkillsNames(user.getCoreSkills().stream().map(CoreSkill::getName).collect(Collectors.toSet()));
        return profile;
    }

    public List<UserRepresentationDto> findAllStudents() {
        return transformListOfUsersToRepresentations(userRepository.findAllByRolesContains(UserRole.ROLE_CLIENT));

    }

    public List<UserRepresentationDto> findAllMentors() {
        return transformListOfUsersToRepresentations(userRepository.findAllByRolesContains(UserRole.ROLE_MENTOR));
    }

    public List<UserRepresentationDto> filterStudents(int rating, List<String> skills) {
        System.out.println(skills);
        List<CoreSkill> coreSkills = coreSkillsService.transformSkillNamesToSkills(skills);
        return transformListOfUsersToRepresentations(filterAllSpecifiedRoleUsers(rating, UserRole.ROLE_CLIENT, coreSkills));
    }

    public List<UserRepresentationDto> filterMentors(int rating, List<String> skills) {
        List<CoreSkill> coreSkills = coreSkillsService.transformSkillNamesToSkills(skills);
        return filterAllSpecifiedRoleUsers(rating, UserRole.ROLE_MENTOR, coreSkills)
                .stream()
                .map(user -> representationUtil.defineUserRepresentation(user.getUsername()))
                .collect(Collectors.toList());
    }

    public ProfileRepresentation findCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username);
        ProfileRepresentation profile = modelMapper.map(user, ProfileRepresentation.class);
        profile.setReceivedReviewsDtos(
                user.getReceivedReviews().stream().map(this::transformReviewToNonDatabaseDto).collect(Collectors.toSet())
        );
        profile.setGivenReviewsDtos(
                user.getGivenReviews().stream().map(this::transformReviewToNonDatabaseDto).collect(Collectors.toSet())
        );
        profile.setSkillsNames(user.getCoreSkills().stream().map(CoreSkill::getName).collect(Collectors.toSet()));
        return profile;
    }

    public List<UserRepresentationDto> findMentorTrainees(User mentor) {
        return userRepository.findById(mentor.getId())
                .map(User::getStudents)
                .orElse(new ArrayList<>())
                .stream()
                .map(student -> representationUtil.defineUserRepresentation(student.getUsername()))
                .collect(Collectors.toList());
    }

    public List<UserRepresentationDto> findMentorUnapprovedTrainees(User mentor) {
        return userRepository.findById(mentor.getId())
                .map(User::getTrainingRequests)
                .orElse(new ArrayList<>())
                .stream()
                .map(request -> userRepository.findByUsername(request.getRequester().getUsername()))
                .map(student -> representationUtil.defineUserRepresentation(student.getUsername()))
                .collect(Collectors.toList());
    }

    private List<User> filterAllSpecifiedRoleUsers(int rating, UserRole role, List<CoreSkill> skills) {
        return userRepository
                .findAllByRolesContains(role)
                .stream()
                .filter(user -> user.getReceivedReviews()
                        .stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0) >= rating
                )
                .filter(user -> user.getCoreSkills().containsAll(skills))
                .collect(Collectors.toList());
    }

    private List<UserRepresentationDto> transformListOfUsersToRepresentations(List<User> users) {
        return users.stream()
                .map(user -> representationUtil.defineUserRepresentation(user.getUsername()))
                .collect(Collectors.toList());
    }

    private ReviewDto transformReviewToNonDatabaseDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setComment(review.getComment());
        reviewDto.setRating(review.getRating());
        reviewDto.setReviewAuthorUsername(review.getAuthorOfRating().getUsername());
        return reviewDto;
    }

    private boolean isUsernameFree(String username) {
        return isNull(userRepository.findByUsername(username));
    }
}
