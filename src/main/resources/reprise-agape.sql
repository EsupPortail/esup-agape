CREATE OR REPLACE FUNCTION migrate() returns void AS
$BODY$
declare
    e record;
    d record;
    ds record;
    s record;
    sc record;
    sm record;
    c record;
    ct record;
    am record;
    amt record;
    aidesm record;
    aidesml record;
    l record;
    contrata record;
    aidanta record;
    contratf record;
    contratpj record;
    bigfilea record;
    bigfilefeuille record;
    bigfileplanning record;
    pj record;
    feuille record;
    planing record;
    contratp record;
    periodea record;
    enquetea record;
    enquetecodamla record;
    enquetecodmeaaa record;
    enquetecodmeaea record;
    enquetecodmeahfa record;
    largeobject record;

    new_id_user bigint;
    new_id_dossier bigint;
    new_id_aide_humaine bigint;
    new_id_enquete bigint;
    new_id_big_file bigint;
    new_id_pj bigint;
    new_id_contrat_pj bigint;
    new_id_periode bigint;
    new_id_feuille bigint;
    new_id_planing bigint;
    dateenvoi date;

    numetunew varchar(255);
    type varchar(255);
    gender varchar(255);
    new_status_dossier varchar(255);
    rdv varchar(255);
    autorisation varchar(255);
    amenagement_text text;
    commentnew text;
    classificationnew varchar(255);
    mdphnew varchar(255);
    status_dossier_amenagement varchar(255);
    tempsmajorenew varchar(255);
    status_dossier_aide_humaine varchar(255);
    status_amenagement varchar(255);
    type_amenagement varchar(255);
    type_aide_materiel varchar(255);
    mois varchar(255);
    mois_paye varchar(255);
    codfilnew varchar(255);
    codfmtnew varchar(255);
    codsconew varchar(255);
    codhdnew varchar(255);
    codpfasnew varchar(255);
    codpfppnew varchar(255);
    modfrmnew varchar(255);
    typfrmnnew varchar(255);
    codmeaenew varchar(255);
    codmeaanew varchar(255);
    codmeahfnew varchar(255);
    codamlnew varchar(255);
    typepjcontrat varchar(255);
    horsunivcounter bigint;
begin
    horsunivcounter = 0;
    for e in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from etudiant') as e1(
                                                                                                               id                       bigint,
                                                                                                               adresse_annuelle         varchar(255),
                                                                                                               adresse_fixe             varchar(255),
                                                                                                               code_postal_annuel       varchar(255),
                                                                                                               code_postal_fixe         varchar(255),
                                                                                                               date_naissance           timestamp,
                                                                                                               email_etudiant           varchar(255),
                                                                                                               email_perso              varchar(255),
                                                                                                               fichier                  bytea,
                                                                                                               login                    varchar(255),
                                                                                                               nom                      varchar(255),
                                                                                                               nom_commune_annuelle     varchar(255),
                                                                                                               nom_commune_fixe         varchar(255),
                                                                                                               num_etudiant             varchar(255),
                                                                                                               prenom                   varchar(255),
                                                                                                               tel_fixe                 varchar(255),
                                                                                                               tel_portable             varchar(255),
                                                                                                               tel_responsable          varchar(255),
                                                                                                               type_individu            integer,
                                                                                                               version                  integer,
                                                                                                               sexe                     integer,
                                                                                                               warning                  boolean,
                                                                                                               textsearchable_index_col tsvector)
        loop
            new_id_user = nextval('hibernate_sequence');
            numetunew = e.num_etudiant;
            if e.num_etudiant in (select num_etudiant from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select num_etudiant as test from etudiant group by num_etudiant having count(num_etudiant) > 1') as e1(num_etudiant varchar(255))) then
                numetunew = concat(e.num_etudiant, horsunivcounter);
                horsunivcounter = horsunivcounter + 1;
            end if;
-- BOUCLE SUR LES ETUDIANTS
                gender = 'MASCULIN';
                if e.sexe = 1 then gender = 'FEMININ'; end if;
                insert into individu (id, contact_phone, date_of_birth, email_etu, eppn, first_name, fix_address, fixcp, fix_city, fix_country, fix_phone, gender, name, nationalite, num_etu, photo_id, sex) values
                    (new_id_user, e.tel_portable, e.date_naissance, e.email_etudiant, null, e.prenom, e.adresse_fixe, e.code_postal_fixe, e.nom_commune_fixe, null, e.tel_fixe, gender, e.nom, null, numetunew, null, e.sexe);
                for d in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from dossier') as d1(
                                                                                                                                                   id                       bigint,
                                                                                                                                                   annee                    varchar(255),
                                                                                                                                                   id_user                  bigint,
                                                                                                                                                    version                 integer,
                                                                                                                                                   premiere_inscription     boolean,
                                                                                                                                                   type_individu            integer) where id_user = e.id
                    loop
