package ru.safonoviv.bankoperationapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.safonoviv.bankoperationapi.dto.ContactInfoDto;

import ru.safonoviv.bankoperationapi.entity.User;
import ru.safonoviv.bankoperationapi.entity.UserContact;
import ru.safonoviv.bankoperationapi.entity.UserInfo;
import ru.safonoviv.bankoperationapi.exceptions.ExceptionRollBack;
import ru.safonoviv.bankoperationapi.exceptions.NotFoundException;
import ru.safonoviv.bankoperationapi.repository.UserContactRepository;
import ru.safonoviv.bankoperationapi.util.SearchUtil;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserContactService {
    @PersistenceContext
    private EntityManager entityManager;
    private final SearchUtil searchUtil;
    private final UserContactRepository userContactRepo;
    @Autowired
    @Lazy
    private UserService userService;

    @Cacheable(value = "userContactCache", key = "#contact")
    public UserContact findByContact(String contact) {
        return userContactRepo.findByContactInfo(contact).orElseThrow(() -> new NotFoundException("Not found: " + contact, HttpStatus.BAD_REQUEST));
    }

    @CacheEvict(value = "userContactCache", key = "#contact")
    public UserContact evictByContact(String contact) {
        return userContactRepo.findByContactInfo(contact).orElseThrow(() -> new NotFoundException("Not found: " + contact, HttpStatus.BAD_REQUEST));
    }

    public Long findUserIdByEmailOrPhone(String contact) {
        return findByContact(contact).getUser().getId();
    }

    @Transactional
    public ResponseEntity<?> addContactInfo(String login, ContactInfoDto contactInfoDto) {
        if (userContactRepo.findByContactInfo(contactInfoDto.getContact()).isPresent()) {
            return new ResponseEntity<>("Contact is busy", HttpStatus.BAD_REQUEST);
        }
        UserContact userContact = searchUtil.getContact(contactInfoDto.getContact());
        userService.evictUserByLogin(login);
        User user = userService.getUserByLogin(login);
        userContact.setUser(user);
        UserContact save = userContactRepo.save(userContact);
        return ResponseEntity.ok(save);
    }

    public int numbersContact(Long userId, CriteriaBuilder cb) {
        CriteriaQuery<UserContact> cr = cb.createQuery(UserContact.class);
        Root<UserContact> userContactRoot = cr.from(UserContact.class);
        CriteriaQuery<UserContact> userContacts = cr.where(cb.equal(userContactRoot.get("user").get("id"), userId));
        return entityManager.createQuery(userContacts).getResultList().size();
    }


    @Transactional(rollbackOn = ExceptionRollBack.class)
    public ResponseEntity<?> removeContactInfoById(String login, Long id) {
        User user = userService.getUserByLogin(login);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<UserContact> criteriaDelete = cb.createCriteriaDelete(UserContact.class);
        Root<UserContact> userContactRoot = criteriaDelete.from(UserContact.class);
        criteriaDelete.where(cb.equal(userContactRoot.get("id"), id), cb.equal(userContactRoot.get("user").get("id"), user.getId()));
        if (entityManager.createQuery(criteriaDelete).executeUpdate() != 1) {
            return new ResponseEntity<>("Bad request!", HttpStatus.BAD_REQUEST);
        }
        CriteriaQuery<UserContact> criteriaSelect = cb.createQuery(UserContact.class);
        Root<UserContact> userContact = criteriaSelect.from(UserContact.class);
        criteriaSelect.where(cb.equal(userContact.get("user").get("id"), user.getId()));

        if (numbersContact(user.getId(), cb) <= 0) {
            throw new ExceptionRollBack("It last contact!", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Success!");

    }

    @Transactional(rollbackOn = ExceptionRollBack.class)
    public ResponseEntity<?> updateContactInfo(Long id, String login, ContactInfoDto contactInfoDto) {
        if (userContactRepo.findByContactInfo(contactInfoDto.getContact()).isPresent()) {
            return new ResponseEntity<>("Контакт уже занят", HttpStatus.BAD_REQUEST);
        }
        UserContact contact = searchUtil.getContact(contactInfoDto.getContact());
        Optional<UserContact> userContact = userContactRepo.findById(id);
        Optional<User> user = userService.findUserByLogin(login);
        if (userContact.isPresent() && user.isPresent() && user.get().getUserContact().contains(userContact.get())) {
            userContact.get().setContactType(contact.getContactType());
            userContact.get().setContactInfo(contact.getContactInfo());
            try {
                entityManager.merge(userContact.get());
            } catch (IllegalArgumentException e) {
                throw new ExceptionRollBack(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(userContact.get());
        }
        return new ResponseEntity<>("Не удалось обновить!", HttpStatus.BAD_REQUEST);

    }

    public Collection<Long> findBySearch(String contact, String fullName, LocalDate date, Pageable pageable) {
        if (pageable.getPageNumber() < 1) {
            throw new NotFoundException("Wrong page or request", HttpStatus.BAD_REQUEST);
        }
        checkSortParameters(pageable.getSort());
        if (contact == null && fullName == null && date == null) {
            throw new NotFoundException("Bad request!", HttpStatus.BAD_REQUEST);
        }
        if (fullName == null && date == null) {
            searchUtil.getContact(contact);
            return Collections.singleton(findByContact(contact).getUser().getId());
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaSelect = cb.createQuery(Long.class);
        Root<UserInfo> clientAccountRoot = criteriaSelect.from(UserInfo.class);

        List<Predicate> predicates = new ArrayList<>();

        if (fullName != null) {
            Expression<String> contactWsFullName = cb.function("CONCAT_WS", String.class,
                    cb.literal(" "),
                    clientAccountRoot.get("firstName"),
                    clientAccountRoot.get("secondName"),
                    clientAccountRoot.get("middleName"));
            criteriaSelect.select(clientAccountRoot.get("user").get("id"));
            predicates.add(cb.like(contactWsFullName, "%" + fullName + "%"));
        }

        if (date != null) {
            predicates.add(cb.lessThan(clientAccountRoot.<LocalDate>get("dateOfBirth"), date));
        }

        if (contact != null) {
            searchUtil.getContact(contact);
            Subquery<Long> sub = criteriaSelect.subquery(Long.class);
            Root<UserContact> subInfoRoot = sub.from(UserContact.class);
            sub.select(subInfoRoot.get("user").get("id"))
                    .where(cb.equal(subInfoRoot.get("contactInfo"), contact));
            predicates.add(cb.equal(clientAccountRoot.get("user").get("id"), sub));
        }

        CriteriaQuery<Long> select = criteriaSelect
                .multiselect(clientAccountRoot.get("user").get("id"))
                .where(
                        cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Long> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());
        criteriaSelect.orderBy(getSort(pageable.getSort(), clientAccountRoot, cb));
        List<Long> results = typedQuery.getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException("Не существует страница или запрос!", HttpStatus.BAD_REQUEST);
        }
        return results;

    }

    private void checkSortParameters(Sort sort) {
        Set<String> fields = Arrays.stream(UserInfo.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
        sort.stream().forEach(t -> {
            if (!fields.contains(t.getProperty())) {
                throw new NotFoundException("Нет сортировки по такому параметру: " + t, HttpStatus.BAD_REQUEST);

            }
        });
    }

    private List<Order> getSort(Sort sort, Root<UserInfo> clientAccountRoot, CriteriaBuilder cb) {
        return sort.stream().map(t -> {
            if (t.getDirection().isAscending()) {
                return cb.asc(clientAccountRoot.get(t.getProperty()));
            } else {
                return cb.desc(clientAccountRoot.get(t.getProperty()));
            }
        }).toList();
    }
}
