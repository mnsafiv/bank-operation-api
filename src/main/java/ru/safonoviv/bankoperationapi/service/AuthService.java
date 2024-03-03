package ru.safonoviv.bankoperationapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.safonoviv.bankoperationapi.dto.*;
import ru.safonoviv.bankoperationapi.exceptions.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) throws NotFoundException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new NotFoundException("Ошибка авторизации", HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getLogin());
        String token = jwtTokenService.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> createUser(@RequestBody RegistrationUserDto registrationUserDto) {
        if (userService.findUserByLogin(registrationUserDto.getLogin()).isPresent()) {
            return new ResponseEntity<>("Логин уже занят!", HttpStatus.BAD_REQUEST);
        }
        if (!userService.isAvailableContacts(registrationUserDto.getContacts())) {
            return new ResponseEntity<>("Данные контакты уже заняты!", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userService.createNewUser(registrationUserDto));
    }


    public ResponseEntity<?> createVerifiedUser(@RequestBody Collection<RegistrationUserFullDto> registrationUsers) {
        userService.createVerifiedUsers(registrationUsers);
        return ResponseEntity.ok("Success");
    }
}
