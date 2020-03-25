----------------reject occupancy certificate applications predefined reasons------------------------------

insert into egbpa_mstr_permit_conditions (id, code, description, conditiontype, ordernumber, version, createdBy, createdDate, lastmodifiedby, lastmodifieddate) values ((nextval('seq_egbpa_mstr_permit_conditions')), 'OCRR01', 'The constructed building is in contradiction to any of the provisions of any of the law or order, rule, declaration or bye laws applicable.', 'OCRejection', 1, 0, 1, now(), 1, now());

insert into egbpa_mstr_permit_conditions (id, code, description, conditiontype, ordernumber, version, createdBy, createdDate, lastmodifiedby, lastmodifieddate) values ((nextval('seq_egbpa_mstr_permit_conditions')), 'OCRR02', 'The application for occupancy certificate does not contain the particulars or is not prepared in the manner required by these rules or bye law made under the acts concerned.', 'OCRejection', 2, 0, 1, now(), 1, now());

insert into egbpa_mstr_permit_conditions (id, code, description, conditiontype, ordernumber, version, createdBy, createdDate, lastmodifiedby, lastmodifieddate) values ((nextval('seq_egbpa_mstr_permit_conditions')), 'OCRR03', 'Any of the documents submitted do not conforms to the qualification requirements of the Architect, Engineer, Town Planner or supervisor or the owner/ applicant as required by the rule or byelaws concerned.', 'OCRejection', 3, 0, 1, now(), 1, now());

insert into egbpa_mstr_permit_conditions (id, code, description, conditiontype, ordernumber, version, createdBy, createdDate, lastmodifiedby, lastmodifieddate) values ((nextval('seq_egbpa_mstr_permit_conditions')), 'OCRR04', 'Any information or document or certificate as required by the permit approval system/ rule/ byelaws related has not been submitted properly or incorporated in an incorrect manner.', 'OCRejection', 4, 0, 1, now(), 1, now());

insert into egbpa_mstr_permit_conditions (id, code, description, conditiontype, ordernumber, version, createdBy, createdDate, lastmodifiedby, lastmodifieddate) values ((nextval('seq_egbpa_mstr_permit_conditions')), 'OCRR05', 'The owner of the land has not laid down and made street or streets or road or roads giving access to the site or sites connecting with an existing public or private streets while utilising , selling or leasing out or otherwise disposing of the land or any portion of the land or any portion or portions of the same site for construction of building.
#The constructed building has made an encroachment upon a land belonging to the Government and / or the Corporation and/ or properties belonging to others than the applicant .', 'OCRejection', 5, 0, 1, now(), 1, now());