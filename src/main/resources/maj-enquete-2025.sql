delete from enquete_cod_aml where cod_aml like 'AM0%';
update enquete_cod_aml set cod_aml = 'AM1' where cod_aml like 'AM1%';
update enquete_cod_aml set cod_aml = 'AM2' where cod_aml like 'AM2%';
update enquete_cod_aml set cod_aml = 'AM3' where cod_aml like 'AM3%';
update enquete_cod_aml set cod_aml = 'AM4' where cod_aml like 'AM4%';
update enquete_cod_aml set cod_aml = 'AM5' where cod_aml like 'AM5%';
update enquete_cod_aml set cod_aml = 'AM6' where cod_aml like 'AM6%';
update enquete_cod_aml set cod_aml = 'AM7' where cod_aml like 'AM7%';
update enquete_cod_aml set cod_aml = 'AM8' where cod_aml like 'AM8%';

delete from enquete_cod_meae where cod_meae = 'AE0';

delete from enquete_cod_meahf where cod_meahf = 'AHS0';
update enquete_cod_meahf set cod_meahf = 'AHS1' where cod_meahf like 'AHS1%';
update enquete_cod_meahf set cod_meahf = 'AHS2' where cod_meahf like 'AHS2%';

update  enquete set cod_pfpp = null where cod_pfpp = 'MH0';

delete from enquete_cod_pfas where cod_pfas = 'AS0' or cod_pfas = 'AS1';