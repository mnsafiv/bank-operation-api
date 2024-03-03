package ru.safonoviv.bankoperationapi.deposite;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.safonoviv.bankoperationapi.service.ClientAccountService;
import ru.safonoviv.bankoperationapi.service.UserService;

import java.time.Duration;
import java.util.Date;

@Service
public class DepositService implements Runnable{
    @Value("${increase.deposit.value}")
    private double DEPOSIT_VALUE;
    @Value("${increase.deposit.max}")
    private double DEPOSIT_START_MAX;
    private final ClientAccountService clientAccountService;
    private final DepositSchedule depositSchedule;
    private final Thread thread;
    private boolean run=true;



    public DepositService(ClientAccountService clientAccountService, DepositSchedule depositSchedule) {
        this.clientAccountService = clientAccountService;
        this.depositSchedule = depositSchedule;
        thread=new Thread(this);
        thread.start();
    }

    public void stop(){
        run=false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        waitSchedule();
        while (run){
            clientAccountService.increaseDeposit(DEPOSIT_VALUE,DEPOSIT_START_MAX);
            waitSchedule();
        }

    }

    @SneakyThrows
    public void waitSchedule()  {
        long schedule = depositSchedule.getDepositSchedule();
        long time = new Date().getTime();
        if (schedule > time) {
            Thread.sleep(schedule - time);
        }
    }
}