-- BOUCLE SUR LES DOSSIERS
                        type = 'INCONNU';
                        if d.type_individu = 0 then type = 'LYCEEN'; end if;
                        if d.type_individu = 1 then type = 'ETUDIANT'; end if;
                        if d.type_individu = 2 then type = 'HORS_UNIV'; end if;
                        new_status_dossier = null;
                        for ds in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from dossier_statut_dossier') as ds1 (dossier bigint, statut_dossier bigint) where dossier = d.id
                            loop
                                    if ds.statut_dossier = 7 or ds.statut_dossier = 1487 then new_status_dossier = 'ACCUEILLI'; end if;
                                    if ds.statut_dossier = 8 or ds.statut_dossier = 131589 then new_status_dossier = 'SUIVI'; end if;
                                    if ds.statut_dossier = 1488 then new_status_dossier = 'IMPOSSIBLE_A_CONTACTER'; end if;
                                    if ds.statut_dossier = 82731 then new_status_dossier = 'IMPORTE'; end if;
                                    if ds.statut_dossier = 104328 then new_status_dossier = 'RECU_PAR_LA_MEDECINE_PREVENTIVE'; end if;
                            end loop;
                        if (select count(*) from year where number = cast(d.annee as integer)) = 0 then insert into year (id, number) values (nextval('hibernate_sequence'), cast(d.annee as integer)); end if;
                        commentnew = null;
                        -- TODO calculer secteur disciplinaire
                        new_id_dossier = nextval('hibernate_sequence');
                        insert into dossier (id, year, individu_id, type, status_dossier, status_dossier_amenagement) values
                            (new_id_dossier, cast(d.annee as integer), new_id_user, type, new_status_dossier, 'NON');
                        for s in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from situation') as s1 (id                         bigint,
                                                                                                                                                              commentaire_classification text,
                                                                                                                                                              date_modification          timestamp,
                                                                                                                                                              id_dossier                 bigint,
                                                                                                                                                              modifier_par               varchar(255),
                                                                                                                                                              version                    integer,
                                                                                                                                                              taux                       integer) where id_dossier = d.id
                            loop
                                mdphnew = null;
                                for sm in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from situation_libellemdph') as sm1 (situation bigint,libellemdph bigint) where situation = s.id
                                    loop
                                        if sm.libellemdph = 21 then mdphnew =    'AAH'; end if;
                                        if sm.libellemdph = 22 then mdphnew =    'AEEH'; end if;
                                        if sm.libellemdph = 23 then mdphnew =    'CARTE_INVALIDITE_PRIORITE'; end if;
                                        if sm.libellemdph = 24 then mdphnew =    'RQTH'; end if;
                                        if sm.libellemdph = 25 then mdphnew =    'PCH_AIDE_HUMAINE'; end if;
                                        if sm.libellemdph = 26 then mdphnew =    'PCH_AIDE_TECHNIQUE'; end if;
                                        if sm.libellemdph = 27 then mdphnew =    'TRANSPORT_INDIVIDUEL_ADAPTE'; end if;
                                        if sm.libellemdph = 14685 then mdphnew = 'CARTE_INVALIDITE'; end if;
                                        if sm.libellemdph = 14686 then mdphnew = 'CARTE_PRIORITE'; end if;
                                        if sm.libellemdph = 11287 then mdphnew = 'OUI'; end if;
                                        if sm.libellemdph = 22504 then mdphnew = 'NON'; end if;
                                        if sm.libellemdph = 22503 then mdphnew = 'NE_SAIS_PAS'; end if;
                                        if sm.libellemdph = 22661 then mdphnew = 'ENVOYE_HANDISUP'; end if;
                                        if sm.libellemdph = 22662 then mdphnew = 'OUI_HANDISUP'; end if;
                                        if sm.libellemdph = 22663 then mdphnew = 'EN_COURS_DE_CONSTITUTION_HANDISUP'; end if;
                                    end loop;
                            update dossier set commentaire = s.commentaire_classification, mdph = mdphnew where id = new_id_dossier;
                            for sc in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from situation_libelle_classification') as sc1 (situation bigint,libelle_classification bigint) where situation = s.id
                                loop
                                    classificationnew = null;
                                    if sc.libelle_classification = 9     then classificationnew = 'CECITE'; end if;
                                    if sc.libelle_classification = 10    then classificationnew = 'SURDITE_SEVERE_ET_PROFONDE'; end if;
                                    if sc.libelle_classification = 11    then classificationnew = 'MOTEUR'; end if;
                                    if sc.libelle_classification = 12    then classificationnew = 'TROUBLES_INTELLECTUELS_ET_COGNITIFS'; end if;
                                    if sc.libelle_classification = 13    then classificationnew = 'TROUBLES_PSYCHIQUES'; end if;
                                    if sc.libelle_classification = 14    then classificationnew = 'TROUBLES_VISCERAUX'; end if;
                                    if sc.libelle_classification = 15    then classificationnew = 'DEFICIENCE_AUDTIVE_AUTRE'; end if;
                                    if sc.libelle_classification = 16    then classificationnew = 'DEFICIENCE_VISUELLE_AUTRE'; end if;
                                    if sc.libelle_classification = 226   then classificationnew = 'TROUBLE_DU_LANGAGE_ET_DE_LA_PAROLE'; end if;
                                    if sc.libelle_classification = 10636 then classificationnew = 'AUTRES_TROUBLES'; end if;
                                    if sc.libelle_classification = 14615 then classificationnew = 'AUTISME'; end if;
                                    if sc.libelle_classification = 70903 then classificationnew = 'REFUS'; end if;
                                    if sc.libelle_classification = 74570 then classificationnew = 'NON_COMMUNIQUE'; end if;
                                    if sc.libelle_classification = 1315  then classificationnew = 'TEMPORAIRE'; end if;
                                    if classificationnew is not null then
                                    insert into dossier_classifications (dossier_id, classifications)
                                        values (new_id_dossier, classificationnew);
                                    end if;
                                end loop;
                            end loop;
-- BOUCLE SUR LES CONTACTS
                        for c in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from contact') as c1(id                     bigint,
                                                                                                                                                           compte_rendu           text,
                                                                                                                                                           date_contact           timestamp,
                                                                                                                                                           fonction_interlocuteur varchar(255),
                                                                                                                                                           id_dossier             bigint,
                                                                                                                                                           interlocuteur          varchar(255),
                                                                                                                                                           version                integer) where id_dossier = d.id
                        loop
                            for ct in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from contact_type_contact') as ct1(contact bigint, type_contact bigint) where contact = c.id
                            loop
                                    rdv = null;
                                    if ct.type_contact = 1 then rdv = 'MAIL'; end if;
                                    if ct.type_contact = 2 then rdv = 'TEL'; end if;
                                    if ct.type_contact = 3 then rdv = 'COURRIER'; end if;
                                    if ct.type_contact = 5 then rdv = 'RENDEZ_VOUS'; end if;
                            end loop;
                            insert into entretien (id, compte_rendu, date, interlocuteur, type_contact, dossier_id) values
                                (nextval('hibernate_sequence'), c.compte_rendu, c.date_contact, c.interlocuteur, rdv, new_id_dossier);
                        end loop;
