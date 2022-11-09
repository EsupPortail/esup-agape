package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.EtudiantMetier.CoordonneesDTO2;
import gouv.education.apogee.commun.client.ws.EtudiantMetier.EtudiantMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.EtudiantMetier.IdentifiantsEtudiantDTO2;
import gouv.education.apogee.commun.client.ws.EtudiantMetier.InfoAdmEtuDTO4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class WsApogeeServiceEtudiant {

	private static final Logger logger = LoggerFactory.getLogger(WsApogeeServiceEtudiant.class);

	@Resource
	EtudiantMetierServiceInterface apogeeProxyEtu;

	public String recupererIdentifiantsEtudiant(String nom, String prenom,
			String dateNaiss) {
		String idEtu = "";
		try {
			if(!dateNaiss.equals("%")) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
				Date date = formatter.parse(dateNaiss);
				DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
				dateNaiss = dateFormat.format(date);
			}
			System.err.println(dateNaiss);
			IdentifiantsEtudiantDTO2 identifiantsEtudiantDTO = apogeeProxyEtu
					.recupererIdentifiantsEtudiantV2(null, null, null, null,
							null, nom, prenom, dateNaiss, null);
			idEtu = identifiantsEtudiantDTO.getCodEtu().toString();
		} catch (Exception e) {
			logger.error("Erreur lors de la recup des infos", e);
		}
		logger.info("recup infos etudiant dans apogee.");
		return idEtu;

	}
	
	public InfoAdmEtuDTO4 recupererInfosAdmEtu(String codEtu) {
		InfoAdmEtuDTO4 infoEtudiant = null;
		try {
			infoEtudiant = apogeeProxyEtu.recupererInfosAdmEtuV4(codEtu);
		} catch (Exception e) {
			logger.warn("Erreur lors de la recup des infos", e);
		}
		logger.info("recup infos administratives dans apogee.");
		return infoEtudiant;
	}

	public CoordonneesDTO2 recupererAdressesEtudiant(String codEtu, String annee) {
		CoordonneesDTO2 adresseEtudiant = null;
		try {
			adresseEtudiant = apogeeProxyEtu.recupererAdressesEtudiantV2(codEtu, annee, "O");
		} catch (Exception e) {
			logger.warn("Erreur lors de la recup des infos", e);
		}
		logger.info("recup infos adresse dans apogee.");
		return adresseEtudiant;
	}
	
	public String recupererLogin(String nom, String prenom,
			String dateNaiss) {
		String login = "";
		try {
		if(!dateNaiss.equals("%")) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
			Date date = formatter.parse(dateNaiss);
			DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
			dateNaiss = dateFormat.format(date);
		}
		IdentifiantsEtudiantDTO2 identifiantsEtudiantDTO = apogeeProxyEtu.recupererIdentifiantsEtudiantV2(
				null, null, null, null, null, nom, prenom,
				dateNaiss, null);
		login = identifiantsEtudiantDTO.getLoginAnnuaire();
		} catch (Exception e) {
			logger.error("Erreur lors de la recup du login", e);
		}
		return login;
	}

}
