package ru.safonoviv.bankoperationapi.dto;

import lombok.Getter;

@Getter
public class TransferDepositDto {
    private String receiver;
    private double value;
}
