package org.esupportail.esupagape.service.ldap;

import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;


public class PersonLdapAttributesMapper implements AttributesMapper<PersonLdap> {

    public PersonLdap mapFromAttributes(Attributes attrs) throws NamingException {
        PersonLdap person = new PersonLdap();
        person.setCn(attrs.get("cn").get().toString());
        Attribute uid = attrs.get("uid");
        if (uid != null){
            person.setUid(uid.get().toString());
        }
        Attribute sn = attrs.get("sn");
        if (sn != null){
            person.setSn(sn.get().toString());
        }
        Attribute givenName = attrs.get("givenName");
        if (givenName != null){
            person.setGivenName(givenName.get().toString());
        }
        Attribute displayName = attrs.get("displayName");
        if (displayName != null){
            person.setDisplayName(displayName.get().toString());
        }
        Attribute mail = attrs.get("mail");
        if (mail != null){
            person.setMail(mail.get().toString());
        }
        Attribute eduPersonPrincipalName = attrs.get("eduPersonPrincipalName");
        if (eduPersonPrincipalName != null){
            person.setEduPersonPrincipalName(eduPersonPrincipalName.get().toString());
        }
        Attribute supannEntiteAffectationPrincipale = attrs.get("supannEntiteAffectationPrincipale");
        if (supannEntiteAffectationPrincipale != null){
            person.setSupannEntiteAffectationPrincipale(supannEntiteAffectationPrincipale.get().toString());
        }
        Attribute supannEntiteAffectation = attrs.get("supannEntiteAffectation");
        if (supannEntiteAffectation != null){
            person.setSupannEntiteAffectation(supannEntiteAffectation.get().toString());
        }
        return person;
    }
}
