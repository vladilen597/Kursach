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
import java.util.NoSuchElementException;

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

    @GetMapping("/{mentorName}/active")
    public List<TrainingCourseRepresentation> showMentorCoursesWithStudents(@PathVariable String mentorName) {
        return trainingCourseService.findCoursesByMentorWithStudents(mentorName);
    }

    @GetMapping("/current/active")
    public List<TrainingCourseRepresentation> showCurrentMentorCoursesWithStudents() {
        String mentorName = principalDefiner.currentUsername();
        return trainingCourseService.findCoursesByMentorWithStudents(mentorName);
    }

    @PostMapping("/current")
    @Secured("ROLE_MENTOR")
    public TrainingCourseRepresentation addMentorCourse(@RequestBody TrainingCourseCreationDto trainingCourseCreationDto) {
        String mentorName = principalDefiner.currentUsername();
        return trainingCourseService.addCourse(mentorName, trainingCourseCreationDto);
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
        return trainingCourseService.findUserRequests(principal.getUsername());
    }

    @GetMapping("/students/requests")
    @Secured("ROLE_MENTOR")
    public List<TrainingRequestRepresentation> showStudentsRequests() {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.findRequestsToMentor(principal);
    }

    @GetMapping("/enrolled")
    @Secured("ROLE_CLIENT")
    public List<TrainingCourseRepresentation> showCoursesWhereStudentISEnrolled() {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.findCoursesByEnrolledStudent(principal);
    }

    @GetMapping("/enrolled/{studentName}")
    @Secured("ROLE_CLIENT")
    public List<TrainingCourseRepresentation> showCoursesWhereStudentISEnrolled(@PathVariable String studentName) {
        return trainingCourseService.findCoursesByEnrolledStudent(studentName);
    }

    @PostMapping("/students/request/{requestId}/approve")
    @Secured("ROLE_MENTOR")
    public List<TrainingRequestRepresentation> approveTrainingStart(@PathVariable String requestId) {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.approveTraining(principal, requestId);
    }

    @PostMapping("/students/request/{requestId}/disapprove")
    @Secured("ROLE_MENTOR")
    public List<TrainingRequestRepresentation> disapproveTrainingStart(@PathVariable String requestId) {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.disapproveTraining(principal, requestId);
    }

    @PostMapping("/students/training/{courseId}/finish/{studentId}")
    @Secured("ROLE_MENTOR")
    public TrainingCourseRepresentation finishTraining(@PathVariable String studentId, @PathVariable String courseId) {
        User principal = principalDefiner.getPrincipal();
        return trainingCourseService.finishTraining(principal, courseId, studentId);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ResponseEntity<String>> handleJwtValidationExceptions(JwtAuthenticationException ex) {
        return ResponseEntity.status(401).body(ResponseEntity.status(401).body("Your session token is expired or invalid. Sign in to continue"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseEntity<String>> handleTakenWrongUserInputExceptions(IllegalArgumentException ex) {
        return ResponseEntity.status(403).body(ResponseEntity.status(403).body(ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseEntity<String>> handleNotFoundEntityExceptions(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ResponseEntity.status(404).body("Can't find data which you are requesting"));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseEntity<String>> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(404).body(ResponseEntity.status(404).body("Can't find data which you are requesting"));
    }
}
