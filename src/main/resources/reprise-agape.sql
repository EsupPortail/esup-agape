CREATE OR REPLACE FUNCTION migrate() returns void AS
$BODY$
DECLARE
    e record;
BEGIN
    for e IN select * from dblink('dbname=agape user=esupagape password=esup', 'select * from etudiant') as e1(
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
        LOOP
            IF e.num_etudiant not in (select num_etudiant from dblink('dbname=agape user=esupagape password=esup', 'select num_etudiant as test from etudiant group by num_etudiant having count(num_etudiant) > 1') as e1(num_etudiant varchar(255))) THEN
                insert into individu (id, contact_phone, current_address, currentcp, current_city, date_of_birth, email_etu, email_perso, eppn, first_name, fix_address, fixcp, fix_city, fix_country, fix_phone, gender, name, nationalite, num_etu, photo_id, sex) VALUES
                    (nextval('hibernate_sequence'), e.tel_portable, e.adresse_annuelle, e.code_postal_annuel, e.nom_commune_annuelle, e.date_naissance, e.email_etudiant, e.email_perso, null, e.prenom, e.adresse_fixe, e.code_postal_fixe, e.nom_commune_fixe, null, e.tel_fixe, e.sexe, e.nom, null, e.num_etudiant, null, e.sexe);
            END IF;
        END LOOP;
END;
$BODY$
    LANGUAGE plpgsql;

DO $$ BEGIN
    PERFORM migrate();
END $$;