package org.esupportail.esupagape.service.externalws.apogee;

import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.ContratPedagogiqueResultatElpEprDTO5;
import gouv.education.apogee.commun.client.ws.PedagogiqueMetier.PedagogiqueMetierServiceInterface;
import org.esupportail.esupagape.exception.AgapeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WsApogeeServicePedago {

	private static final Logger logger = LoggerFactory.getLogger(WsApogeeServicePedago.class);

	@Resource
	PedagogiqueMetierServiceInterface apogeeProxyPedago;

	public ContratPedagogiqueResultatElpEprDTO5[] recupererResultatsElpEprDTO(String codEtu, String annee, String codEtp, String codVrsVet) throws AgapeException {
		logger.info("recup des resultats dans apogee.");
		try {
			List<ContratPedagogiqueResultatElpEprDTO5> resultatElp = apogeeProxyPedago.recupererContratPedagogiqueResultatElpEprV6(codEtu, annee, codEtp, codVrsVet, "Apogee", null, null, null, null);
			return resultatElp.toArray(new ContratPedagogiqueResultatElpEprDTO5[resultatElp.size()]);
		} catch (Exception e) {
			throw new AgapeException("Erreur lors de la recup des infos pedago Elp");
		}

	}
	

}
