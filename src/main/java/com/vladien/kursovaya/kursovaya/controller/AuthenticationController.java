package com.vladien.kursovaya.kursovaya.controller;

import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.UserRole;
import com.vladien.kursovaya.kursovaya.entity.dto.CreateUserDto;
import com.vladien.kursovaya.kursovaya.entity.dto.LoginDto;
import com.vladien.kursovaya.kursovaya.entity.dto.ResponseMessage;
import com.vladien.kursovaya.kursovaya.entity.dto.UserRepresentationDto;
import com.vladien.kursovaya.kursovaya.security.JwtAuthenticationException;
import com.vladien.kursovaya.kursovaya.security.JwtTokenProvider;
import com.vladien.kursovaya.kursovaya.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(value = "/login")
    public ResponseMessage loginMentor(@RequestBody LoginDto loginDto) {
        User user = authenticationService.loginUser(loginDto);
        return new ResponseMessage("Bearer_" + jwtTokenProvider.createToken(user.getUsername(), user.getRoles()));
    }

    @PostMapping(value = "/registration/mentor")
    public Optional<UserRepresentationDto> addMentor(@RequestBody CreateUserDto profile) {
        return authenticationService.createUser(profile, UserRole.ROLE_MENTOR);
    }

    @PostMapping(value = "/registration/student")
    public Optional<UserRepresentationDto> addStudent(@RequestBody CreateUserDto profile) {
        return authenticationService.createUser(profile, UserRole.ROLE_CLIENT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<String> handleJwtValidationExceptions(JwtAuthenticationException ex) {
        return ResponseEntity.status(401).body("Your session token is expired or invalid. Sign in to continue");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseMessage> handleTakenUsernameExceptions(IllegalArgumentException ex) {
        return ResponseEntity.status(403).body(new ResponseMessage(ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundEntityExceptions(JwtAuthenticationException ex) {
        return ResponseEntity.status(404).body("Can't find data which you are requesting");
    }
}
