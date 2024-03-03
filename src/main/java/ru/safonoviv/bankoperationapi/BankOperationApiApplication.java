package ru.safonoviv.bankoperationapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import ru.safonoviv.bankoperationapi.entity.*;
import ru.safonoviv.bankoperationapi.repository.RoleRepository;
import ru.safonoviv.bankoperationapi.repository.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@EnableCaching
@SpringBootApplication
public class BankOperationApiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BankOperationApiApplication.class, args);
    }

    @Autowired
    private RoleRepository roleRepository;

    public void run(String... args) throws Exception {
        Role role = new Role();
        role.setName(RoleType.USER.name());
        roleRepository.save(role);
    }
}
