package org.esupportail.esupagape.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.esupportail.esupagape.entity.LibelleAmenagement;
import org.esupportail.esupagape.repository.LibelleAmenagementRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    private final LibelleAmenagementRepository libelleAmenagementRepository;

    public CsvImportService(LibelleAmenagementRepository libelleAmenagementRepository) {
        this.libelleAmenagementRepository = libelleAmenagementRepository;
    }


    public void importCsvLibelleAmenagement(MultipartFile file) throws IOException {
        libelleAmenagementRepository.deleteAllInBatch();
        CSVFormat.Builder csvFormat = CSVFormat.Builder.create(CSVFormat.DEFAULT);
        csvFormat.setDelimiter(";");
        csvFormat.setSkipHeaderRecord(true);
        List<CSVRecord> csvRecords = csvFormat.build().parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)).getRecords();
        List<LibelleAmenagement> libelleAmenagements = new ArrayList<>();
        for (CSVRecord csvRecord : csvRecords) {
            String order = StringUtils.trimToNull(csvRecord.get(0));
            String title = StringUtils.trimToNull(csvRecord.get(1));
            LibelleAmenagement libelleAmenagement = new LibelleAmenagement();
            libelleAmenagement.setTitle(title);
            libelleAmenagement.setOrderIndex(Integer.valueOf(order));
            libelleAmenagements.add(libelleAmenagement);
        }
        libelleAmenagementRepository.saveAll(libelleAmenagements);
    }
}


