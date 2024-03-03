package ru.safonoviv.bankoperationapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.safonoviv.bankoperationapi.dto.TransferDepositDto;
import ru.safonoviv.bankoperationapi.service.ClientAccountService;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/account")
@SecurityRequirements
public class ClientAccountController {
    @Autowired
    private ClientAccountService clientAccountService;

    @Operation(description = "Money transfer", responses = {
            @ApiResponse(responseCode = "200", ref = "successResponse"),
            @ApiResponse(responseCode = "400", ref = "badRequest"),
            @ApiResponse(responseCode = "403", ref = "forbiddenResponse") })
    @PostMapping("/transfer")
    public ResponseEntity<?> transferDeposit(@RequestBody TransferDepositDto transferDepositDto, final Principal principal){
        clientAccountService.transferDeposit(transferDepositDto,principal.getName());
        return ResponseEntity.ok("Success!");
    }

}
