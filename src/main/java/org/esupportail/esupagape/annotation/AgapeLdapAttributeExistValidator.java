package org.esupportail.esupagape.annotation;

import org.esupportail.esupagape.service.ldap.PersonLdap;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class AgapeLdapAttributeExistValidator implements ConstraintValidator<AgapeLdapAttributExist, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || Arrays.stream(PersonLdap.class.getDeclaredFields()).anyMatch(field -> field.getName().equals(value));
    }
}
