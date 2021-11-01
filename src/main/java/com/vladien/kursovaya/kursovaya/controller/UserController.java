package com.vladien.kursovaya.kursovaya.controller;

import com.vladien.kursovaya.kursovaya.controller.util.CurrentPrincipalDefiner;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.*;
import com.vladien.kursovaya.kursovaya.security.JwtAuthenticationException;
import com.vladien.kursovaya.kursovaya.service.ReviewService;
import com.vladien.kursovaya.kursovaya.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
// TODO: 10/27/2021 пикчи на профили,
//  проблемы - фильтрация(скорее всего из-за рейтинга)
//  вывод списка студентов с инфой о средней оценке
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final CurrentPrincipalDefiner principalDefiner;
    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/{username}")
    public ProfileRepresentation showUserById(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PostMapping("/{username}")
    public ProfileRepresentation leaveRating(@PathVariable String username, @RequestBody ReviewCreationDto ratingDto) {
        User principal = principalDefiner.getPrincipal();
        reviewService.rateUser(username, ratingDto, principal);
        return userService.findByUsername(username);
    }

    @GetMapping("/students")
    public List<UserRepresentationDto> showAllStudents() {
        return userService.findAllStudents();
    }

    @GetMapping("/mentors")
    public List<UserRepresentationDto> showAllMentors() {
        return userService.findAllMentors();
    }

    @GetMapping("/students/trainees")
    @Secured("ROLE_MENTOR")
    public List<UserRepresentationDto> showMentorStudents() {
        User principal = principalDefiner.getPrincipal();
        return userService.findMentorTrainees(principal);
    }

    @GetMapping("/students/trainees/unapproved")
    @Secured("ROLE_MENTOR")
    public List<UserRepresentationDto> showMentorPendingStudents() {
        User principal = principalDefiner.getPrincipal();
        return userService.findMentorUnapprovedTrainees(principal);
    }

    @PostMapping("/students/filter")
    public List<UserRepresentationDto> showAllStudentsFiltered(@RequestBody UserFilterDto filterDto) {
        return userService.filterStudents(filterDto.getRating(), filterDto.getCoreSkills());
    }

    @PostMapping("/mentors/filter")
    public List<UserRepresentationDto> showAllMentorsFiltered(@RequestBody UserFilterDto filterDto) {
        return userService.filterMentors(filterDto.getRating(), filterDto.getCoreSkills());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseMessage> handleTakenUsernameExceptions(IllegalArgumentException ex) {
        return ResponseEntity.status(403).body(new ResponseMessage(ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<String> handleValidationExceptions(JwtAuthenticationException ex) {
        return ResponseEntity.status(401).body("Your session token is expired or invalid. Sign in to continue");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundEntityExceptions(JwtAuthenticationException ex) {
        return ResponseEntity.status(404).body("Can't find data which you are requesting");
    }
}
