package ru.safonoviv.bankoperationapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.safonoviv.bankoperationapi.entity.Role;
import ru.safonoviv.bankoperationapi.entity.RoleType;
import ru.safonoviv.bankoperationapi.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByName(RoleType.USER.name()).stream().findFirst().get();
    }
}
