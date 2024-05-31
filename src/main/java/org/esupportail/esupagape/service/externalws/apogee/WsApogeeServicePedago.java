package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.ContratPedagogiqueResultatElpEprDTO5;
import org.esupportail.esupagape.exception.AgapeApogeeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnBean(ApogeePedagoFactory.class)
public class WsApogeeServicePedago {

	private static final Logger logger = LoggerFactory.getLogger(WsApogeeServicePedago.class);

	private final ApogeePedagoFactory apogeePedagoFactory;

	public WsApogeeServicePedago(ApogeePedagoFactory apogeePedagoFactory) {
		this.apogeePedagoFactory = apogeePedagoFactory;
	}

	public ContratPedagogiqueResultatElpEprDTO5[] recupererResultatsElpEprDTO(String codEtu, String annee, String codEtp, String codVrsVet) throws AgapeApogeeException {
		logger.debug("recup des resultats dans apogee.");
		try {
			List<ContratPedagogiqueResultatElpEprDTO5> resultatElp = apogeePedagoFactory.getInstancePedago().recupererContratPedagogiqueResultatElpEprV6(codEtu, annee, codEtp, codVrsVet, "Apogee", null, null, null, null);
			return resultatElp.toArray(new ContratPedagogiqueResultatElpEprDTO5[0]);
		} catch (Exception e) {
			throw new AgapeApogeeException("Erreur lors de la recup des infos pedago Elp : " + codEtu + " error : " + e.getMessage());
		}
	}

}
