update eg_action set enabled=true WHERE NAME='RemittanceVoucherReport';
INSERT INTO EG_ROLEACTION (ROLEID,ACTIONID) VALUES ((SELECT ID FROM eg_role WHERE name='Coll_View Access'),(SELECT ID FROM EG_ACTION WHERE NAME='ReceiptNumberSearchAjax'));
INSERT INTO EG_ROLEACTION (ROLEID,ACTIONID) VALUES ((SELECT ID FROM eg_role WHERE name='Coll_View Access'),(SELECT ID FROM EG_ACTION WHERE NAME='Voucher View'));