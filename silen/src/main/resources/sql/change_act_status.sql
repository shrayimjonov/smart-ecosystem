update acceptance_transfer_act set status='CANCEL' where status='CANCELED';
update acceptance_transfer_act set status='ACCEPT' where status='ACCEPTED';