-- BOUCLE SUR LES AMENAGEMENTS
                        for am in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from amenagement') as am1(
                                                                                                                                                                 id                  bigint,
                                                                                                                                                                 accord              varchar(255),
                                                                                                                                                                 autres_amenagements text,
                                                                                                                                                                 autres_temp_majore  text,
                                                                                                                                                                 date_create         timestamp,
                                                                                                                                                                 date_debut          timestamp,
                                                                                                                                                                 date_fin            timestamp,
                                                                                                                                                                 date_update         timestamp,
                                                                                                                                                                 date_visa           timestamp,
                                                                                                                                                                 id_dossier          bigint,
                                                                                                                                                                 nom_medecin         varchar(255),
                                                                                                                                                                 statut              varchar(255),
                                                                                                                                                                 temp_majore         varchar(255),
                                                                                                                                                                 version             integer,
                                                                                                                                                                 nom_valideur        varchar(255),
                                                                                                                                                                 autres_type_epreuve text,
                                                                                                                                                                 amenagement_premier text,
                                                                                                                                                                 date_refus          timestamp,
                                                                                                                                                                 motif_refus         text,
                                                                                                                                                                 login_medecin       varchar(255),
                                                                                                                                                                 login_valideur      varchar(255),
                                                                                                                                                                 statut_mail         boolean,
                                                                                                                                                                 statut_mail_scol    boolean) where id_dossier = d.id
                        loop
                            status_amenagement = 'BROUILLON';
                            if am.statut = 'supprime' then status_amenagement = 'SUPPRIME'; end if;
                            if am.statut = 'refusAdministration' then status_amenagement = 'REFUSE_ADMINISTRATION'; end if;
                            if am.statut = 'visaAdministration' then status_amenagement = 'VISE_ADMINISTRATION'; end if;
                            if am.statut = 'valideMedecin' then status_amenagement = 'VALIDE_MEDECIN'; end if;
                            autorisation = 'NC';
                            if am.accord = 'oui' then autorisation = 'OUI'; end if;
                            if am.accord = 'non' then autorisation = 'NON'; end if;
                            amenagement_text = '';
                            for amt in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from amenagement_amenagements') as amt1(amenagement bigint, amenagements bigint) where amenagement = am.id
                                loop
                                    for l in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from libelle') as l1(dtype          varchar(31),
                                                                                                                                                                       id             bigint,
                                                                                                                                                                       actif          boolean,
                                                                                                                                                                       code_libelle   varchar(255),
                                                                                                                                                                       nom_libelle    varchar(255),
                                                                                                                                                                       rubrique       varchar(255),
                                                                                                                                                                       version        integer,
                                                                                                                                                                       a              varchar(255),
                                                                                                                                                                       b              varchar(255),
                                                                                                                                                                       c              varchar(255),
                                                                                                                                                                       so             varchar(255),
                                                                                                                                                                       parent_libelle bigint) where id = amt.amenagements
                                    loop
                                        amenagement_text := amenagement_text || l.nom_libelle || chr(10);
                                    end loop;
                                end loop;
                            type_amenagement = 'DATE';
                            if am.date_fin > '01-01-2024' then type_amenagement = 'CURSUS'; end if;
                            tempsmajorenew = upper(am.temp_majore);
                            if upper(am.temp_majore) = 'AUTRE' then tempsmajorenew = 'AUCUN'; end if;
                            dateenvoi = null;
                            if am.statut_mail = true then dateenvoi = now();
                            insert into amenagement (id, administration_date, amenagement_text, autorisation, autres_temps_majores,
                                                     autres_type_epreuve, create_date, delete_date, end_date, individu_send_date, mail_medecin,
                                                     mail_valideur, motif_refus, nom_medecin, nom_valideur, status_amenagement, temps_majore,
                                                     type_amenagement, valide_medecin_date, dossier_id)
                                values (nextval('hibernate_sequence'), am.date_visa, amenagement_text, autorisation, am.autres_temp_majore, am.autres_type_epreuve, am.date_create, am.date_refus, am.date_fin, dateenvoi, am.login_medecin, am.login_valideur, am.motif_refus, am.nom_medecin, am.nom_valideur, status_amenagement, tempsmajorenew, 'CURSUS', am.date_update, new_id_dossier);
                            if am.statut = 'valideMedecin' then
                                update dossier set status_dossier_amenagement = 'EN_ATTENTE' where id = new_id_dossier;
                            end if;
                            if am.statut = 'visaAdministration' then
                                update dossier set status_dossier_amenagement = 'VALIDE' where id = new_id_dossier;
                            end if;
                        end loop;
-- BOUCLE SUR LES AIDES MATERIELLES
                for aidesm in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from aide_materielle') as aidesm1(id               bigint,
                                                                                                                                                                     commentaires     varchar(255),
                                                                                                                                                                     couts            integer,
                                                                                                                                                                     date_debut       timestamp,
                                                                                                                                                                     date_fin         timestamp,
                                                                                                                                                                     id_dossier       bigint,
                                                                                                                                                                     type_aide        integer,
                                                                                                                                                                     version          integer,
                                                                                                                                                                     libelle_materiel bigint) where id_dossier = d.id
                    loop
                        type_aide_materiel = null;
                        for aidesml in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from aide_materielle_libelle_materiel') as aidesml1(aide_materielle bigint, libelle_materiel bigint)
                            loop
                                if aidesml.libelle_materiel = 28 then type_aide_materiel = 'RECHARGEMENT_LEOCARTE'; end if;
                                if aidesml.libelle_materiel = 30 then type_aide_materiel = 'LOGICIEL_ADAPTE'; end if;
                                if aidesml.libelle_materiel = 10357 then type_aide_materiel = 'TRANSCRIPTIONS_BRAILLE'; end if;
                            end loop;
                        insert into aide_materielle (id, comment, cost, end_date, start_date, type_aide_materielle, dossier_id)
                            values (nextval('hibernate_sequence'), aidesm.commentaires, aidesm.couts, aidesm.date_fin, aidesm.date_debut, type_aide_materiel, new_id_dossier);
                    end loop;
