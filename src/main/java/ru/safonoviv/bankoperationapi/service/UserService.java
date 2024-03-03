package ru.safonoviv.bankoperationapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.safonoviv.bankoperationapi.dto.RegistrationUserDto;
import ru.safonoviv.bankoperationapi.dto.RegistrationUserFullDto;
import ru.safonoviv.bankoperationapi.entity.*;
import ru.safonoviv.bankoperationapi.exceptions.ExceptionRollBack;
import ru.safonoviv.bankoperationapi.exceptions.NotFoundException;
import ru.safonoviv.bankoperationapi.repository.UserRepository;
import ru.safonoviv.bankoperationapi.util.SearchUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager entityManager;
    private final UserRepository userRepository;


    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final SearchUtil searchUtil;


    public Optional<User> findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Cacheable(value = "userCache", key = "#login")
    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("Not found: " + login));
    }

    @CacheEvict(value = "userCache", key = "#login")
    public void evictUserByLogin(String login) {
    }

    @Cacheable(value = "userCache", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found: " + id, HttpStatus.BAD_REQUEST));
    }

    @CacheEvict(value = "userCache", key = "#id")
    public void evictUserById(String id) {
    }

    @CacheEvict(value = "userCache", allEntries = true)
    public void evictAllUserCache() {
    }


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = getUserByLogin(login);
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                user.getUserRoles().stream().map(UserRole::getRole).map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }


    @Transactional(rollbackOn = ExceptionRollBack.class)
    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = User.builder()
                .login(registrationUserDto.getLogin())
                .password(passwordEncoder.encode(registrationUserDto.getPassword()))
                .build();
        ClientAccount clientAccount = ClientAccount.builder()
                .user(user)
                .balanceCurrent(registrationUserDto.getBalance())
                .balanceStart(registrationUserDto.getBalance())
                .build();
        Set<UserContact> userContacts = registrationUserDto.getContacts().stream().map(searchUtil::getContact).collect(Collectors.toSet());
        for (UserContact userContact : userContacts) userContact.setUser(user);
        UserInfo userInfo = UserInfo.builder()
                .user(user)
                .build();

        user.setUserRoles(Collections.singleton(new UserRole(user, roleService.getUserRole())));
        user.setUserContact(userContacts);
        user.setClientAccount(clientAccount);
        user.setUserInfo(userInfo);
        userRepository.save(user);
        if (user.getId() == null || !correctContacts(registrationUserDto.getContacts())) {
            throw new ExceptionRollBack("Акаунт не создан!", HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    public boolean isAvailableContacts(Collection<String> contacts) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserContact> cr = cb.createQuery(UserContact.class);
        Root<UserContact> userContactRoot = cr.from(UserContact.class);
        CriteriaQuery<UserContact> userContacts = cr.where(cb.in(userContactRoot.get("contactInfo")).value(contacts));
        return entityManager.createQuery(userContacts).getResultList().isEmpty();
    }

    public boolean correctContacts(Collection<String> contacts) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserContact> cr = cb.createQuery(UserContact.class);
        Root<UserContact> userContactRoot = cr.from(UserContact.class);
        CriteriaQuery<UserContact> userContacts = cr.where(cb.in(userContactRoot.get("contactInfo")).value(contacts));
        return contacts.size() == entityManager.createQuery(userContacts).getResultList().size();
    }


    @Transactional(rollbackOn = ExceptionRollBack.class)
    public void createVerifiedUsers(Collection<RegistrationUserFullDto> registrationUsers) {
        Set<String> contacts = registrationUsers.stream().flatMap(n -> n.getContact().stream()).collect(Collectors.toSet());
        if (!isAvailableContacts(contacts)) {
            throw new ExceptionRollBack("Аккаунт не создан! Не уникальная контактная информация!", HttpStatus.BAD_REQUEST);
        }
        for (RegistrationUserFullDto regUser : registrationUsers) {
            User user = User.builder()
                    .login(regUser.getLogin())
                    .password(passwordEncoder.encode(regUser.getPassword()))
                    .build();
            user.setUserRoles(Collections.singleton(new UserRole(user, roleService.getUserRole())));
            ClientAccount clientAccount = ClientAccount.builder()
                    .user(user)
                    .balanceCurrent(regUser.getBalance())
                    .balanceStart(regUser.getBalance())
                    .verified(true)
                    .build();
            user.setClientAccount(clientAccount);

            List<UserContact> userContacts = regUser.getContact().stream().map(searchUtil::getContact).toList();
            for (UserContact userContact : userContacts) userContact.setUser(user);

            user.setUserContact(new HashSet<>(userContacts));
            UserInfo userInfo = UserInfo.builder()
                    .user(user)
                    .firstName(regUser.getFirstName())
                    .middleName(regUser.getMiddleName())
                    .secondName(regUser.getSecondName())
                    .dateOfBirth(regUser.getDateOfBorn())
                    .build();
            user.setUserInfo(userInfo);
            userRepository.save(user);
            if (user.getId() == null) {
                throw new ExceptionRollBack("Аккаунт не создан! " + user.getLogin(), HttpStatus.BAD_REQUEST);
            }
        }
        if (!correctContacts(contacts)) {
            throw new ExceptionRollBack("Аккаунт не создан из-за дубликатов в контактах!", HttpStatus.BAD_REQUEST);
        }
    }
}
