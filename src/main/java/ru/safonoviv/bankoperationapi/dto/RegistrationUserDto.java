package ru.safonoviv.bankoperationapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationUserDto {
    private String login;
    private String password;
    private List<String> contacts;
    private float balance;
}
