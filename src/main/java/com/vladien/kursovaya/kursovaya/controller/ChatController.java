package com.vladien.kursovaya.kursovaya.controller;

import com.vladien.kursovaya.kursovaya.controller.util.CurrentPrincipalDefiner;
import com.vladien.kursovaya.kursovaya.entity.ChatRoom;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.*;
import com.vladien.kursovaya.kursovaya.security.JwtAuthenticationException;
import com.vladien.kursovaya.kursovaya.service.ChatRoomService;
import com.vladien.kursovaya.kursovaya.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ChatController {
    private final CurrentPrincipalDefiner currentPrincipalDefiner;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;

    @GetMapping
    public List<ChatRoom> viewChatRooms() {
        User user = currentPrincipalDefiner.getPrincipal();
        return chatRoomService.findByUser(user);
    }

    @PostMapping
    public ChatRoom createChatRoom(@RequestBody ChatDto chatDto) {
        User user = currentPrincipalDefiner.getPrincipal();
        return chatRoomService.createChatRoom(user, chatDto);
    }

    @PostMapping("/{recipientId}")
    public ChatRoom createChatRoom(@PathVariable String recipientId) {
        User user = currentPrincipalDefiner.getPrincipal();
        return chatRoomService.createChatRoom(user, recipientId);
    }

    @GetMapping("/{chatId}")
    public ChatRoom viewChatRoom(@PathVariable String chatId) {
        User user = currentPrincipalDefiner.getPrincipal();
        return chatRoomService.showChat(user, chatId);
    }

    @PostMapping(value = "/conversation/{chatId}")
    public ChatRoom addMessage(@PathVariable String chatId, @RequestBody TextDto newMessage) {
        User user = currentPrincipalDefiner.getPrincipal();
        return messageService.createMessage(user, chatId, newMessage);
    }

    @DeleteMapping(value = "/conversation/{chatId}")
    public ChatRoom deleteMessage(@PathVariable String chatId, @RequestBody IdDto messageId) {
        User user = currentPrincipalDefiner.getPrincipal();
        return messageService.deleteMessage(user, chatId, messageId.getId());
    }

    @PutMapping(value = "/conversation/{chatId}")
    public ChatRoom editMessage(@PathVariable String chatId, @RequestBody EditMessageDto editedMessage) {
        User user = currentPrincipalDefiner.getPrincipal();
        return messageService.editMessage(user, chatId, editedMessage);
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
