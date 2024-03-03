package ru.safonoviv.bankoperationapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.safonoviv.bankoperationapi.dto.JwtRequest;
import ru.safonoviv.bankoperationapi.dto.RegistrationUserDto;
import ru.safonoviv.bankoperationapi.dto.RegistrationUserFullDto;
import ru.safonoviv.bankoperationapi.service.AuthService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Tag(name = "auth")
public class AuthController {
	private final AuthService authService;

	@Operation(description = "Sign in service", responses = {
			@ApiResponse(responseCode = "200", ref = "successResponse"),
			@ApiResponse(responseCode = "401", ref = "unauthorized") })
	@PostMapping("/auth")
	public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
		return authService.createAuthToken(authRequest);
	}

	@Operation(description = "Register no verified user in service", responses = {
			@ApiResponse(responseCode = "200", ref = "successRegister"),
			@ApiResponse(responseCode = "400", ref = "badRequestRegister") })
	@PostMapping("/registration")
	public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
		return authService.createUser(registrationUserDto);
	}
	@Operation(description = "Register verified user in service", responses = {
			@ApiResponse(responseCode = "200", ref = "successResponse"),
			@ApiResponse(responseCode = "400", ref = "badRequest") })
	@PostMapping("/registrationFull")
	public ResponseEntity<?> createNewUserFull(@RequestBody Collection<RegistrationUserFullDto> registrationUsers) {
		return authService.createVerifiedUser(registrationUsers);
	}
}