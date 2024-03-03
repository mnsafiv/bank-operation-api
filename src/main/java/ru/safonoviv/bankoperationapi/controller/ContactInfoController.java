package ru.safonoviv.bankoperationapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.safonoviv.bankoperationapi.dto.ContactInfoDto;
import ru.safonoviv.bankoperationapi.service.UserContactService;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/contactInfo")
@SecurityRequirements
public class ContactInfoController {
    private final UserContactService userContactService;

    @Operation(description = "Add contact info", responses = {
            @ApiResponse(responseCode = "200", ref = "successResponse"),
            @ApiResponse(responseCode = "400", ref = "badRequest"),
            @ApiResponse(responseCode = "403", ref = "forbiddenResponse")})
    @PostMapping("/create")
    public ResponseEntity<?> addContactInfo(@RequestBody ContactInfoDto contactInfoDto, final Principal principal) {
        return userContactService.addContactInfo(principal.getName(), contactInfoDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editContactInfo(@PathVariable Long id, @RequestBody ContactInfoDto contactInfoDto, final Principal principal) {
        return userContactService.updateContactInfo(id,principal.getName(), contactInfoDto);
    }

    @Operation(description = "Delete contact info by id", responses = {
            @ApiResponse(responseCode = "200", ref = "successResponse"),
            @ApiResponse(responseCode = "400", ref = "badRequest"),
            @ApiResponse(responseCode = "403", ref = "forbiddenResponse")})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContactInfo(@PathVariable Long id, final Principal principal) {
        return userContactService.removeContactInfoById(principal.getName(), id);
    }

}
