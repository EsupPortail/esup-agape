package org.esupportail.esupagape.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.esupportail.esupagape.exception.AgapeIOException;
import org.esupportail.esupagape.repository.EnqueteEnumFilFmtScoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    private final EnqueteEnumFilFmtScoRepository enqueteEnumFilFmtScoRepository;
    private final JdbcTemplate jdbcTemplate;

    public CsvImportService(EnqueteEnumFilFmtScoRepository enqueteEnumFilFmtScoRepository, JdbcTemplate jdbcTemplate) {
        this.enqueteEnumFilFmtScoRepository = enqueteEnumFilFmtScoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


    public void importCsv(MultipartFile file) throws IOException {

        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreEmptyLines()
                .withNullString("");

        List<CSVRecord> csvRecords = csvFormat.parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)).getRecords();
        List<Object[]> records = new ArrayList<>();
        for (CSVRecord csvRecord : csvRecords) {
            Long id = null;
            try {
                id = Long.parseLong(csvRecord.get(0));
            } catch (NumberFormatException e) {
                throw new AgapeIOException(e.getMessage(), e);
            }
            String cod_fil = StringUtils.trimToNull(csvRecord.get(1));
            String cod_fmt = StringUtils.trimToNull(csvRecord.get(2));
            String cod_sco = "";
            if (csvRecord.size() > 3) {
                cod_sco = StringUtils.trimToNull(csvRecord.get(3));
            }
            records.add(new Object[]{id, cod_fil, cod_fmt, cod_sco});
        }

        String sql = "INSERT INTO enquete_enum_fil_fmt_sco(id, cod_fil, cod_fmt, cod_sco) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, records);
    }

    public void importCsvLibelle(MultipartFile file) throws IOException {

        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreEmptyLines()
                .withNullString("");

        List<CSVRecord> csvRecords = csvFormat.parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)).getRecords();
        List<Object[]> records = new ArrayList<>();
        for (CSVRecord csvRecord : csvRecords) {
            Long id = null;
            try {
                id = Long.parseLong(csvRecord.get(0));
            } catch (NumberFormatException e) {
                throw new AgapeIOException(e.getMessage(), e);
            }
            String cod = StringUtils.trimToNull(csvRecord.get(1));
            String libelle = StringUtils.trimToNull(csvRecord.get(2));

            records.add(new Object[]{id, cod, libelle});
        }

        String sql = "INSERT INTO enquete_enum_fil_fmt_sco_libelle(id, cod, libelle) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, records);
    }
}


