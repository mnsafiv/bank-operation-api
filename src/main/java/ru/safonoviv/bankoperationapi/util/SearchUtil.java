package ru.safonoviv.bankoperationapi.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.safonoviv.bankoperationapi.dto.SearchType;
import ru.safonoviv.bankoperationapi.entity.ContactType;
import ru.safonoviv.bankoperationapi.entity.UserContact;
import ru.safonoviv.bankoperationapi.exceptions.NotFoundException;

import java.util.regex.Pattern;

@Component
public class SearchUtil {

    private final Pattern patternPhoneNumber;
    private static Pattern patternDate;
    private final Pattern patternEmail;

    public SearchUtil() {
        patternPhoneNumber = Pattern.compile("^(\\+?\\d{1,2}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");
        patternDate = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        patternEmail = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    }

    public SearchType searchType(String search) {
        if (validatePhoneNumber(search))
            return SearchType.phone;

        if (validateDate(search))
            return SearchType.dateOfBirth;

        if (validateEmail(search))
            return SearchType.email;

        return SearchType.fullName;


    }

    public UserContact getContact(String search) throws NotFoundException{
        ContactType contactType;
        if (validatePhoneNumber(search)) {
            contactType = ContactType.phone;
        } else if (validateEmail(search)){
                contactType = ContactType.email;
        }else throw new NotFoundException("No correct contact: "+search, HttpStatus.BAD_REQUEST);

        return UserContact.builder()
                .contactType(contactType)
                .contactInfo(search)
                .build();


    }

    private boolean validatePhoneNumber(String phoneNumber) {
        return patternPhoneNumber.matcher(phoneNumber).find();
    }

    private boolean validateEmail(String date) {
        return patternEmail.matcher(date).find();
    }

    private boolean validateDate(String date) {
        return patternDate.matcher(date).find();
    }

}
