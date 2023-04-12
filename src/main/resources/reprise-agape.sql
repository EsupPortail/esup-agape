CREATE OR REPLACE FUNCTION migrate() returns void AS
$BODY$
declare
    e record;
    d record;
    ds record;
    c record;
    ct record;
    am record;
    amt record;
    aidesm record;
    aidesml record;
    l record;
    contrata record;
    aidant record;
    contratf record;

    new_id_user bigint;
    new_id_dossier bigint;
    new_id_aide_humaine bigint;
    type varchar(255);
    gender varchar(255);
    new_status_dossier varchar(255);
    rdv varchar(255);
    autorisation varchar(255);
    amenagement_text text;
    status_dossier_amenagement varchar(255);
    status_dossier_aide_humaine varchar(255);
    status_amenagement varchar(255);
    type_amenagement varchar(255);
    type_aide_materiel varchar(255);
begin
    for e in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from etudiant order by id desc limit 100') as e1(
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
            if e.num_etudiant not in (select num_etudiant from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select num_etudiant as test from etudiant group by num_etudiant having count(num_etudiant) > 1') as e1(num_etudiant varchar(255))) then
-- BOUCLE SUR LES ETUDIANTS
                gender = 'MASCULIN';
                if e.sexe = 1 then gender = 'FEMININ'; end if;
                insert into individu (id, contact_phone, date_of_birth, email_etu, eppn, first_name, fix_address, fixcp, fix_city, fix_country, fix_phone, gender, name, nationalite, num_etu, photo_id, sex) values
                    (new_id_user, e.tel_portable, e.date_naissance, e.email_etudiant, null, e.prenom, e.adresse_fixe, e.code_postal_fixe, e.nom_commune_fixe, null, e.tel_fixe, gender, e.nom, null, e.num_etudiant, null, e.sexe);

                for d in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from dossier') as d1(
                                                                                                                                                   id                       bigint,
                                                                                                                                                   annee                    varchar(255),
                                                                                                                                                   id_user                  bigint,
                                                                                                                                                    version                 integer,
                                                                                                                                                   premiere_inscription     boolean,
                                                                                                                                                   type_individu            integer
                    ) where id_user = e.id
                    loop
-- BOUCLE SUR LES DOSSIERS
                        type = 'INCONNU';
                        if d.type_individu = 0 then type = 'LYCEEN'; end if;
                        if d.type_individu = 1 then type = 'ETUDIANT'; end if;
                        if d.type_individu = 2 then type = 'HORS_UNIV'; end if;
                        for ds in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from dossier_statut_dossier') as ds1 (dossier bigint, statut_dossier bigint) where dossier = d.id
                        loop
                                if ds.statut_dossier = 7 or ds.statut_dossier = 1487 then new_status_dossier = 'ACCUEILLI'; end if;
                                if ds.statut_dossier = 8 or ds.statut_dossier = 131589 then new_status_dossier = 'SUIVI'; end if;
                                if ds.statut_dossier = 1488 then new_status_dossier = 'IMPOSSIBLE_A_CONTACTER'; end if;
                                if ds.statut_dossier = 82731 then new_status_dossier = 'IMPORTE'; end if;
                                if ds.statut_dossier = 104328 then new_status_dossier = 'RECU_PAR_LA_MEDECINE_PREVENTIVE'; end if;
                        end loop;
                        if (select count(*) from year where number = cast(d.annee as integer)) = 0 then insert into year (id, number) values (nextval('hibernate_sequence'), cast(d.annee as integer)); end if;
                        new_id_dossier = nextval('hibernate_sequence');
                        insert into dossier (id, year, individu_id, type, status_dossier, status_dossier_amenagement) values
                            (new_id_dossier, cast(d.annee as integer), new_id_user, type, new_status_dossier, 'NON');
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
                            if am.statut = 'suprime' then status_amenagement = 'SUPPRIME'; end if;
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
                            insert into amenagement (id, administration_date, amenagement_text, autorisation, autres_temps_majores,
                                                     autres_type_epreuve, create_date, delete_date, end_date, mail_individu, mail_medecin,
                                                     mail_valideur, motif_refus, nom_medecin, nom_valideur, status_amenagement, temps_majore,
                                                     type_amenagement, valide_medecin_date, dossier_id)
                            values (nextval('hibernate_sequence'), am.date_visa, amenagement_text, autorisation, am.autres_temp_majore, am.autres_type_epreuve, am.date_create, am.date_refus, am.date_fin, am.statut_mail, am.login_medecin, am.login_valideur, am.motif_refus, am.nom_medecin, am.nom_valideur, status_amenagement, upper(am.temp_majore), 'CURSUS', am.date_update, new_id_dossier);
                            if am.statut = 'valideMedecin' then
                                update dossier set status_dossier_amenagement = 'EN_ATTENTE' where id = new_id_dossier;
                            end if;
                            if am.statut = 'visaAdministration' then
                                update dossier set status_dossier_amenagement = 'VALIDE' where id = new_id_dossier;
                            end if;
                        end loop;
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
                        type_aide_materiel = '';
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
                        for aidant in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from aidant') as aidant1(id             bigint,
                                                                                                                                                                    date_naissance timestamp,
                                                                                                                                                                    email          varchar(255),
                                                                                                                                                                    nom            varchar(255),
                                                                                                                                                                    num_etudiant   varchar(255),
                                                                                                                                                                    prenom         varchar(255),
                                                                                                                                                                    tel            varchar(255),
                                                                                                                                                                    version        integer) where id = contrata.aidant
                            loop
                                if contrata.statut_dossier = 0 then status_dossier_aide_humaine = 'EN_COURS'; end if;
                                if contrata.statut_dossier = 1 then status_dossier_aide_humaine = 'COMPLET'; end if;
                                insert into aide_humaine (id, date_of_birth_aidant, email_aidant, first_name_aidant, name_aidant, num_etu_aidant, phone_aidant, start_date, status_aide_humaine, dossier_id)
                                values (new_id_aide_humaine, aidant.date_naissance, aidant.email, aidant.prenom, aidant.nom, aidant.num_etudiant, aidant.tel, contrata.date_contrat, status_dossier_aide_humaine, new_id_dossier);
                            end loop;
                        for contratf in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from contrat_fonctions') as contratf1(contrat bigint, fonctions bigint) where contrat = contrata.id
                            loop
                                if contratf.fonctions = 93 then insert into aide_humaine_fonction_aidants (aide_humaine_id, fonction_aidants) values (new_id_aide_humaine, 'PRENEUR_NOTES'); end if;
                                if contratf.fonctions = 94 then insert into aide_humaine_fonction_aidants (aide_humaine_id, fonction_aidants) values (new_id_aide_humaine, 'TUTEUR_ACC'); end if;
                                if contratf.fonctions = 95 then insert into aide_humaine_fonction_aidants (aide_humaine_id, fonction_aidants) values (new_id_aide_humaine, 'TUTEUR_PEDAGO'); end if;
                            end loop;
                    end loop;
            end if;
        end loop;
end
$BODY$
    language plpgsql;
do $$ begin
    perform migrate();
end $$;