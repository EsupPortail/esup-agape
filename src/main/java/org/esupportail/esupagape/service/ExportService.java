package org.esupportail.esupagape.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang3.BooleanUtils;
import org.esupportail.esupagape.config.ApplicationProperties;
import org.esupportail.esupagape.dtos.csvs.EnqueteExportCsv;
import org.esupportail.esupagape.entity.Enquete;
import org.esupportail.esupagape.exception.AgapeException;
import org.esupportail.esupagape.exception.AgapeRuntimeException;
import org.esupportail.esupagape.repository.ExportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(EnqueteService.class);

    private final ApplicationProperties applicationProperties;

    private final ExportRepository exportRepository;

    private final EnqueteService enqueteService;

    public ExportService(ApplicationProperties applicationProperties, ExportRepository exportRepository,
        EnqueteService enqueteService) {
        this.applicationProperties = applicationProperties;
        this.exportRepository = exportRepository;
        this.enqueteService = enqueteService;
    }

    private final Map<String, String> dossierCompletCsv = new LinkedHashMap<>() {{
        put("yearOfBirth", "Année de naissance");
        put("gender", "Genre");
        put("fixCP", "Code postal");
        put("fixCity", "Ville");
        put("fixCountry", "Pays");
        put("type", "Type de l'individu");
        put("statusDossier", "Statut du dossier");
        put("statusDossierAmenagement", "Statut du Dossier Aménagement");
        put("classifications", "Classification du handicap");
        put("mdph", "Dossier MDPH");
        put("taux", "Taux");
        put("typeSuiviHandisup", "Type de suivi Handisup");
        put("typeFormation", "Type de formation");
        put("modeFormation", "Modalités de formation");
        put("libelleFormation", "Formation");
        put("libelleFormationPrec", "Formation précédente");
        put("codComposante", "Code composante");
        put("composante", "Composante");
        put("formAddress", "Adresse de formation");
        put("resultatTotal", "Resultat total");

    }};

    @Transactional
    public void getCsvDossier(Integer year, Writer writer) {
        writeObjectListToCsv(exportRepository.findByYearForCSV(year), dossierCompletCsv, writer);
    }

    private final Map<String, String> enqueteCsv = new LinkedHashMap<>() {{
        put("nfic", "Nfic");
        put("id", "Id");
        put("numetu", "Numéro étudiant");
        put("an", "Année");
        put("sexe", "Sexe");
        put("typFrmn", "Type formation");
        put("modFrmn", "Modalité formation");
        put("alternance", "Alternance");
        put("codSco", "Année d'études");
        put("codFmt", "Formation");
        put("codFil", "Discipline");
        put("codHd", "Typologie de trouble");
        put("hdTmp", "Handicap temporaire");
        put("com", "Commentaire");
        put("codPfpp", "Plan d'accompagnement");
        put("codPfas", "Aménagement du cursus de formation");
        put("codMeahF", "Mesures aides humaines");
//        put("interpH", "supprimé");
//        put("codeurH", "supprimé");
        put("aidHnat", "Autre aide humaine");
        put("codMeae", "Aménagement des examens");
        put("autAE", "Autre aménagement des examens");
        put("codMeaa", "Autres aides");
        put("autAA", "Autres (à préciser");
        put("codAmL", "Autres mesures relevant ou non de la compétence de la CDAPH");
        put("finished", "Terminée");
        put("link", "Lien");
    }};

    @Transactional
    public void findEnqueteByYearForCSV(Integer year, Writer writer, boolean partial) throws AgapeException {
        List<EnqueteExportCsv> enqueteExportCsvs = new ArrayList<>();
        List<Enquete> enquetes = enqueteService.findAllByDossierYear(year);
        if(!partial && (enquetes.stream().anyMatch(enquete -> enquete.getFinished() == null || !enquete.getFinished()))) {
            throw new AgapeException("Certaines enquêtes ne sont pas complètes");
        }
        int id = 1;
        for(Enquete enquete : enquetes) {
            EnqueteExportCsv enqueteExportCsv = new EnqueteExportCsv(
                    "1",
                    applicationProperties.getCodeEtab(),
                    String.valueOf(id + Integer.parseInt(year + "0000")),
                    enquete.getAn(),
                    enquete.getSexe(),
                    enquete.getTypFrmn() != null ? enquete.getTypFrmn().name().toLowerCase() : "",
                    enquete.getModFrmn() != null ? enquete.getModFrmn().name().toLowerCase() : "",
                    (BooleanUtils.isTrue(enquete.getAlternance())) ? "1" : "",
                    enquete.getCodSco() != null ? enquete.getCodSco().toLowerCase() : "",
                    enquete.getCodFmt() != null ? enquete.getCodFmt().toLowerCase() : "",
                    enquete.getCodFil() != null ? enquete.getCodFil().toLowerCase() : "",
                    enquete.getCodHd() != null ? enquete.getCodHd().name().toLowerCase() : "",
                    (enquete.getHdTmp()) ? "1" : "",
                    enquete.getCom(),
                    enquete.getCodPfpp() != null ? enquete.getCodPfpp().name().toLowerCase() : "",
                    String.join("" ,enquete.getCodPfas().stream().map(codPfas -> codPfas.name().toLowerCase()).sorted(String::compareTo).toList()),
                    String.join("" ,enquete.getCodMeahF().stream().map(codMeahF -> codMeahF.name().toLowerCase()).sorted(String::compareTo).toList()),
                    "",
                    "",
                    enquete.getAidHNat(),
                    String.join("" ,enquete.getCodMeae().stream().map(codMeae -> codMeae.name().toLowerCase()).sorted(String::compareTo).toList()),
                    enquete.getAutAE(),
                    String.join("" ,enquete.getCodMeaa().stream().map(codMeaa -> codMeaa.name().toLowerCase()).sorted(String::compareTo).toList()),
                    enquete.getAutAA(),
                    String.join("" ,enquete.getCodAmL().stream().map(codAmL -> codAmL.name().toLowerCase()).sorted(String::compareTo).toList()),
                    BooleanUtils.isTrue(enquete.getFinished()) ? "oui" : "non",
                    applicationProperties.getPath() + "/dossiers/" + enquete.getDossier().getId() + "/enquete"
            );
            enqueteExportCsvs.add(enqueteExportCsv);
            id++;
        }
        writeObjectListToCsv(enqueteExportCsvs, enqueteCsv, writer);
    }

    public <T> void writeObjectListToCsv(List<T> objectList, Map<String, String> fieldsHeaders, Writer writer) {
        CSVFormat.Builder csvFormat = CSVFormat.Builder.create(CSVFormat.EXCEL);
        csvFormat.setDelimiter(";");
        csvFormat.setQuote('"');
        csvFormat.setQuoteMode(QuoteMode.ALL);
        csvFormat.setHeader(fieldsHeaders.values().toArray(String[]::new));
        try {
            CSVPrinter printer = new CSVPrinter(writer, csvFormat.build());
            for (Object object : objectList) {
                List<String> record = new ArrayList<>();
                for (String methodName : fieldsHeaders.keySet()) {
                    try {
                        record.add(object.getClass().getDeclaredMethod("get" + StringUtils.capitalize(methodName)).invoke(object).toString());
                    } catch (Exception e) {
                        record.add("");
                        logger.debug(methodName + " doesn't exist");
                    }
                }
                printer.printRecord(record);
            }
        } catch (IOException e) {
            throw new AgapeRuntimeException("Enable to write export csv");
        }
    }

}