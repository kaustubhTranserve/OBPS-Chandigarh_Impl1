insert into eg_role (id,name,description,internal,createddate,createdby,lastmodifieddate,lastmodifiedby) 
values (nextval('seq_eg_role'), 'EMP_PORTAL_ACCESS', 'User who can access employee portal other than employee', true, now(), 1, now(), 1);