create table type_aide_materielle
(
    id    bigint not null
        primary key,
    code  varchar(255),
    label varchar(255)
);

INSERT INTO public.type_aide_materielle (id, code, label) VALUES (nextval('hibernate_sequence'), 'LOGICIEL_ADAPTE', 'Logiciel adapté');
INSERT INTO public.type_aide_materielle (id, code, label) VALUES (nextval('hibernate_sequence'), 'RECHARGEMENT_LEOCARTE', 'Rechargement LéoCarte');
INSERT INTO public.type_aide_materielle (id, code, label) VALUES (nextval('hibernate_sequence'), 'TRANSCRIPTIONS_BRAILLE', 'Transcriptions braille');

alter table aide_materielle
    add type_aide_materielle_id bigint
        constraint fk97mdgcpgfrp3yjpqsbd0yasw7
            references type_aide_materielle;

UPDATE aide_materielle
SET type_aide_materielle_id = (
    SELECT t.id
    FROM type_aide_materielle t
    WHERE t.code = aide_materielle.type_aide_materielle
)
WHERE type_aide_materielle IS NOT NULL;

GRANT SELECT, INSERT, UPDATE, DELETE ON public.type_aide_materielle TO esupagape;

-- alter table public.aide_materielle drop column type_aide_materielle;

create table type_contact
(
    id    bigint not null
        primary key,
    code  varchar(255),
    label varchar(255)
);

INSERT INTO public.type_contact (id, code, label) VALUES (nextval('hibernate_sequence'), 'COURRIER', 'Courrier');
INSERT INTO public.type_contact (id, code, label) VALUES (nextval('hibernate_sequence'), 'MAIL', 'Email');
INSERT INTO public.type_contact (id, code, label) VALUES (nextval('hibernate_sequence'), 'RENDEZ_VOUS', 'Rendez-vous');
INSERT INTO public.type_contact (id, code, label) VALUES (nextval('hibernate_sequence'), 'TEL', 'Téléphonique');

alter table entretien
    add type_contact_id bigint
        constraint fk9y4leho8p94161i8f0o2fu8mv
            references type_contact;

UPDATE entretien
SET type_contact_id = (
    SELECT t.id
    FROM type_contact t
    WHERE t.code = entretien.type_contact
)
WHERE type_contact IS NOT NULL;

GRANT SELECT, INSERT, UPDATE, DELETE ON public.type_contact TO esupagape;

alter table public.entretien alter column type_contact drop not null;
-- alter table public.entretien drop column type_contact;
