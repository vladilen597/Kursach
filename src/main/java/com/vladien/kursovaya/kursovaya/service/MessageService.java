package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.entity.ChatRoom;
import com.vladien.kursovaya.kursovaya.entity.Message;
import com.vladien.kursovaya.kursovaya.entity.User;
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
    public ChatRoom deleteMessage(User user, String chatId, Long messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent() && message.get().getAuthorId().equals(user.getId())) {
            messageRepository.deleteById(messageId);
            ChatRoom chatRoom = chatRoomService.showChat(user, chatId);
            chatRoom.getMessages().remove(message.get());
            return chatRoom;
        }
        return chatRoomService.showChat(user, chatId);
    }

    @Transactional
    public ChatRoom editMessage(User user, String chatId, EditMessageDto redactedMessage) {
        Optional<Message> messageToEdit = messageRepository.findById(redactedMessage.getId());
        if (messageToEdit.isPresent() && messageToEdit.get().getAuthorId().equals(user.getId())) {
            messageToEdit.get().setText(redactedMessage.getText());
            messageRepository.save(messageToEdit.get());
        }
        return chatRoomService.showChat(user, chatId);
    }

    @Transactional
    public ChatRoom createMessage(User owner, String chatId, TextDto newMessage) {
        Message createdMessage = new Message();
        Optional<ChatRoom> chatRoom = defineChatRoom(chatId);
        if (chatRoom.isPresent()) {
            createdMessage.setText(newMessage.getText());
            createdMessage.setAuthorId(owner.getId());
            createdMessage.setChatRoom(chatRoom.get());
            createdMessage.setCreationDateTime(LocalDateTime.now());
            messageRepository.save(createdMessage);
            chatRoom.get().getMessages().add(createdMessage);
            return chatRoom.get();
        }
        return chatRoomService.showChat(owner, chatId);
    }

    public Message updateMessages(Message message) {
        return messageRepository.save(message);
    }

    private Optional<ChatRoom> defineChatRoom(String chatId) {
        try {
            Long id = Long.parseLong(chatId);
            return chatRoomService.findById(id);
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentException("invalid id for a chat room");
        }
    }
}
