package org.esupportail.esupagape.service.utils;

import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class UtilsService {

    public int getCurrentYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if(Calendar.getInstance().get(Calendar.MONTH)<Calendar.AUGUST){
            year--;
        }
        return year;
    }

}
