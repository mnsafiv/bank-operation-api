package ru.safonoviv.bankoperationapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.safonoviv.bankoperationapi.entity.UserContact;

import java.util.Optional;

@Repository
public interface UserContactRepository extends CrudRepository<UserContact, Long> {
    Optional<UserContact> findByContactInfo(String name);
}
