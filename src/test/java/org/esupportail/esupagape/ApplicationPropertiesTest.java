package org.esupportail.esupagape;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.service.ldap.PersonLdap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationProperties.class)
@TestPropertySource(locations = "/application.properties")
public class ApplicationPropertiesTest {

    @Resource
    private ApplicationProperties applicationProperties;

    @Test
    public void checkDisplayPhotoConfiguration() {
        if(!applicationProperties.getDisplayPhotoUriPattern().isEmpty() && applicationProperties.getMappingPhotoIdToLdapField() != null) {
            assert (Arrays.stream(PersonLdap.class.getDeclaredFields()).anyMatch(field -> field.getName().equals(applicationProperties.getMappingPhotoIdToLdapField())));
        }
    }

}
