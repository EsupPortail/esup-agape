ALTER TABLE public.dossier
    DROP CONSTRAINT dossier_status_dossier_check;

ALTER TABLE public.dossier
    ADD CONSTRAINT dossier_status_dossier_check
        CHECK (
            status_dossier::text = ANY (
                ARRAY[
                    'IMPORTE',
                    'AJOUT_MANUEL',
                    'RECU_PAR_LA_MEDECINE_PREVENTIVE',
                    'RECONDUIT',
                    'NON_RECONDUIT',
                    'CONTACTE',
                    'ACCUEILLI',
                    'SUIVI',
                    'IMPOSSIBLE_A_CONTACTER',
                    'ANONYMOUS'
                    ]::text[]
                )
            );