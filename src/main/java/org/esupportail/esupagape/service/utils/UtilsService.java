package org.esupportail.esupagape.service.utils;

import org.esupportail.esupagape.entity.Year;
import org.esupportail.esupagape.repository.YearRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

@Service
public class UtilsService {

    private final YearRepository yearRepository;

    public UtilsService(YearRepository yearRepository) {
        this.yearRepository = yearRepository;
    }

    public int getCurrentYear() {
        List<Year> years = getYears();
        if(years.size() == 0) {
            Year year = new Year();
            year.setNumber(computeCurrentYear());
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
}
