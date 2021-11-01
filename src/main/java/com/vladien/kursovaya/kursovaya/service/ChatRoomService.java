package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.entity.ChatRoom;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.ChatDto;
import com.vladien.kursovaya.kursovaya.repository.ChatRoomRepository;
import com.vladien.kursovaya.kursovaya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public List<ChatRoom> findByUser(User user) {
        List<ChatRoom> chats = chatRoomRepository.findAllByParticipantsContains(user);
        chats = chats.stream().map(chat -> defineOneToOneChatParameters(chat, user)).collect(Collectors.toList());
        chats = chats.stream().map(chat -> defineOneUserChatParameters(chat, user)).collect(Collectors.toList());
        return chats;
    }

    public ChatRoom showChat(User user, String id) {
        Long chatId = Long.parseLong(id);
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatId);
        if (chatRoom.get().getParticipants().contains(user)) {
            chatRoom = Optional.of(defineOneToOneChatParameters(chatRoom.get(), user));
            chatRoom = Optional.of(defineOneUserChatParameters(chatRoom.get(), user));
        }
        return chatRoom.get();
    }

    public ChatRoom createChatRoom(User currentUser, ChatDto chatDto) {
        Set<User> users =
                chatDto.getMembersUsernames().stream().map(userRepository::findByUsername).collect(Collectors.toSet());
        users.add(currentUser);
        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setParticipants(users);
        newChatRoom.setMessages(new ArrayList<>());
        newChatRoom.setName(chatDto.getChatName());
        chatRoomRepository.save(newChatRoom);
        return newChatRoom;
    }

    public ChatRoom createChatRoom(User currentUser, String recipientName) {
        ChatRoom chatRoom = new ChatRoom();
        User recipient = userRepository.findByUsername(recipientName);
        if (recipient != null) {
            chatRoom.setMessages(new ArrayList<>());
            chatRoom.setParticipants(Stream.of(currentUser, recipient).collect(Collectors.toSet()));
            chatRoomRepository.save(chatRoom);
            return chatRoom;
        } else {
            throw new IllegalArgumentException("Wrong recipient name");
        }
    }

    public Optional<ChatRoom> findById(Long id) {
        return chatRoomRepository.findById(id);
    }

    private ChatRoom defineOneToOneChatParameters(ChatRoom chatRoom, User currentUser) {
        if (chatRoom.getParticipants().size() == 2) {
            for (User recipient : chatRoom.getParticipants()) {
                if (!recipient.equals(currentUser)) {
                    chatRoom.setName(recipient.getUsername());
                }
            }
        }
        return chatRoom;
    }

    private ChatRoom defineOneUserChatParameters(ChatRoom chatRoom, User currentUser) {
        if (chatRoom.getParticipants().size() == 1) {
            chatRoom.setName(currentUser.getUsername());
        }
        return chatRoom;
    }
}
