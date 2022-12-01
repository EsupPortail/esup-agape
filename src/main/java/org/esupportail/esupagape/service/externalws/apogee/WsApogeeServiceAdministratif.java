package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.AdministratifMetier.InsAdmEtpDTO3;
import org.esupportail.esupagape.exception.AgapeApogeeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnBean(ApogeeAdministratifFactory.class)
public class WsApogeeServiceAdministratif {

	private static final Logger logger = LoggerFactory.getLogger(WsApogeeServiceAdministratif.class);

	private final ApogeeAdministratifFactory apogeeAdministratifFactory;

	public WsApogeeServiceAdministratif(ApogeeAdministratifFactory apogeeAdministratifFactory) {
		this.apogeeAdministratifFactory = apogeeAdministratifFactory;
	}

	public List<InsAdmEtpDTO3> recupererIAEtapes(String codEtu, String annee) throws AgapeApogeeException {
		logger.debug("recup des données administratives dans apoge : " + codEtu);
		try {
			return apogeeAdministratifFactory.getInstanceAdministratif().recupererIAEtapesV3(codEtu, annee, null, null);
		} catch (Exception e) {
			throw new AgapeApogeeException("Erreur lors de la recup des infos administratives : " + codEtu + " error : " + e.getMessage());
		}
	}

}
