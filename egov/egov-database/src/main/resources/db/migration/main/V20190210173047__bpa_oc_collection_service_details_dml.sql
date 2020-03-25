Insert into egcl_servicecategory (id,name,code,isactive,version,createdby,createddate,lastmodifiedby,lastmodifieddate) values (nextval('seq_egcl_servicecategory'),'Occupancy Certificate','OC',true,0,1,now(),1,now());

Insert into egcl_servicedetails (id,name,serviceurl,isenabled,callbackurl,servicetype,code,fund,fundsource,functionary,vouchercreation,scheme,subscheme,servicecategory,isvoucherapproved,vouchercutoffdate,created_by,created_date,modified_by,modified_date,ordernumber) values (nextval('seq_egcl_servicedetails'), 'Occupancy Certificate Charges', '/../bpa/collection/bill', true, '/receipts/receipt-create.action', 'B', 'OC', (select id from fund where code='01'), null, null, true, null, null, (select id from egcl_servicecategory where code='OC'), true, now(), 1, now(), 1, now(),null);