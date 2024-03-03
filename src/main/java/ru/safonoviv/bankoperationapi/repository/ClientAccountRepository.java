package ru.safonoviv.bankoperationapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.safonoviv.bankoperationapi.entity.ClientAccount;
import ru.safonoviv.bankoperationapi.entity.Role;

import java.util.Optional;

@Repository
public interface ClientAccountRepository extends CrudRepository<ClientAccount, Long> {
}
