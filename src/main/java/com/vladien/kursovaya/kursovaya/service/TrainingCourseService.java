package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.entity.*;
import com.vladien.kursovaya.kursovaya.entity.dto.*;
import com.vladien.kursovaya.kursovaya.service.util.UserRepresentationUtil;
import com.vladien.kursovaya.kursovaya.repository.TrainingCourseRepository;
import com.vladien.kursovaya.kursovaya.repository.TrainingRequestRepository;
import com.vladien.kursovaya.kursovaya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingCourseService {
    private final TrainingCourseRepository trainingCourseRepository;
    private final UserRepository userRepository;
    private final TrainingRequestRepository trainingRequestRepository;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final UserRepresentationUtil representationUtil;
    private final ModelMapper modelMapper;

    public TrainingRequestRepresentation signForTraining(User assigner, String mentorName, Long courseId) {
        TrainingCourse trainingCourse = trainingCourseRepository.getById(courseId);
        if(trainingCourse.getActiveStudents().contains(assigner)) {
            throw new IllegalArgumentException("You are already enrolled for this course");
        }
        if (trainingCourse.getRequests()
                .stream()
                .map(TrainingRequest::getRequester)
                .anyMatch(requester -> requester.equals(assigner))) {
            throw new IllegalArgumentException("You have already sent a request for this course");
        }
        ChatRoom createdChat = chatRoomService.createChatRoom(assigner, mentorName);
        messageService.createMessage(assigner, String.valueOf(createdChat.getId()), new TextDto("Hello, I want to sign for one of your trainings!!!"));
        TrainingRequest request = TrainingRequest
                .builder()
                .trainingCourse(trainingCourse)
                .creationDateTime(LocalDateTime.now())
                .requester(assigner)
                .isApproved(false)
                .mentor(trainingCourse.getOwner())
                .build();
        User currentUser = userRepository.findByUsername(assigner.getUsername());
        currentUser.getTrainingRequests().add(request);
        userRepository.save(currentUser);
        TrainingRequest savedRequest = trainingRequestRepository.save(request);
        return getTrainingRequestRepresentation(assigner, mentorName, savedRequest);
    }

    public List<TrainingRequestRepresentation> findRequestsToMentor(User mentor) {
        List<TrainingRequest> requests = trainingRequestRepository.findAllByMentor(mentor);
        return requests
                .stream()
                .map(request -> getTrainingRequestRepresentation(request.getRequester(), mentor.getUsername(), request))
                .collect(Collectors.toList());
    }

    public List<TrainingCourseRepresentation> findCoursesByEnrolledStudent(User student) {
        return trainingCourseRepository.findAllByActiveStudentsContains(student).stream()
                .map(training -> getTrainingCourseRepresentation(training, training.getCourseName(), student))
                .collect(Collectors.toList());
    }

    public List<TrainingCourseRepresentation> findCoursesByMentor(String mentorName) {
        User courseOwner = defineOwnerByUsername(mentorName);
        return trainingCourseRepository.findAllByOwner(courseOwner).stream()
                .map(training -> getTrainingCourseRepresentation(training, training.getCourseName(), courseOwner))
                .collect(Collectors.toList());
    }

    public TrainingCourseRepresentation addCourse(String mentorName, TrainingCourseCreationDto trainingCourseCreationDto) {
        User courseOwner = defineOwnerByUsername(mentorName);
        if (doesCourseAlreadyExist(courseOwner, trainingCourseCreationDto.getCourseName())) {
            throw new IllegalArgumentException("Such course is already present for this mentor");
        }
        SkillLevel level = SkillLevel.valueOf(trainingCourseCreationDto.getSkillLevel());
        TrainingCourse trainingCourse = trainingCourseRepository.save(TrainingCourse.builder()
                .owner(courseOwner)
                .courseName(trainingCourseCreationDto.getCourseName())
                .description(trainingCourseCreationDto.getDescription())
                        .skillLevel(level)
                .build());
        return getTrainingCourseRepresentation(trainingCourse, trainingCourseCreationDto.getCourseName(), courseOwner);
    }

    public List<TrainingCourseRepresentation> deleteCourse(String mentorName, Long courseId) {
        User courseOwner = defineOwnerByUsername(mentorName);
        TrainingCourse trainingCourse = trainingCourseRepository.getById(courseId);
        if (trainingCourse.getOwner().getUsername().equals(mentorName)) {
            trainingCourseRepository.delete(trainingCourse);
            return trainingCourseRepository.findAllByOwner(courseOwner).stream()
                    .map(training -> getTrainingCourseRepresentation(training, training.getCourseName(), courseOwner))
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException("This course belongs to another mentor!");
    }

    public List<TrainingRequestRepresentation> findUserRequests(String studentName) {
        User student = userRepository.findByUsername(studentName);
        return trainingRequestRepository.findAllByRequester(student)
                .stream()
                .map(request -> getTrainingRequestRepresentation(student, request.getMentor().getUsername(), request))
                .collect(Collectors.toList());
    }

    public List<TrainingRequestRepresentation> approveTraining(User mentor, String requestId) {
        Long id = defineId(requestId);
        Optional<TrainingRequest> request = trainingRequestRepository.findById(id);
        request.ifPresent(trainingRequest -> {
            User student = userRepository.findById(request.get().getRequester().getId()).get();
            TrainingCourse trainingCourse = trainingRequest.getTrainingCourse();
            trainingCourse.getActiveStudents().add(student);
            trainingCourseRepository.save(trainingCourse);
            trainingRequestRepository.delete(trainingRequest);
        });
        return trainingRequestRepository.findAllByMentor(mentor)
                .stream()
                .map(trainingRequest -> getTrainingRequestRepresentation(trainingRequest.getRequester(),
                        trainingRequest.getMentor().getUsername(), trainingRequest))
                .collect(Collectors.toList());
    }

    public List<TrainingRequestRepresentation> disapproveTraining(User mentor, String requestId) {
        Long id = defineId(requestId);
        Optional<TrainingRequest> request = trainingRequestRepository.findById(id);
        request.ifPresent(trainingRequestRepository::delete);
        return trainingRequestRepository.findAllByMentor(mentor)
                .stream()
                .map(trainingRequest -> getTrainingRequestRepresentation(trainingRequest.getRequester(),
                        trainingRequest.getMentor().getUsername(), trainingRequest))
                .collect(Collectors.toList());
    }

    public TrainingCourseRepresentation finishTraining(User mentor, String trainingId, String studentId) {
        Long studentIdValue = defineId(studentId);
        Long idForTraining = defineId(trainingId);
        TrainingCourse trainingCourse = trainingCourseRepository.getById(idForTraining);
        if (trainingCourse.getOwner().getUsername().equals(mentor.getUsername())) {
            Optional<User> student = userRepository.findById(studentIdValue);
            if (student.isEmpty()) {
                throw new IllegalArgumentException("Invalid student id");
            }
            trainingCourse.getActiveStudents().remove(student.get());
            TrainingCourse course = trainingCourseRepository.save(trainingCourse);
            return getTrainingCourseRepresentation(course, trainingCourse.getCourseName(), trainingCourse.getOwner());
        } else {
            throw new IllegalArgumentException("This course is not permitted to be changed by you");
        }
    }

    private TrainingRequestRepresentation getTrainingRequestRepresentation(User assigner, String mentorName, TrainingRequest request) {
        return TrainingRequestRepresentation.builder()
                .requestId(request.getId())
                .creationTime(request.getCreationDateTime())
                .requester(representationUtil.defineUserRepresentation(assigner.getUsername()))
                .courseRepresentation(getTrainingCourseRepresentation(
                        request.getTrainingCourse(),
                        request.getTrainingCourse().getCourseName(),
                        request.getMentor())
                )
                .build();
    }

    private TrainingCourseRepresentation getTrainingCourseRepresentation(TrainingCourse course, String courseName, User courseOwner) {
        UserRepresentationDto profile = representationUtil.defineUserRepresentation(courseOwner.getUsername());
        if (course.getActiveStudents() == null) {
            course.setActiveStudents(new ArrayList<>());
        }
        String skillLevel = "Нету";
        if(course.getSkillLevel() != null) {
            skillLevel = course.getSkillLevel().getRussianAnalogue();
        }
        return TrainingCourseRepresentation.builder()
                .mentorName(profile)
                .courseName(courseName)
                .students(course.getActiveStudents()
                        .stream()
                        .map(student -> representationUtil.defineUserRepresentation(student.getUsername()))
                        .collect(Collectors.toSet()))
                .id(course.getId())
                .description(course.getDescription())
                .skillLevel(skillLevel)
                .build();
    }

    private Long defineId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Wrong Id format");
        }
    }

    private User defineOwnerByUsername(String mentorName) {
        User courseOwner = userRepository.findByUsername(mentorName);
        if (courseOwner == null) {
            throw new IllegalArgumentException("No mentor with such name is present");
        }
        return courseOwner;
    }

    private boolean doesCourseAlreadyExist(User courseOwner, String courseName) {
        if (trainingCourseRepository.findByOwnerAndCourseName(courseOwner, courseName).isPresent()) {
            return true;
        }
        return false;
    }
}
