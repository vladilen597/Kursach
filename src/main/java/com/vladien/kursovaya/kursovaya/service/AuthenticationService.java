package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.service.util.UserRepresentationUtil;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.entity.UserRole;
import com.vladien.kursovaya.kursovaya.entity.dto.CreateUserDto;
import com.vladien.kursovaya.kursovaya.entity.dto.LoginDto;
import com.vladien.kursovaya.kursovaya.entity.dto.UserRepresentationDto;
import com.vladien.kursovaya.kursovaya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final ModelMapper modelMapper;
    private final UserRepresentationUtil representationUtil;
    private final UserRepository userRepository;

    public User loginUser(LoginDto loginDto) {
        try {
            String username = loginDto.getUsername();
            String password = loginDto.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepository.findByUsername(username);
            if (isNull(user)) {
                throw new UsernameNotFoundException("user not found");
            }
            return user;
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public Optional<UserRepresentationDto> createUser(CreateUserDto newUser, UserRole userRole) {
        Optional<UserRepresentationDto> createdUser;
        if (isUsernameFree(newUser.getUsername())) {
            User user = modelMapper.map(newUser, User.class);
            user.setPassword(encoder.encode(newUser.getPassword()));
            user.setRoles(new HashSet<>(Collections.singleton(userRole)));
            user.setEnabled(true);
            userRepository.save(user);
            createdUser = Optional.of(representationUtil.defineUserRepresentation(newUser.getUsername()));
        } else {
            throw new IllegalArgumentException("Username is taken");
        }
        return createdUser;
    }

    private boolean isUsernameFree(String username) {
        return isNull(userRepository.findByUsername(username));
    }
}
