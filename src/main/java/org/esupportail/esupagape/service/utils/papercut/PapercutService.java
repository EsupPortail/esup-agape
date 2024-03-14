/**
 * Licensed to EsupPortail under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * EsupPortail licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.esupagape.service.utils.papercut;

import org.esupportail.esupagape.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Vector;

@Service
public class PapercutService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ApplicationProperties applicationProperties;

    ServerCommandProxy papercutProxy;

    public PapercutService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        papercutProxy = new ServerCommandProxy(applicationProperties.getPapercutServer(), applicationProperties.getPapercutScheme(), applicationProperties.getPapercutPort(), applicationProperties.getPapercutAuthToken());
    }

    public UserPapercutInfos getUserPapercutInfos(String uid) {
        Vector<String> propertyValues = papercutProxy.getUserProperties(uid, UserPapercutInfos.propertyNames);
        UserPapercutInfos userPapercutInfos = new UserPapercutInfos(uid, propertyValues);
        log.debug("userPapercutInfos de " + uid + " = " + userPapercutInfos);
        return userPapercutInfos;
    }

    public void creditUserBalance(String uid, double amount) {
        String logMessage = String.format("Ajout de %s à %s via l'appli Esup-Agape", amount, uid);
        log.info(logMessage);
        papercutProxy.adjustUserAccountBalance(uid, amount, logMessage, applicationProperties.getPapercutAccountName());
    }

}