ALTER TABLE public.dossier
    DROP CONSTRAINT dossier_status_dossier_check;

ALTER TABLE public.amenagement_type_epreuves
    DROP CONSTRAINT IF EXISTS amenagement_type_epreuves_type_epreuves_check;

alter table public.amenagement
    drop constraint if exists amenagement_status_amenagement_check;