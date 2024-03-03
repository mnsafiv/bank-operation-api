package ru.safonoviv.bankoperationapi.deposite;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
public class DepositSchedule {
    @Value("${increase.deposit.time}")
    private Duration TIME_DEPOSIT;
    private Long nextDeposit;

    public DepositSchedule() {
        nextDeposit = new Date().getTime();
    }

    public synchronized long getDepositSchedule() {
        nextDeposit = nextDeposit + TIME_DEPOSIT.getSeconds() * 1000;
        if (nextDeposit < new Date().getTime()) {
            throw new RuntimeException("Error time deposit!");
        }
        return nextDeposit;
    }
}
