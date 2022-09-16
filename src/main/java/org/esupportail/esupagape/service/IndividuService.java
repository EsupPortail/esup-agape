package org.esupportail.esupagape.service;

import org.esupportail.esupagape.service.interfaces.importIndividu.ImportIndividuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class IndividuService {

    @Resource
    private List<ImportIndividuService> importIndividuServices;




}
