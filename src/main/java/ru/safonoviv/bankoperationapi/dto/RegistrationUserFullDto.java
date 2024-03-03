package ru.safonoviv.bankoperationapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationUserFullDto {
    private String login;
    private String password;
    private List<String> contact;
    private Float balance;
    private String firstName;
    private String secondName;
    private String middleName;
    private LocalDate dateOfBorn;
}
