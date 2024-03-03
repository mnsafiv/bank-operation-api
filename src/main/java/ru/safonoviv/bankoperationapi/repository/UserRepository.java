package ru.safonoviv.bankoperationapi.repository;

import org.springframework.data.repository.CrudRepository;
import ru.safonoviv.bankoperationapi.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByLogin(String login);
}
