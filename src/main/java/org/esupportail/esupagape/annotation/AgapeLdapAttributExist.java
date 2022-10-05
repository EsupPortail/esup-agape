package org.esupportail.esupagape.annotation;

import org.esupportail.esupagape.annotation.aspect.AgapeLdapAttributeExistValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Target( { FIELD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AgapeLdapAttributeExistValidator.class)
public @interface AgapeLdapAttributExist {

    String message() default "Attribute doesn't exist in LDAP";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
