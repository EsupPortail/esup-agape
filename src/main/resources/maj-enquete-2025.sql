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

update enquete set cod_sco = '1' where cod_sco = 'L1';
update enquete set cod_sco = '2' where cod_sco = 'L2';
update enquete set cod_sco = '3' where cod_sco = 'L3';

update enquete set cod_sco = '1' where cod_sco = 'iu1';
update enquete set cod_sco = '2' where cod_sco = 'iu2';
update enquete set cod_sco = '3' where cod_sco = 'iu3';

update enquete set cod_sco = '1' where cod_sco = '1a';
update enquete set cod_sco = '2' where cod_sco = '2a';
update enquete set cod_sco = '3' where cod_sco = '3a';
update enquete set cod_sco = '4' where cod_sco = '4a';
update enquete set cod_sco = '6' where cod_sco = '5a';
update enquete set cod_sco = '6+' where cod_sco = '6a+';

update enquete set cod_sco = '4' where cod_sco = 'M1';
update enquete set cod_sco = '5' where cod_sco = 'M2';

