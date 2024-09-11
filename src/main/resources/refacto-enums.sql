INSERT INTO public.type_aide_materielle (id, code, label) VALUES (nextval('hibernate_sequence'), 'LOGICIEL_ADAPTE', 'Logiciel adapté');
INSERT INTO public.type_aide_materielle (id, code, label) VALUES (nextval('hibernate_sequence'), 'RECHARGEMENT_LEOCARTE', 'Rechargement LéoCarte');
INSERT INTO public.type_aide_materielle (id, code, label) VALUES (nextval('hibernate_sequence'), 'TRANSCRIPTIONS_BRAILLE', 'Transcriptions braille');

UPDATE aide_materielle
SET type_aide_materielle_id = (
    SELECT t.id
    FROM type_aide_materielle t
    WHERE t.code = aide_materielle.type_aide_materielle
)
WHERE type_aide_materielle IS NOT NULL;

INSERT INTO public.type_contact (id, code, label) VALUES (nextval('hibernate_sequence'), 'COURRIER', 'Courrier');
INSERT INTO public.type_contact (id, code, label) VALUES (nextval('hibernate_sequence'), 'MAIL', 'Email');
INSERT INTO public.type_contact (id, code, label) VALUES (nextval('hibernate_sequence'), 'RENDEZ_VOUS', 'Rendez-vous');
INSERT INTO public.type_contact (id, code, label) VALUES (nextval('hibernate_sequence'), 'TEL', 'Téléphonique');

UPDATE entretien
SET type_contact_id = (
    SELECT t.id
    FROM type_contact t
    WHERE t.code = entretien.type_contact
)
WHERE type_contact IS NOT NULL;

alter table public.entretien alter column type_contact drop not null;

