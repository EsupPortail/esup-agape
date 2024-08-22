package org.esupportail.esupagape.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.esupportail.esupagape.entity.EnqueteEnumFilFmtSco;
import org.esupportail.esupagape.entity.EnqueteEnumFilFmtScoLibelle;
import org.esupportail.esupagape.entity.LibelleAmenagement;
import org.esupportail.esupagape.repository.EnqueteEnumFilFmtScoLibelleRepository;
import org.esupportail.esupagape.repository.EnqueteEnumFilFmtScoRepository;
import org.esupportail.esupagape.repository.LibelleAmenagementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    private final EnqueteEnumFilFmtScoRepository enqueteEnumFilFmtScoRepository;
    private final EnqueteEnumFilFmtScoLibelleRepository enqueteEnumFilFmtScoLibelleRepository;
    private final LibelleAmenagementRepository libelleAmenagementRepository;

    public CsvImportService(EnqueteEnumFilFmtScoRepository enqueteEnumFilFmtScoRepository,
                            EnqueteEnumFilFmtScoLibelleRepository enqueteEnumFilFmtScoLibelleRepository, LibelleAmenagementRepository libelleAmenagementRepository) {
        this.enqueteEnumFilFmtScoRepository = enqueteEnumFilFmtScoRepository;
        this.enqueteEnumFilFmtScoLibelleRepository = enqueteEnumFilFmtScoLibelleRepository;
        this.libelleAmenagementRepository = libelleAmenagementRepository;
    }

    @Transactional
    public void importCsv(MultipartFile file) throws IOException {
        enqueteEnumFilFmtScoRepository.deleteAllInBatch();
        CSVFormat.Builder csvFormat = CSVFormat.Builder.create(CSVFormat.DEFAULT);
        csvFormat.setDelimiter(";");
        csvFormat.setSkipHeaderRecord(true);
        List<CSVRecord> csvRecords = csvFormat.build().parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)).getRecords();
        List<EnqueteEnumFilFmtSco> enqueteEnumFilFmtScos = new ArrayList<>();
        for (CSVRecord csvRecord : csvRecords) {
            String cod_fil = StringUtils.trimToNull(csvRecord.get(0));
            String cod_fmt = StringUtils.trimToNull(csvRecord.get(1));
            String cod_sco = StringUtils.trimToNull(csvRecord.get(2));
            EnqueteEnumFilFmtSco enqueteEnumFilFmtSco = new EnqueteEnumFilFmtSco();
            enqueteEnumFilFmtSco.setCodFil(cod_fil);
            enqueteEnumFilFmtSco.setCodFmt(cod_fmt);
            enqueteEnumFilFmtSco.setCodSco(cod_sco);
            enqueteEnumFilFmtScos.add(enqueteEnumFilFmtSco);
        }

        enqueteEnumFilFmtScoRepository.saveAll(enqueteEnumFilFmtScos);
    }

    @Transactional
    public void importCsvLibelle(MultipartFile file) throws IOException {
        enqueteEnumFilFmtScoLibelleRepository.deleteAllInBatch();
        CSVFormat.Builder csvFormat = CSVFormat.Builder.create(CSVFormat.DEFAULT);
        csvFormat.setDelimiter(";");
        csvFormat.setSkipHeaderRecord(true);
        List<CSVRecord> csvRecords = csvFormat.build().parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)).getRecords();
        List<EnqueteEnumFilFmtScoLibelle> enqueteEnumFilFmtScoLibelles = new ArrayList<>();
        for (CSVRecord csvRecord : csvRecords) {
            String cod = StringUtils.trimToNull(csvRecord.get(0));
            String libelle = StringUtils.trimToNull(csvRecord.get(1));
            EnqueteEnumFilFmtScoLibelle enqueteEnumFilFmtScoLibelle = new EnqueteEnumFilFmtScoLibelle();
            enqueteEnumFilFmtScoLibelle.setCod(cod);
            enqueteEnumFilFmtScoLibelle.setLibelle(libelle);
            enqueteEnumFilFmtScoLibelles.add(enqueteEnumFilFmtScoLibelle);
        }
        enqueteEnumFilFmtScoLibelleRepository.saveAll(enqueteEnumFilFmtScoLibelles);
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


