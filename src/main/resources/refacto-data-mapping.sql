update enquete set cod_sco = '_1' where cod_sco = '1';
update enquete set cod_sco = '_2' where cod_sco = '2';
update enquete set cod_sco = '_3' where cod_sco = '3';
update enquete set cod_sco = '_4' where cod_sco = '4';
update enquete set cod_sco = '_5' where cod_sco = '5';
update enquete set cod_sco = '_6' where cod_sco = '6+';

update enquete set cod_sco = '_1' where lower(cod_sco) = lower('1a');
update enquete set cod_sco = '_2' where lower(cod_sco) = lower('2a');
update enquete set cod_sco = '_3' where lower(cod_sco) = lower('3a');
update enquete set cod_sco = '_4' where lower(cod_sco) = lower('4a');
update enquete set cod_sco = '_5' where lower(cod_sco) = lower('5a');
update enquete set cod_sco = '_6' where lower(cod_sco) = lower('6a+');

update enquete set cod_sco = '_1' where lower(cod_sco) = lower('iu1');
update enquete set cod_sco = '_2' where lower(cod_sco) = lower('iu2');
update enquete set cod_sco = '_3' where lower(cod_sco) = lower('iu3');

update enquete set cod_sco = '_1' where lower(cod_sco) = lower('L1');
update enquete set cod_sco = '_2' where lower(cod_sco) = lower('L2');
update enquete set cod_sco = '_3' where lower(cod_sco) = lower('L3');

update enquete set cod_sco = '_4' where lower(cod_sco) = lower('M1');
update enquete set cod_sco = '_5' where lower(cod_sco) = lower('M2');

update enquete set cod_sco = '_6' where lower(cod_sco) = lower('D');

update enquete set cod_fmt = 'BUT' where lower(cod_fmt) = lower('iut');

update enquete set cod_fmt = 'MEDI' where lower(cod_fmt) = lower('med');

update enquete set cod_fil = 'MEDI' where lower(cod_fil) = lower('san');

update enquete set cod_fil = upper(cod_fil);

update enquete set cod_fmt = upper(cod_fmt);