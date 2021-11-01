package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.service.util.UserRepresentationUtil;
import com.vladien.kursovaya.kursovaya.entity.ChatRoom;
import com.vladien.kursovaya.kursovaya.entity.TrainingCourse;
import com.vladien.kursovaya.kursovaya.entity.TrainingRequest;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.TextDto;
import com.vladien.kursovaya.kursovaya.entity.dto.TrainingCourseRepresentation;
import com.vladien.kursovaya.kursovaya.entity.dto.TrainingRequestRepresentation;
import com.vladien.kursovaya.kursovaya.entity.dto.UserRepresentationDto;
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

    public List<TrainingCourseRepresentation> findCoursesByMentor(String mentorName) {
        User courseOwner = defineOwnerByUsername(mentorName);
        return trainingCourseRepository.findAllByOwner(courseOwner).stream()
                .map(training -> getTrainingCourseRepresentation(training, training.getCourseName(), courseOwner))
                .collect(Collectors.toList());
    }

    public TrainingCourseRepresentation addCourse(String mentorName, String courseName) {
        User courseOwner = defineOwnerByUsername(mentorName);
        if (doesCourseAlreadyExist(courseOwner, courseName)) {
            throw new IllegalArgumentException("Such course is already present for this mentor");
        }
        TrainingCourse trainingCourse = trainingCourseRepository.save(TrainingCourse.builder()
                .owner(courseOwner)
                .courseName(courseName)
                .build());
        return getTrainingCourseRepresentation(trainingCourse, courseName, courseOwner);
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

    public List<TrainingRequestRepresentation> findUserApprovedRequests(String studentName) {
        User student = userRepository.findByUsername(studentName);
        return trainingRequestRepository.findAllByRequester(student)
                .stream()
                .filter(TrainingRequest::getIsApproved)
                .map(request -> getTrainingRequestRepresentation(student, request.getMentor().getUsername(), request))
                .collect(Collectors.toList());
    }

    public List<TrainingRequestRepresentation> findUserRequests(String studentName) {
        User student = userRepository.findByUsername(studentName);
        return trainingRequestRepository.findAllByRequester(student)
                .stream()
                .map(request -> getTrainingRequestRepresentation(student, request.getMentor().getUsername(), request))
                .collect(Collectors.toList());
    }

    public List<TrainingRequestRepresentation> findUserUnapprovedRequests(String studentName) {
        User student = userRepository.findByUsername(studentName);
        return trainingRequestRepository.findAllByRequester(student)
                .stream()
                .filter(trainingRequest -> !trainingRequest.getIsApproved())
                .map(request -> getTrainingRequestRepresentation(student, request.getMentor().getUsername(), request))
                .collect(Collectors.toList());
    }

    public List<TrainingRequestRepresentation> approveTraining(User mentor, String requestId) {
        Long id = defineId(requestId);
        Optional<TrainingRequest> request = trainingRequestRepository.findById(id);
        request.ifPresent(trainingRequest -> {
            trainingRequest.setIsApproved(true);
            User student = userRepository.findById(request.get().getRequester().getId()).get();
            TrainingCourse trainingCourse = trainingRequest.getTrainingCourse();
            trainingCourse.getActiveStudents().add(student);
            trainingCourseRepository.save(trainingCourse);
            trainingRequestRepository.save(trainingRequest);
        });
        return trainingRequestRepository.findAllByIsApprovedAndMentor(true, mentor)
                .stream()
                .map(trainingRequest -> getTrainingRequestRepresentation(trainingRequest.getRequester(),
                        trainingRequest.getMentor().getUsername(), trainingRequest))
                .collect(Collectors.toList());
    }

    public TrainingCourseRepresentation finishTraining(User mentor, String requestId, String trainingId) {
        Long idForRequest = defineId(requestId);
        Long idForTraining = defineId(trainingId);
        TrainingCourse trainingCourse = trainingCourseRepository.getById(idForTraining);
        if (trainingCourse.getOwner().getUsername().equals(mentor.getUsername())) {
            Optional<TrainingRequest> request = trainingRequestRepository.findById(idForRequest);
            if (request.isEmpty()) {
                throw new IllegalArgumentException("Invalid training request id");
            }
            User student = userRepository.findById(request.get().getRequester().getId()).get();
            trainingCourse.getActiveStudents().remove(student);
            TrainingCourse course = trainingCourseRepository.save(trainingCourse);
            trainingRequestRepository.delete(request.get());
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
                .mentor(modelMapper.map(userRepository.findByUsername(mentorName), UserRepresentationDto.class))
                .courseId(request.getTrainingCourse().getId())
                .build();
    }

    private TrainingCourseRepresentation getTrainingCourseRepresentation(TrainingCourse course, String courseName, User courseOwner) {
        UserRepresentationDto profile = representationUtil.defineUserRepresentation(courseOwner.getUsername());
        if(course.getActiveStudents() == null) {
            course.setActiveStudents(new ArrayList<>());
        }
        return TrainingCourseRepresentation.builder()
                .mentorName(profile)
                .courseName(courseName)
                .students(course.getActiveStudents()
                        .stream()
                        .map(student -> representationUtil.defineUserRepresentation(student.getUsername()))
                        .collect(Collectors.toList()))
                .id(course.getId())
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
