CREATE OR REPLACE FUNCTION migrate() returns void AS
$BODY$
declare
    e record;
    d record;
    ds record;
    c record;
    new_id_user bigint;
    new_id_dossier bigint;
    type varchar(255);
    gender varchar(255);
    new_status_dossier varchar(255);
    rdv varchar(255);
begin
    for e in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from etudiant order by id desc limit 1000') as e1(
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
                    )
                    loop
                        if d.id_user = e.id then
-- BOUCLE SUR LES DOSSIER
                            type = 'INCONNU';
                            if d.type_individu = 0 then type = 'LYCEEN'; end if;
                            if d.type_individu = 1 then type = 'ETUDIANT'; end if;
                            if d.type_individu = 2 then type = 'HORS_UNIV'; end if;
                            for ds in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from dossier_statut_dossier') as ds1 (dossier bigint, statut_dossier bigint)
                            loop
                                if ds.dossier = d.id then
                                    if ds.statut_dossier = 7 or ds.statut_dossier = 1487 then new_status_dossier = 'ACCUEILLI'; end if;
                                    if ds.statut_dossier = 8 or ds.statut_dossier = 131589 then new_status_dossier = 'SUIVI'; end if;
                                    if ds.statut_dossier = 1488 then new_status_dossier = 'IMPOSSIBLE_A_CONTACTER'; end if;
                                    if ds.statut_dossier = 82731 then new_status_dossier = 'IMPORTE'; end if;
                                    if ds.statut_dossier = 104328 then new_status_dossier = 'RECU_PAR_LA_MEDECINE_PREVENTIVE'; end if;
                                end if;
                            end loop;
                            if (select count(*) from year where number = cast(d.annee as integer)) = 0 then insert into year (id, number) values (nextval('hibernate_sequence'), cast(d.annee as integer)); end if;
                            new_id_dossier = nextval('hibernate_sequence');
                            insert into dossier (id, year, individu_id, type, status_dossier) values
                                (new_id_dossier, cast(d.annee as integer), new_id_user, type, new_status_dossier);
                            for c in select * from dblink('dbname=agape port=5432 host=127.0.0.1 user=mh password=mh2015Agape', 'select * from contact') as c1(id                     bigint,
                                                                                                                                                               compte_rendu           text,
                                                                                                                                                               date_contact           timestamp,
                                                                                                                                                               fonction_interlocuteur varchar(255),
                                                                                                                                                               id_dossier             bigint,
                                                                                                                                                               interlocuteur          varchar(255),
                                                                                                                                                               version                integer)
                            loop
                                if c.id_dossier = d.id then
                                    insert into entretien (id, compte_rendu, date, interlocuteur, type_contact, dossier_id) values
                                        (nextval('hibernate_sequence'), c.compte_rendu, c.date_contact, c.interlocuteur, 'RENDEZ_VOUS', new_id_dossier);
                                end if;
                            end loop;
                        end if;
                    end loop;
            end if;
        end loop;
end;
$BODY$
    language plpgsql;
do $$ begin
    perform migrate();
end $$;