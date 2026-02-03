update dossier_mdphs set mdphs = 'EN_COURS_DE_CONSTITUTION' where mdphs = 'EN_COURS_DE_CONSTITUTION_HANDISUP';
update dossier_mdphs set mdphs = 'ENVOYE_HANDISUP' where mdphs = 'DOSSIER_TRANSMIS';
update dossier_mdphs set mdphs = 'CARTE_INVALIDITE_PRIORITE' where mdphs = 'CARTE_PRIORITE';
delete from dossier_mdphs where mdphs = 'OUI';
delete from dossier_mdphs where mdphs = 'OUI_HANDISUP';