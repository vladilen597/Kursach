package com.vladien.kursovaya.kursovaya.controller;

import com.vladien.kursovaya.kursovaya.controller.util.CurrentPrincipalDefiner;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.dto.EditProfileDto;
import com.vladien.kursovaya.kursovaya.entity.dto.ProfileRepresentation;
import com.vladien.kursovaya.kursovaya.entity.dto.ResponseMessage;
import com.vladien.kursovaya.kursovaya.security.JwtAuthenticationException;
import com.vladien.kursovaya.kursovaya.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final CurrentPrincipalDefiner principalDefiner;
    private final UserService userService;

    @Value("${spring.servlet.multipart.location}")
    private String filePath;

    @GetMapping("/current")
    public ProfileRepresentation showCurrentUserProfile() {
        String username = principalDefiner.currentUsername();
        return userService.findCurrentUserProfile(username);
    }

    @PutMapping("/current")
    public ProfileRepresentation editProfile(@RequestBody EditProfileDto editProfileDto) {
        String username = principalDefiner.currentUsername();
        return userService.editProfile(username, editProfileDto);
    }

    @PostMapping("current/uploadImage")
    public ProfileRepresentation uploadFile(@RequestParam("File") MultipartFile file) {
        String uploadedFileName = filePath + file.getOriginalFilename();
        File savedFile = new File(uploadedFileName);
        try {
            savedFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(savedFile);
            fos.write(file.getBytes());
            fos.close();
            User user = principalDefiner.getPrincipal();
            return userService.updateUserImage(user, uploadedFileName);
        } catch (IOException e) {
            throw new IllegalArgumentException("File was not uploaded");
        }
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
