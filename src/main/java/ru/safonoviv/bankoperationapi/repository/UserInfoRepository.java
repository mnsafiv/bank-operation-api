package ru.safonoviv.bankoperationapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.safonoviv.bankoperationapi.entity.UserInfo;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
}
