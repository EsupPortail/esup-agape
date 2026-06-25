package org.esupportail.esupagape.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.esupportail.esupagape.entity.Year;
import org.esupportail.esupagape.exception.AgapeJpaException;
import org.esupportail.esupagape.repository.YearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class UtilsService {

    private final YearRepository yearRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public UtilsService(YearRepository yearRepository) {
        this.yearRepository = yearRepository;
    }

    public static List<Map<String, String>> jsonToList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public int getCurrentYear() {
        List<Year> years = getYears();
        if(years.isEmpty()) {
            Year year = new Year(computeCurrentYear());
            yearRepository.save(year);
            return year.getNumber();
        }
        return years.get(0).getNumber();
    }

    public int computeCurrentYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if(Calendar.getInstance().get(Calendar.MONTH)<Calendar.AUGUST){
            year--;
        }
        return year;
    }

    public List<Year> getYears() {
         return yearRepository.findAll().stream().sorted(Comparator.comparingInt(Year::getNumber).reversed()).toList();
    }

    @Transactional
    public void addYear(Integer number) throws AgapeJpaException {
        if(getYears().stream().noneMatch(year -> year.getNumber().equals(number))) {
            Year year = new Year(number);
            yearRepository.save(year);
        } else {
            throw new AgapeJpaException("Ajout impossible, cette année existe déjà");
        }
    }

    @Transactional
    public void deleteYear(Long id) {
        Year year = yearRepository.findById(id).orElseThrow();
        yearRepository.delete(year);
    }

    public void copyFileStreamToHttpResponse(String name, String contentType, InputStream inputStream, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType(contentType);
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));
        IOUtils.copyLarge(inputStream, httpServletResponse.getOutputStream());
    }

}
