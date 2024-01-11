CREATE OR REPLACE FUNCTION refactodossieramenagementporte() returns void AS
$BODY$
DECLARE
    a amenagement%rowtype;
    d dossier%rowtype;
    new_id_da bigint;

BEGIN
    for a in select * from amenagement
        LOOP
            new_id_da = nextval('hibernate_sequence');
            for d in select * from dossier where id in (select ap.dossier_id from dossier_amenagements_portes as ap where ap.dossier_id = a.dossier_id)
                LOOP
                    insert into dossier_amenagement (id, last_year, mail_valideur_portabilite, nom_valideur_portabilite, status_dossier_amenagement, amenagement_id, dossier_id)
                    values (new_id_da, d.year, d.mail_valideur_portabilite, d.nom_valideur_portabilite, d.status_dossier_amenagement, a.id, d.id);
                END LOOP;
        END LOOP ;
END
$BODY$
    LANGUAGE plpgsql;

DO $$ BEGIN
    PERFORM refactodossieramenagementporte();
END $$;