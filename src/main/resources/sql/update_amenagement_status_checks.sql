alter table public.amenagement
    drop constraint if exists amenagement_status_amenagement_check;

alter table public.amenagement
    add constraint amenagement_status_amenagement_check
        check ((status_amenagement)::text = any
               ((array ['BROUILLON'::character varying,
                        'ENVOYE'::character varying,
                        'VALIDE_MEDECIN'::character varying,
                        'VALIDE_REFERENT'::character varying,
                        'VISE_ADMINISTRATION'::character varying,
                        'REFUSE_ADMINISTRATION'::character varying,
                        'SUPPRIME'::character varying])::text[]));
