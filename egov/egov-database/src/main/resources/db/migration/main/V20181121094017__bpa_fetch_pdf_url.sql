Insert into eg_roleaction values((select id from eg_role where name='BPA Approver'),(select id from eg_action where name='Get edcr converted pdfs'));

Insert into eg_roleaction values((select id from eg_role where name='Bpa Administrator'),(select id from eg_action where name='Get edcr converted pdfs'));