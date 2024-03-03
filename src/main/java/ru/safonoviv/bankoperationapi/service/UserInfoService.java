package ru.safonoviv.bankoperationapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.safonoviv.bankoperationapi.entity.UserInfo;
import ru.safonoviv.bankoperationapi.exceptions.NotFoundException;
import ru.safonoviv.bankoperationapi.repository.UserInfoRepository;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserInfoService {
    @PersistenceContext
    private EntityManager entityManager;


    public Collection<Long> findUsersIdSortedByDateOfBorn(String search, Pageable pageable) {
        if (pageable.getPageNumber() < 1) {
            throw new NotFoundException("Wrong page or request", HttpStatus.BAD_REQUEST);
        }
        checkSortParameters(pageable.getSort());
        LocalDate localDate = LocalDate.parse(search);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaSelect = cb.createQuery(Long.class);
        Root<UserInfo> clientAccountRoot = criteriaSelect.from(UserInfo.class);

        criteriaSelect.select(clientAccountRoot.get("user").get("id"));
        criteriaSelect.where(cb.lessThan(clientAccountRoot.<LocalDate>get("dateOfBirth"), localDate));
        criteriaSelect.orderBy(getSort(pageable.getSort(),clientAccountRoot,cb));

        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaSelect);
        typedQuery.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());
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

    public Collection<Long> findUsersIdSorted(String search, Pageable pageable) {
        if (pageable.getPageNumber() < 1) {
            throw new NotFoundException("Не существует страница!", HttpStatus.BAD_REQUEST);
        }
        checkSortParameters(pageable.getSort());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaSelect = cb.createQuery(Long.class);
        Root<UserInfo> clientAccountRoot = criteriaSelect.from(UserInfo.class);

        Expression<String> concatWsGenre = cb.function("CONCAT_WS", String.class,
                cb.literal(" "),
                clientAccountRoot.get("firstName"),
                clientAccountRoot.get("secondName"),
                clientAccountRoot.get("middleName"));
        criteriaSelect.select(clientAccountRoot.get("user").get("id"));

        criteriaSelect.where(cb.like(concatWsGenre, "%" + search + "%"));
        criteriaSelect.orderBy(getSort(pageable.getSort(),clientAccountRoot,cb));

        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaSelect);
        typedQuery.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Long> results = typedQuery.getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException("Не существует страница или запрос!", HttpStatus.BAD_REQUEST);
        }
        return results;
    }

    private List<Order> getSort(Sort sort, Root<UserInfo> clientAccountRoot, CriteriaBuilder cb) {
        return sort.stream().map(t->{
            if(t.getDirection().isAscending()){
                return cb.asc(clientAccountRoot.get(t.getProperty()));
            }else {
                return cb.desc(clientAccountRoot.get(t.getProperty()));
            }
        }).toList();
    }
}
