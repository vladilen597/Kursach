package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.entity.ChatRoom;
import com.vladien.kursovaya.kursovaya.entity.Message;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.ChatRepresentation;
import com.vladien.kursovaya.kursovaya.entity.dto.EditMessageDto;
import com.vladien.kursovaya.kursovaya.entity.dto.TextDto;
import com.vladien.kursovaya.kursovaya.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;

    @Transactional
    public ChatRepresentation deleteMessage(User user, String courseId, Long messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent() && message.get().getAuthorId().equals(user.getId())) {
            messageRepository.deleteById(messageId);
            ChatRoom chatRoom = chatRoomService.findChatForCourse(getLongFromString(courseId));
            chatRoom.getMessages().remove(message.get());
        }
        return chatRoomService.showChatForCourse(user, courseId);
    }

    @Transactional
    public ChatRepresentation editMessage(User user, String courseId, EditMessageDto redactedMessage) {
        Optional<Message> messageToEdit = messageRepository.findById(redactedMessage.getId());
        if (messageToEdit.isPresent() && messageToEdit.get().getAuthorId().equals(user.getId())) {
            messageToEdit.get().setText(redactedMessage.getText());
            messageRepository.save(messageToEdit.get());
        }
        return chatRoomService.showChatForCourse(user, courseId);
    }

    @Transactional
    public ChatRepresentation createMessage(User owner, String courseId, TextDto newMessage) {
        Message createdMessage = new Message();
        ChatRoom chatRoom = chatRoomService.findChatForCourse(getLongFromString(courseId));
        createdMessage.setText(newMessage.getText());
        createdMessage.setAuthorId(owner.getId());
        createdMessage.setAuthorFullName(owner.getFirstName() + " " + owner.getLastName() + " " + owner.getPatronymic());
        createdMessage.setChatRoom(chatRoom);
        createdMessage.setCreationDateTime(LocalDateTime.now());
        messageRepository.save(createdMessage);
        chatRoom.getMessages().add(createdMessage);
        return chatRoomService.showChatForCourse(owner, courseId);
    }

    private Long getLongFromString(String courseID) {
        try {
            return Long.parseLong(courseID);
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentException("invalid id for a chat room");
        }
    }
}
