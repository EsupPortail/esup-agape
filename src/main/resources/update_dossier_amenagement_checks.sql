alter table public.dossier
    drop constraint dossier_status_dossier_amenagement_check;

alter table public.dossier
    add constraint dossier_status_dossier_amenagement_check
        check ((status_dossier_amenagement)::text = ANY
               ((ARRAY ['NON'::character varying, 'EN_ATTENTE'::character varying, 'VALIDE'::character varying, 'PORTE'::character varying, 'EXPIRE'::character varying, 'REFUSE'::character varying])::text[]));