-- BOUCLE SUR LES AIDES HUMAINES
                for contrata in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from contrat') as contrata1( id             bigint,
                                                                                                                                                                date_contrat   timestamp,
                                                                                                                                                                id_dossier     bigint,
                                                                                                                                                                statut_dossier integer,
                                                                                                                                                                type_contrat   integer,
                                                                                                                                                                version        integer,
                                                                                                                                                                aidant         bigint) where id_dossier = d.id
                    loop

                        new_id_aide_humaine = nextval('hibernate_sequence');
                        for aidanta in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from aidant') as aidant1(id             bigint,
                                                                                                                                                                    date_naissance timestamp,
                                                                                                                                                                    email          varchar(255),
                                                                                                                                                                    nom            varchar(255),
                                                                                                                                                                    num_etudiant   varchar(255),
                                                                                                                                                                    prenom         varchar(255),
                                                                                                                                                                    tel            varchar(255),
                                                                                                                                                                    version        integer) where id = contrata.aidant
                            loop
                                status_dossier_aide_humaine = null;
                                if contrata.statut_dossier = 0 then status_dossier_aide_humaine = 'EN_COURS'; end if;
                                if contrata.statut_dossier = 1 then status_dossier_aide_humaine = 'COMPLET'; end if;
                                insert into aide_humaine (id, date_of_birth_aidant, email_aidant, first_name_aidant, name_aidant, num_etu_aidant, phone_aidant, start_date, status_aide_humaine, dossier_id)
                                values (new_id_aide_humaine, aidanta.date_naissance, aidanta.email, aidanta.prenom, aidanta.nom, aidanta.num_etudiant, aidanta.tel, contrata.date_contrat, status_dossier_aide_humaine, new_id_dossier);
                                for contratpj in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from contrat_pieces_jointes') as contratpj1(contrat bigint, pieces_jointes bigint) where contrat = contrata.id
                                    loop
                                        for pj in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from piece_jointe') as pj1(id                bigint,
                                                                                                                                                                                  content_type      varchar(255),
                                                                                                                                                                                  creer_par         varchar(255),
                                                                                                                                                                                  date_creation     timestamp,
                                                                                                                                                                                  date_modification timestamp,
                                                                                                                                                                                  description       varchar(500),
                                                                                                                                                                                  id_dossier        bigint,
                                                                                                                                                                                  modifier_par      varchar(255),
                                                                                                                                                                                  nom_fichier       varchar(255),
                                                                                                                                                                                  size              bigint,
                                                                                                                                                                                  titre             varchar(255),
                                                                                                                                                                                  version           integer,
                                                                                                                                                                                  fichier           bigint) where id = contratpj.pieces_jointes
                                            loop
                                                for bigfilea in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from big_file') as pj1(id          bigint,
                                                                                                                                                                                            binary_file oid, version integer) where id = pj.fichier
                                                    loop
                                                        new_id_big_file = nextval('hibernate_sequence');
                                                        insert into big_file (id, binary_file, version) VALUES (new_id_big_file, bigfilea.binary_file, bigfilea.version);
                                                        new_id_pj = nextval('hibernate_sequence');
                                                        typepjcontrat = 'FICHE';
                                                        if pj.titre = 'Annexe' then typepjcontrat = 'ANNEXE'; end if;
                                                        if pj.titre = 'Contrat' then typepjcontrat = 'CONTRAT'; end if;
                                                        insert into document (id, content_type, create_date, file_name, parent_id, parent_type, size, type_document, version, big_file_id, dossier_id)
                                                        VALUES (new_id_pj, pj.content_type, pj.date_creation, pj.nom_fichier, new_id_aide_humaine, 'AideHumaine', pj.size, typepjcontrat, pj.version, new_id_big_file, new_id_dossier);
                                                    end loop;
                                            end loop;
                                    end loop;
                                for contratf in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from contrat_fonctions') as contratf1(contrat bigint, fonctions bigint) where contrat = contrata.id
                                    loop
                                        if contratf.fonctions = 93 then insert into aide_humaine_fonction_aidants (aide_humaine_id, fonction_aidants) values (new_id_aide_humaine, 'PRENEUR_NOTES'); end if;
                                        if contratf.fonctions = 94 then insert into aide_humaine_fonction_aidants (aide_humaine_id, fonction_aidants) values (new_id_aide_humaine, 'TUTEUR_ACC'); end if;
                                        if contratf.fonctions = 95 then insert into aide_humaine_fonction_aidants (aide_humaine_id, fonction_aidants) values (new_id_aide_humaine, 'TUTEUR_PEDAGO'); end if;
                                    end loop;
                                for periodea in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from periode') as periode1(id            bigint,
                                                                                                                                                                               cout          integer,
                                                                                                                                                                               id_contrat    bigint,
                                                                                                                                                                               mois          integer,
                                                                                                                                                                               mois_paye     integer,
                                                                                                                                                                               nb_heures     double precision,
                                                                                                                                                                               rendu_le      timestamp,
                                                                                                                                                                               semestre      integer,
                                                                                                                                                                               version       integer,
                                                                                                                                                                               feuille_heure bigint,
                                                                                                                                                                               planning      bigint) where id_contrat = contrata.id
                                    loop
                                        if periodea.mois = 4 then mois = 'JANUARY'; end if;
                                        if periodea.mois = 5 then mois = 'FEBRUARY'; end if;
                                        if periodea.mois = 6 then mois = 'MARCH'; end if;
                                        if periodea.mois = 7 then mois = 'APRIL'; end if;
                                        if periodea.mois = 8 then mois = 'MAY'; end if;
                                        if periodea.mois = 9 then mois = 'JUNE'; end if;
                                        if periodea.mois = 10 then mois = 'JULY'; end if;
                                        if periodea.mois = 11 then mois = 'AUGUST'; end if;
                                        if periodea.mois = 0 then mois = 'SEPTEMBER'; end if;
                                        if periodea.mois = 1 then mois = 'OCTOBER'; end if;
                                        if periodea.mois = 2 then mois = 'NOVEMBER'; end if;
                                        if periodea.mois = 3 then mois = 'DECEMBER'; end if;
                                        if periodea.mois_paye = 0 then mois_paye = 'JANUARY'; end if;
                                        if periodea.mois_paye = 1 then mois_paye = 'FEBRUARY'; end if;
                                        if periodea.mois_paye = 2 then mois_paye = 'MARCH'; end if;
                                        if periodea.mois_paye = 3 then mois_paye = 'APRIL'; end if;
                                        if periodea.mois_paye = 4 then mois_paye = 'MAY'; end if;
                                        if periodea.mois_paye = 5 then mois_paye = 'JUNE'; end if;
                                        if periodea.mois_paye = 6 then mois_paye = 'JULY'; end if;
                                        if periodea.mois_paye = 7 then mois_paye = 'AUGUST'; end if;
                                        if periodea.mois_paye = 8 then mois_paye = 'SEPTEMBER'; end if;
                                        if periodea.mois_paye = 9 then mois_paye = 'OCTOBER'; end if;
                                        if periodea.mois_paye = 10 then mois_paye = 'NOVEMBER'; end if;
                                        if periodea.mois_paye = 11 then mois_paye = 'DECEMBER'; end if;

                                        new_id_periode = nextval('hibernate_sequence');

                                        new_id_feuille = null;
                                        if periodea.feuille_heure is not null then
                                            new_id_big_file = null;
                                            for feuille in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from piece_jointe') as feuille1(id                bigint,
                                                                                                                                                                                      content_type      varchar(255),
                                                                                                                                                                                      creer_par         varchar(255),
                                                                                                                                                                                      date_creation     timestamp,
                                                                                                                                                                                      date_modification timestamp,
                                                                                                                                                                                      description       varchar(500),
                                                                                                                                                                                      id_dossier        bigint,
                                                                                                                                                                                      modifier_par      varchar(255),
                                                                                                                                                                                      nom_fichier       varchar(255),
                                                                                                                                                                                      size              bigint,
                                                                                                                                                                                      titre             varchar(255),
                                                                                                                                                                                      version           integer,
                                                                                                                                                                                      fichier           bigint) where id = periodea.feuille_heure
                                                loop
                                                    for bigfilea in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from big_file') as bigfeuille1(id          bigint,
                                                                                                                                                                                                binary_file oid, version integer) where id = feuille.fichier
                                                        loop
                                                            new_id_feuille = nextval('hibernate_sequence');
                                                            new_id_big_file = nextval('hibernate_sequence');
                                                            insert into big_file (id, binary_file, version) VALUES (new_id_big_file, bigfilea.binary_file, bigfilea.version);
                                                            new_id_pj = nextval('hibernate_sequence');
                                                            insert into document (id, content_type, create_date, file_name, parent_id, parent_type, size, type_document, version, big_file_id, dossier_id)
                                                            VALUES (new_id_feuille, pj.content_type, pj.date_creation, pj.nom_fichier, new_id_periode, 'Periode', pj.size, typepjcontrat, pj.version, new_id_big_file, new_id_dossier);
                                                        end loop;
                                                end loop;
                                            end if;
                                        new_id_planing = null;
                                        if periodea.planning is not null then
                                            new_id_big_file = null;
                                            for planing in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from piece_jointe') as planing1(id                bigint,
                                                                                                                                                                                      content_type      varchar(255),
                                                                                                                                                                                      creer_par         varchar(255),
                                                                                                                                                                                      date_creation     timestamp,
                                                                                                                                                                                      date_modification timestamp,
                                                                                                                                                                                      description       varchar(500),
                                                                                                                                                                                      id_dossier        bigint,
                                                                                                                                                                                      modifier_par      varchar(255),
                                                                                                                                                                                      nom_fichier       varchar(255),
                                                                                                                                                                                      size              bigint,
                                                                                                                                                                                      titre             varchar(255),
                                                                                                                                                                                      version           integer,
                                                                                                                                                                                      fichier           bigint) where id = periodea.planning
                                                loop
                                                    for bigfileplanning in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from big_file') as bigplanning1(id          bigint,
                                                                                                                                                                                                binary_file oid, version integer) where id = planing.fichier
                                                        loop
                                                            new_id_planing = nextval('hibernate_sequence');
                                                            new_id_big_file = nextval('hibernate_sequence');
                                                            insert into big_file (id, binary_file, version) VALUES (new_id_big_file, bigfileplanning.binary_file, bigfileplanning.version);
                                                            new_id_pj = nextval('hibernate_sequence');
                                                            insert into document (id, content_type, create_date, file_name, parent_id, parent_type, size, type_document, version, big_file_id, dossier_id)
                                                            VALUES (new_id_planing, pj.content_type, pj.date_creation, pj.nom_fichier, new_id_periode, 'Periode', pj.size, typepjcontrat, pj.version, new_id_big_file, new_id_dossier);
                                                        end loop;
                                                end loop;
                                            end if;
                                        insert into periode_aide_humaine (id, cost, mois, mois_paye, nb_heures, registration_date, aide_humaine_id, feuille_heures_id, planning_id)
                                        values (new_id_periode, periodea.cout, mois, mois_paye, periodea.nb_heures, periodea.rendu_le, new_id_aide_humaine, new_id_feuille, new_id_planing);
                                    end loop;
                            end loop;
                    end loop;
