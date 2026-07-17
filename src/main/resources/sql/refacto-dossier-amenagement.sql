CREATE OR REPLACE FUNCTION refactodossieramenagement() returns void AS
$BODY$
DECLARE
    a amenagement%rowtype;
    d dossier%rowtype;
    new_id_da bigint;

BEGIN
    for a in select * from amenagement
        LOOP
            for d in select * from dossier where id = a.dossier_id or id in (select dap.dossier_id from dossier_amenagements_portes as dap where dap.amenagements_portes_id = a.id)
                LOOP
                    new_id_da = nextval('hibernate_sequence');
                    insert into dossier_amenagement (id, last_year, mail_valideur_portabilite, nom_valideur_portabilite, status_dossier_amenagement, amenagement_id, dossier_id)
                    values (new_id_da, d.year, d.mail_valideur_portabilite, d.nom_valideur_portabilite, d.status_dossier_amenagement, a.id, d.id);
                END LOOP;
        END LOOP ;
END
$BODY$
    LANGUAGE plpgsql;

DO $$ BEGIN
    PERFORM refactodossieramenagement();
END $$;