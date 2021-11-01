package com.vladien.kursovaya.kursovaya.controller;

import com.vladien.kursovaya.kursovaya.controller.util.CurrentPrincipalDefiner;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.*;
import com.vladien.kursovaya.kursovaya.security.JwtAuthenticationException;
import com.vladien.kursovaya.kursovaya.service.TrainingCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class TrainingCourseController {
    private final TrainingCourseService trainingCourseService;
    private final CurrentPrincipalDefiner principalDefiner;

    @GetMapping("/{mentorName}")
    public List<TrainingCourseRepresentation> showMentorCourses(@PathVariable String mentorName) {
        return trainingCourseService.findCoursesByMentor(mentorName);
    }

    @GetMapping("/current")
    public List<TrainingCourseRepresentation> showCurrentMentorCourses() {
        String mentorName = principalDefiner.currentUsername();
        return trainingCourseService.findCoursesByMentor(mentorName);
    }

    @PostMapping("/current")
    @Secured("ROLE_MENTOR")
    public TrainingCourseRepresentation addMentorCourse(@RequestBody TextDto courseName) {
        String mentorName = principalDefiner.currentUsername();
        return trainingCourseService.addCourse(mentorName, courseName.getText());
    }

    @DeleteMapping("/current")
    @Secured("ROLE_MENTOR")
    public List<TrainingCourseRepresentation> deleteMentorCourses(@RequestBody IdDto courseId) {
        String mentorName = principalDefiner.currentUsername();
        return trainingCourseService.deleteCourse(mentorName, courseId.getId());
    }

    @PostMapping("/{mentorName}")
    @Secured("ROLE_CLIENT")
    public TrainingRequestRepresentation signForTraining(@RequestBody IdDto idDto, @PathVariable String mentorName) {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.signForTraining(principal, mentorName, idDto.getId());
    }

    @GetMapping("/requests")
    @Secured("ROLE_CLIENT")
    public List<TrainingRequestRepresentation> viewCurrentUserRequests() {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.findUserRequests(principal.getUsername());
    }

    @GetMapping("/requests/approved")
    @Secured("ROLE_CLIENT")
    public List<TrainingRequestRepresentation> viewCurrentUserApprovedRequests() {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.findUserApprovedRequests(principal.getUsername());
    }

    @GetMapping("/requests/unapproved")
    @Secured("ROLE_CLIENT")
    public List<TrainingRequestRepresentation> viewCurrentUserUnapprovedRequests() {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.findUserUnapprovedRequests(principal.getUsername());
    }

    @GetMapping("/students/requests")
    @Secured("ROLE_MENTOR")
    public List<TrainingRequestRepresentation> showStudentsRequests() {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.findRequestsToMentor(principal);
    }

    @PostMapping("/students/request/{requestId}/approve")
    @Secured("ROLE_MENTOR")
    public List<TrainingRequestRepresentation> approveTrainingStart(@PathVariable String requestId) {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.approveTraining(principal, requestId);
    }

    @PostMapping("/students/training/{courseId}/finish/{trainingId}")
    @Secured("ROLE_MENTOR")
    public TrainingCourseRepresentation finishTraining(@PathVariable String trainingId, @PathVariable String courseId) {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.finishTraining(principal, trainingId, courseId);
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
