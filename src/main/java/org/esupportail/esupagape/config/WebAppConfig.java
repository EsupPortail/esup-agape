package org.esupportail.esupagape.config;

import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.dialect.springdata.SpringDataDialect;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class WebAppConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebAppConfig.class);

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        registry.addFormatterForFieldType(LocalDateTime.class, new Formatter<LocalDateTime>() {
            @Override
            public LocalDateTime parse(String text, Locale locale) throws ParseException {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter formatterWithHour = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                try {
                    return LocalDate.parse(text, formatter).atStartOfDay();
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }
                try {
                    return LocalDateTime.parse(text, formatterWithHour);
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }
                throw new AgapeRuntimeException("unable to parse date " + text);
            }

            @Override
            public String print(LocalDateTime object, Locale locale) {
                return object.toString();
            }

        });
    }

    @Bean
    public SpringDataDialect springDataDialect() {
        return new SpringDataDialect();
    }

}
