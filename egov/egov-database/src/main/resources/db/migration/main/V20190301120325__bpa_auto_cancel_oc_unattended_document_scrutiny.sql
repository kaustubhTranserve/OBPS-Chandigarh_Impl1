INSERT INTO eg_appconfig ( ID, KEY_NAME, DESCRIPTION, VERSION, MODULE ) VALUES (nextval('SEQ_EG_APPCONFIG'), 'AUTO_CANCEL_UNATTENDED_DOCUMENT_SCRUTINY_OC', 'Is auto cancel unattended documnet scrutiny oc',0, (select id from eg_module where name='BPA')); 

INSERT INTO eg_appconfig_values ( ID, KEY_ID, EFFECTIVE_FROM, VALUE, VERSION ) VALUES (nextval('SEQ_EG_APPCONFIG_VALUES'), (SELECT id FROM EG_APPCONFIG WHERE KEY_NAME='AUTO_CANCEL_UNATTENDED_DOCUMENT_SCRUTINY_OC' and module= (select id from eg_module where name='BPA')), current_date,'YES',0);