-- BOUCLE SUR LES ENQUETES
                for enquetea in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from enquete') as enquetea1(id_        bigint,
                                                                                                                                                               aidhnat    text,
                                                                                                                                                               an         varchar(255),
                                                                                                                                                               autaa      text,
                                                                                                                                                               autae      text,
                                                                                                                                                               codaml     integer,
                                                                                                                                                               codeurh    integer,
                                                                                                                                                               codfil     integer,
                                                                                                                                                               codfmt     integer,
                                                                                                                                                               codhd      integer,
                                                                                                                                                               codmeaa    integer,
                                                                                                                                                               codmeae    integer,
                                                                                                                                                               codmeahf   integer,
                                                                                                                                                               codpfas    integer,
                                                                                                                                                               codpfpp    integer,
                                                                                                                                                               codsco     integer,
                                                                                                                                                               com        text,
                                                                                                                                                               dja_cop    varchar(255),
                                                                                                                                                               hdtmp      boolean,
                                                                                                                                                               id         varchar(255),
                                                                                                                                                               id_dossier bigint,
                                                                                                                                                               interph    integer,
                                                                                                                                                               modfrmn    integer,
                                                                                                                                                               newid      varchar(255),
                                                                                                                                                               newnum     varchar(255),
                                                                                                                                                               nfic       varchar(255),
                                                                                                                                                               numetu     varchar(255),
                                                                                                                                                               sexe       integer,
                                                                                                                                                               typefrmn   integer,
                                                                                                                                                               version    integer) where id_dossier = d.id
                    loop
                        codfilnew = null;
                        if enquetea.codfil = 1 then codfilnew = 'lsh'; end if;
                        if enquetea.codfil = 2 then codfilnew = 'dsg'; end if;
                        if enquetea.codfil = 3 then codfilnew = 'sci'; end if;
                        if enquetea.codfil = 4 then codfilnew = 'sta'; end if;
                        if enquetea.codfil = 5 then codfilnew = 'san'; end if;
                        if enquetea.codfil = 6 then codfilnew = 'par'; end if;
                        codfmtnew = null;
                        if enquetea.codfmt = 1 then
                            codfmtnew = 'CA';
                        elsif enquetea.codfmt = 2 then
                            codfmtnew = 'DaA';
                        elsif enquetea.codfmt = 3 then
                            codfmtnew = 'DaB';
                        elsif enquetea.codfmt = 4 then
                            codfmtnew = 'iut';
                        elsif enquetea.codfmt = 5 then
                            codfmtnew = 'L';
                        elsif enquetea.codfmt = 6 then
                            codfmtnew = 'Lp';
                        elsif enquetea.codfmt = 7 then
                            codfmtnew = 'M';
                        elsif enquetea.codfmt = 8 then
                            codfmtnew = 'Mf';
                        elsif enquetea.codfmt = 9 then
                            codfmtnew = 'iep';
                        elsif enquetea.codfmt = 10 then
                            codfmtnew = 'ing';
                        elsif enquetea.codfmt = 11 then
                            codfmtnew = 'D';
                        elsif enquetea.codfmt = 12 then
                            codfmtnew = 'HDR';
                        elsif enquetea.codfmt = 13 then
                            codfmtnew = 'DU';
                        elsif enquetea.codfmt = 14 then
                            codfmtnew = 'pa';
                        elsif enquetea.codfmt = 15 then
                            codfmtnew = 'med';
                        elsif enquetea.codfmt = 16 then
                            codfmtnew = 'odo';
                        elsif enquetea.codfmt = 17 then
                            codfmtnew = 'pha';
                        elsif enquetea.codfmt = 18 then
                            codfmtnew = 'mai';
                        elsif enquetea.codfmt = 19 then
                            codfmtnew = 'aup';
                        elsif enquetea.codfmt = 20 then
                            codfmtnew = 'inf';
                        elsif enquetea.codfmt = 21 then
                            codfmtnew = 'ane';
                        elsif enquetea.codfmt = 22 then
                            codfmtnew = 'ped';
                        elsif enquetea.codfmt = 23 then
                            codfmtnew = 'psm';
                        elsif enquetea.codfmt = 24 then
                            codfmtnew = 'orh';
                        elsif enquetea.codfmt = 25 then
                            codfmtnew = 'ort';
                        elsif enquetea.codfmt = 26 then
                            codfmtnew = 'bim';
                        elsif enquetea.codfmt = 27 then
                            codfmtnew = 'kin';
                        elsif enquetea.codfmt = 28 then
                            codfmtnew = 'mem';
                        elsif enquetea.codfmt = 29 then
                            codfmtnew = 'erg';
                        elsif enquetea.codfmt = 30 then
                            codfmtnew = 'afp';
                        elsif enquetea.codfmt = 31 then
                            codfmtnew = 'x';
                        end if;

                        codsconew = null;
                        if enquetea.codsco = 1 then codsconew = 'iu1'; end if;
                        if enquetea.codsco = 2 then codsconew = 'iu2'; end if;
                        if enquetea.codsco = 3 then codsconew = 'L1'; end if;
                        if enquetea.codsco = 4 then codsconew = 'L2'; end if;
                        if enquetea.codsco = 5 then codsconew = 'L3'; end if;
                        if enquetea.codsco = 6 then codsconew = 'M1'; end if;
                        if enquetea.codsco = 7 then codsconew = 'M2'; end if;
                        if enquetea.codsco = 8 then codsconew = 'ip1'; end if;
                        if enquetea.codsco = 9 then codsconew = 'ip2'; end if;
                        if enquetea.codsco = 10 then codsconew = 'ip3'; end if;
                        if enquetea.codsco = 11 then codsconew = 'ip4'; end if;
                        if enquetea.codsco = 12 then codsconew = 'ip5'; end if;
                        if enquetea.codsco = 13 then codsconew = 'ing1'; end if;
                        if enquetea.codsco = 14 then codsconew = 'ing2'; end if;
                        if enquetea.codsco = 15 then codsconew = 'ing3'; end if;
                        if enquetea.codsco = 16 then codsconew = 'ing4'; end if;
                        if enquetea.codsco = 17 then codsconew = 'ing5'; end if;
                        if enquetea.codsco = 18 then codsconew = 'ing6'; end if;
                        if enquetea.codsco = 19 then codsconew = 'san1a'; end if;
                        if enquetea.codsco = 20 then codsconew = 'san2a'; end if;
                        if enquetea.codsco = 21 then codsconew = 'san3a'; end if;
                        if enquetea.codsco = 22 then codsconew = 'san4a'; end if;
                        if enquetea.codsco = 23 then codsconew = 'san5a'; end if;
                        if enquetea.codsco = 24 then codsconew = 'san6a'; end if;
                        if enquetea.codsco = 25 then codsconew = 'cycx1'; end if;
                        if enquetea.codsco = 26 then codsconew = 'cycx2'; end if;
                        codhdnew = null;
                        if enquetea.codhd = 1 then codhdnew = 'COG'; end if;
                        if enquetea.codhd = 2 then codhdnew = 'TSA'; end if;
                        if enquetea.codhd = 3 then codhdnew = 'PSY'; end if;
                        if enquetea.codhd = 4 then codhdnew = 'LNG'; end if;
                        if enquetea.codhd = 5 then codhdnew = 'MOT'; end if;
                        if enquetea.codhd = 6 then codhdnew = 'VIS'; end if;
                        if enquetea.codhd = 7 then codhdnew = 'VIS0'; end if;
                        if enquetea.codhd = 8 then codhdnew = 'VUE'; end if;
                        if enquetea.codhd = 9 then codhdnew = 'VUA'; end if;
                        if enquetea.codhd = 10 then codhdnew = 'AUD'; end if;
                        if enquetea.codhd = 11 then codhdnew = 'AUA'; end if;
                        if enquetea.codhd = 12 then codhdnew = 'PTA'; end if;
                        if enquetea.codhd = 13 then codhdnew = 'AUT'; end if;
                        if enquetea.codhd = 14 then codhdnew = 'TND'; end if;
                        codpfasnew = null;
                        if enquetea.codpfas = 1 then codpfasnew = 'AS0'; end if;
                        if enquetea.codpfas = 2 then codpfasnew = 'AS1'; end if;
                        codpfppnew = null;
                        if enquetea.codpfpp = 1 then codpfppnew = 'PP0'; end if;
                        if enquetea.codpfpp = 2 then codpfppnew = 'MH1'; end if;
                        if enquetea.codpfpp = 3 then codpfppnew = 'MH0'; end if;
                        if enquetea.codpfpp = 4 then codpfppnew = 'PP2'; end if;
                        modfrmnew = null;
                        if enquetea.modfrmn = 1 then modfrmnew = 'P'; end if;
                        if enquetea.modfrmn = 2 then modfrmnew = 'D'; end if;
                        if enquetea.modfrmn = 3 then modfrmnew = 'H'; end if;
                        if enquetea.modfrmn = 4 then modfrmnew = 'A'; end if;
                        typfrmnnew = null;
                        if enquetea.typefrmn = 1 then typfrmnnew = 'I'; end if;
                        if enquetea.typefrmn = 2 then typfrmnnew = 'C'; end if;
                        new_id_enquete = nextval('hibernate_sequence');
                        insert into enquete (id, aidhnat, an, autaa, autae, cod_fil, cod_fmt, cod_hd, cod_pfas, cod_pfpp, cod_sco, codeurh, com, finished, hd_tmp, interph, mod_frmn, sexe, typ_frmn, dossier_id)
                            values (new_id_enquete, enquetea.aidhnat, enquetea.an, enquetea.autaa, enquetea.autae, codfilnew, codfmtnew, codhdnew, codpfasnew, codpfppnew, codsconew, enquetea.codeurh, enquetea.com, true, enquetea.hdtmp, enquetea.interph, modfrmnew, enquetea.sexe, typfrmnnew, new_id_dossier);
                        codmeaanew = null;
                        if enquetea.codmeaa = 1 then codmeaanew = 'AA1'; end if;
                        if enquetea.codmeaa = 2 then codmeaanew = 'AA2'; end if;
                        if enquetea.codmeaa = 3 then codmeaanew = 'AA3'; end if;
                        if enquetea.codmeaa = 4 then codmeaanew = 'AAO'; end if;
                        if codmeaanew is not null then
                            insert into enquete_cod_meaa (enquete_id, cod_meaa) values (new_id_enquete, codmeaanew);
                        end if;
                        for enquetecodamla in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from enquete_codaml') as enquetecodamla1(enquete bigint, codaml bigint) where enquete = enquetea.id_
                            loop
                                codamlnew = null;
                                if enquetecodamla.codaml = 0 then codamlnew = 'AM00'; end if;
                                if enquetecodamla.codaml = 1 then codamlnew = 'AM1'; end if;
                                if enquetecodamla.codaml = 2 then codamlnew = 'AM10'; end if;
                                if enquetecodamla.codaml = 3 then codamlnew = 'AM11'; end if;
                                if enquetecodamla.codaml = 4 then codamlnew = 'AM1X'; end if;
                                if enquetecodamla.codaml = 5 then codamlnew = 'AM2'; end if;
                                if enquetecodamla.codaml = 6 then codamlnew = 'AM20'; end if;
                                if enquetecodamla.codaml = 7 then codamlnew = 'AM21'; end if;
                                if enquetecodamla.codaml = 8 then codamlnew = 'AM2X'; end if;
                                if enquetecodamla.codaml = 9 then codamlnew = 'AM3'; end if;
                                if enquetecodamla.codaml = 10 then codamlnew = 'AM30'; end if;
                                if enquetecodamla.codaml = 11 then codamlnew = 'AM31'; end if;
                                if enquetecodamla.codaml = 12 then codamlnew = 'AM3X'; end if;
                                if enquetecodamla.codaml = 13 then codamlnew = 'AM4'; end if;
                                if enquetecodamla.codaml = 14 then codamlnew = 'AM40'; end if;
                                if enquetecodamla.codaml = 15 then codamlnew = 'AM41'; end if;
                                if enquetecodamla.codaml = 16 then codamlnew = 'AM4X'; end if;
                                if enquetecodamla.codaml = 17 then codamlnew = 'AM5'; end if;
                                if enquetecodamla.codaml = 18 then codamlnew = 'AM50'; end if;
                                if enquetecodamla.codaml = 19 then codamlnew = 'AM51'; end if;
                                if enquetecodamla.codaml = 20 then codamlnew = 'AM5X'; end if;
                                if enquetecodamla.codaml = 21 then codamlnew = 'AM6'; end if;
                                if enquetecodamla.codaml = 22 then codamlnew = 'AM60'; end if;
                                if enquetecodamla.codaml = 23 then codamlnew = 'AM61'; end if;
                                if enquetecodamla.codaml = 24 then codamlnew = 'AM6X'; end if;
                                if enquetecodamla.codaml = 25 then codamlnew = 'AM7'; end if;
                                if enquetecodamla.codaml = 26 then codamlnew = 'AM70'; end if;
                                if enquetecodamla.codaml = 27 then codamlnew = 'AM71'; end if;
                                if enquetecodamla.codaml = 28 then codamlnew = 'AM7X'; end if;
                                if enquetecodamla.codaml = 29 then codamlnew = 'AM8'; end if;
                                if enquetecodamla.codaml = 30 then codamlnew = 'AM80'; end if;
                                if enquetecodamla.codaml = 31 then codamlnew = 'AM81'; end if;
                                if enquetecodamla.codaml = 32 then codamlnew = 'AM8X'; end if;
                                if codamlnew is not null then
                                    insert into enquete_cod_aml (enquete_id, cod_aml) values (new_id_enquete, codamlnew);
                                end if;
                            end loop;
                        for enquetecodmeaea in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from enquete_codmeae') as enquetecodmeaea1(enquete bigint, codmeae bigint) where enquete = enquetea.id_
                            loop
                                codmeaenew = null;
                                if enquetecodmeaea.codmeae = 0 then
                                    codmeaenew = 'AE0';
                                elsif enquetecodmeaea.codmeae = 1 then
                                    codmeaenew = 'AE1';
                                elsif enquetecodmeaea.codmeae = 2 then
                                    codmeaenew = 'AE2';
                                elsif enquetecodmeaea.codmeae = 3 then
                                    codmeaenew = 'AE3';
                                elsif enquetecodmeaea.codmeae = 4 then
                                    codmeaenew = 'AE4';
                                elsif enquetecodmeaea.codmeae = 5 then
                                    codmeaenew = 'AE5';
                                elsif enquetecodmeaea.codmeae = 6 then
                                    codmeaenew = 'AE6';
                                elsif enquetecodmeaea.codmeae = 7 then
                                    codmeaenew = 'AE7';
                                elsif enquetecodmeaea.codmeae = 8 then
                                    codmeaenew = 'AE8';
                                elsif enquetecodmeaea.codmeae = 9 then
                                    codmeaenew = 'AEO';
                                end if;
                                if codmeaenew is not null then
                                    insert into enquete_cod_meae (enquete_id, cod_meae) values (new_id_enquete, codmeaenew);
                                end if;
                            end loop;
                        for enquetecodmeahfa in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from enquete_codmeahf') as enquetecodmeahfa1(enquete bigint, codmeahf bigint) where enquete = enquetea.id_
                            loop
                                codmeahfnew = null;
                                if enquetecodmeahfa.codmeahf = 0 then
                                    codmeahfnew = 'AHS0';
                                elsif enquetecodmeahfa.codmeahf = 1 then
                                    codmeahfnew = 'AHS1F';
                                elsif enquetecodmeahfa.codmeahf = 2 then
                                    codmeahfnew = 'AHS1E';
                                elsif enquetecodmeahfa.codmeahf = 3 then
                                    codmeahfnew = 'AHS1F';
                                elsif enquetecodmeahfa.codmeahf = 4 then
                                    codmeahfnew = 'AHS2F';
                                elsif enquetecodmeahfa.codmeahf = 5 then
                                    codmeahfnew = 'AHS2E';
                                elsif enquetecodmeahfa.codmeahf = 6 then
                                    codmeahfnew = 'AHS2F';
                                elsif enquetecodmeahfa.codmeahf = 7 then
                                    codmeahfnew = 'AHS3P';
                                elsif enquetecodmeahfa.codmeahf = 8 then
                                    codmeahfnew = 'AHS3O';
                                elsif enquetecodmeahfa.codmeahf = 9 then
                                    codmeahfnew = 'AHS3P';
                                elsif enquetecodmeahfa.codmeahf = 10 then
                                    codmeahfnew = 'AHS4';
                                elsif enquetecodmeahfa.codmeahf = 11 then
                                    codmeahfnew = 'AHS5O';
                                elsif enquetecodmeahfa.codmeahf = 12 then
                                    codmeahfnew = 'AHS5O';
                                elsif enquetecodmeahfa.codmeahf = 13 then
                                    codmeahfnew = 'AHS5Q';
                                elsif enquetecodmeahfa.codmeahf = 14 then
                                    codmeahfnew = 'AHS5H';
                                elsif enquetecodmeahfa.codmeahf = 15 then
                                    codmeahfnew = 'AHS5M';
                                elsif enquetecodmeahfa.codmeahf = 16 then
                                    codmeahfnew = 'AHS5S';
                                elsif enquetecodmeahfa.codmeahf = 17 then
                                    codmeahfnew = 'AHS5E';
                                end if;
                                if codmeahfnew is not null then
                                    insert into enquete_cod_meahf (enquete_id, cod_meahf) values (new_id_enquete, codmeahfnew);
                                end if;
                            end loop;
                        end loop;
                end loop;
        end loop;
end
$BODY$
    language plpgsql;
do $$ begin
    perform migrate();
end $$;