package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.entity.ChatRoom;
import com.vladien.kursovaya.kursovaya.entity.TrainingCourse;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.ChatRepresentation;
import com.vladien.kursovaya.kursovaya.entity.dto.UserRepresentationDto;
import com.vladien.kursovaya.kursovaya.repository.ChatRoomRepository;
import com.vladien.kursovaya.kursovaya.repository.TrainingCourseRepository;
import com.vladien.kursovaya.kursovaya.service.util.UserRepresentationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final TrainingCourseRepository trainingCourseRepository;
    private final UserRepresentationUtil representationUtil;

    public ChatRepresentation showChatForCourse(User user, String id) {
        Long courseId = Long.parseLong(id);
        Optional<TrainingCourse> course = trainingCourseRepository.findById(courseId);
        if (course.get().getChatRoom().getParticipants().contains(user)) {
            ChatRoom chatRoom = course.get().getChatRoom();
            return defineChatRepresentation(chatRoom);
        }
        throw new IllegalArgumentException("You are not a part of this chat");
    }

    public ChatRoom addParticipant(ChatRoom chatRoom, User user) {
        chatRoom.getParticipants().add(user);
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom deleteParticipant(ChatRoom chatRoom, User user) {
        chatRoom.getParticipants().remove(user);
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom deleteAllParticipants(ChatRoom chatRoom) {
        chatRoom.getParticipants().clear();
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom createChatRoomForCourse(TrainingCourse course) {
        List<User> users = course.getActiveStudents();
        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setParticipants(new HashSet<>(users));
        newChatRoom.getParticipants().add(course.getOwner());
        newChatRoom.setMessages(new ArrayList<>());
        newChatRoom.setName(course.getCourseName());
        newChatRoom.setTrainingCourse(course);
        newChatRoom = chatRoomRepository.save(newChatRoom);
        course.setChatRoom(newChatRoom);
        trainingCourseRepository.save(course);
        return newChatRoom;
    }

    public ChatRoom findChatForCourse(Long courseID) {
        if(trainingCourseRepository.findById(courseID).isEmpty()) {
            throw new IllegalArgumentException("No course with such id");
        }
        return trainingCourseRepository.findById(courseID).get().getChatRoom();
    }

    private ChatRepresentation defineChatRepresentation(ChatRoom chatRoom) {
        return ChatRepresentation.builder()
                .id(chatRoom.getId())
                .messages(chatRoom.getMessages())
                .name(chatRoom.getName())
                .trainingCourseId(chatRoom.getTrainingCourse().getId())
                .participants(transformListOfUsersToRepresentations(chatRoom.getParticipants()))
                .build();
    }

    private List<UserRepresentationDto> transformListOfUsersToRepresentations(Set<User> users) {
        return users.stream()
                .map(user -> representationUtil.defineUserRepresentation(user.getUsername()))
                .collect(Collectors.toList());
    }
}
