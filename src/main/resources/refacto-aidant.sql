CREATE OR REPLACE FUNCTION refactoaidant() returns void AS
$BODY$
DECLARE
    ah aide_humaine%rowtype;
    new_id_aidant bigint;
    uuid text;
BEGIN
    for ah in select * from aide_humaine
        LOOP
            new_id_aidant = nextval('hibernate_sequence');
            uuid = (SELECT uuid_in(overlay(overlay(md5(random()::text || ':' || random()::text) placing '4' from 13) placing to_hex(floor(random()*(11-8+1) + 8)::int)::text from 17)::cstring));
            while (select count(*) from aidant where uuid = num_etu_aidant) >= 1 loop
                    uuid = (SELECT uuid_in(overlay(overlay(md5(random()::text || ':' || random()::text) placing '4' from 13) placing to_hex(floor(random()*(11-8+1) + 8)::int)::text from 17)::cstring));
                end loop;
            insert into aidant (id, date_of_birth_aidant, email_aidant, first_name_aidant, name_aidant, num_etu_aidant, phone_aidant)
            values (new_id_aidant, ah.date_of_birth_aidant, ah.email_aidant, ah.first_name_aidant, ah.name_aidant, uuid, ah.phone_aidant);
            update aide_humaine set aidant_id = new_id_aidant where id = ah.id;
        end loop;
END
$BODY$
    LANGUAGE plpgsql;
DO $$ BEGIN
    PERFORM refactoaidant();
END $$;

alter table public.aide_humaine
    drop column email_aidant;

alter table public.aide_humaine
    drop column first_name_aidant;

alter table public.aide_humaine
    drop column name_aidant;

alter table public.aide_humaine
    drop column num_etu_aidant;

alter table public.aide_humaine
    drop column phone_aidant;
