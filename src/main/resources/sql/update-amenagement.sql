CREATE OR REPLACE FUNCTION updateamenagement() returns void AS
$BODY$
DECLARE
    d dossier%rowtype;
BEGIN
    for d in select * from dossier
        LOOP
            if d.amenagement_porte_id is not null then
                insert into dossier_amenagements_portes (dossier_id, amenagements_portes_id) values (d.id, d.amenagement_porte_id);
            end if;
        END LOOP ;
END
$BODY$
    LANGUAGE plpgsql;

DO $$ BEGIN
    PERFORM updateamenagement();
END $$;