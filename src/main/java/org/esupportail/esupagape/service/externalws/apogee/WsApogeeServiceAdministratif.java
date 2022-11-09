package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.AdministratifMetier.AdministratifMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.AdministratifMetier.InsAdmEtpDTO3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@ConditionalOnProperty(value = {"apogee.etu-url", "apogee.administratif-url", "apogee.pedago-url"})
public class WsApogeeServiceAdministratif {

	private static final Logger logger = LoggerFactory.getLogger(WsApogeeServiceAdministratif.class);

	@Resource
	AdministratifMetierServiceInterface apogeeProxyAdministratif;

	public List<InsAdmEtpDTO3> recupererIAEtapes(String codEtu, String annee) {
		logger.info("recup des données administratives dans apogee.");
		List<InsAdmEtpDTO3> ieEtapes = null;
		try {
			ieEtapes = apogeeProxyAdministratif.recupererIAEtapesV3(codEtu, annee, null, null);
		} catch (Exception e) {
			logger.warn("Erreur lors de la recup des infos administratives");
		}
		return ieEtapes;
	}

}
