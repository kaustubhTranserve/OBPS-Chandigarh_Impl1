INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, VERSION, MODULE ) VALUES (nextval('SEQ_EG_APPCONFIG'), 'RECENT_DCRRULE_AMENDMENTDATE', 'Recent DCR rule amendment date',0, (select id from eg_module where name='BPA')); 

INSERT INTO eg_appconfig_values ( ID, KEY_ID, EFFECTIVE_FROM, VALUE, VERSION ) VALUES (nextval('SEQ_EG_APPCONFIG_VALUES'), (SELECT id FROM EG_APPCONFIG WHERE KEY_NAME='RECENT_DCRRULE_AMENDMENTDATE' and module= (select id from eg_module where name='BPA')), current_date, '01/01/2019',0);