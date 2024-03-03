package ru.safonoviv.bankoperationapi.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.safonoviv.bankoperationapi.entity.User;
import ru.safonoviv.bankoperationapi.entity.UserContact;
import ru.safonoviv.bankoperationapi.service.UserContactService;
import ru.safonoviv.bankoperationapi.service.UserInfoService;
import ru.safonoviv.bankoperationapi.service.UserService;
import ru.safonoviv.bankoperationapi.util.SearchUtil;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/clients")
@SecurityRequirements
public class ClientController {

    private final UserService userService;
    private final UserInfoService userInfoService;
    private final UserContactService userContactService;
    private final SearchUtil searchUtil;

    @Operation(description = "Get client by id", responses = {
            @ApiResponse(responseCode = "200", ref = "successResponse"),
            @ApiResponse(responseCode = "400", ref = "badRequest"),
            @ApiResponse(responseCode = "403", ref = "forbiddenResponse") })
    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(description = "Search client", responses = {
            @ApiResponse(responseCode = "200", ref = "successResponse"),
            @ApiResponse(responseCode = "400", ref = "badRequest"),
            @ApiResponse(responseCode = "403", ref = "forbiddenResponse") })
    @GetMapping("/search")
    public ResponseEntity<?> searchClient(@RequestParam(name = "search") String search,
                                          @PageableDefault(value = 3)
                                          @SortDefault.SortDefaults({@SortDefault(sort = "id", direction = Sort.Direction.ASC)}) Pageable pageable) {
        switch (searchUtil.searchType(search)) {
            case email, phone -> {
                UserContact userContact = userContactService.findByContact(search);
                userService.getUserById(userContact.getId());
                return ResponseEntity.ok(userService.getUserById(userContact.getId()));
            }
            case fullName -> {
                Collection<Long> usersId = userInfoService.findUsersIdSorted(search, pageable);
                Set<User> users = usersId.stream().map(userService::getUserById).collect(Collectors.toSet());
                return ResponseEntity.ok(users);
            }
            default -> {
                Collection<Long> usersId = userInfoService.findUsersIdSortedByDateOfBorn(search, pageable);
                Set<User> users = usersId.stream().map(userService::getUserById).collect(Collectors.toSet());
                return ResponseEntity.ok(users);
            }
        }
    }
}
