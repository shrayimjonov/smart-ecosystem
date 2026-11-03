--for checking
select count(id) from invoice where number is not null;--18987
select count(id) from invoice where number is null;--2
select id from invoice where number is null; -- 4773,4879
select invoice_id from list_invoice where number is null;--2


-- For setting invoice number
update list_invoice set  number = (select i.number from invoice i where i.id = list_invoice.invoice_id)
where number is null;

-- For setting shipment id to list invoices
update list_invoice set  shipment_id = (select sh.id from shipment sh where sh.invoice_id = list_invoice.invoice_id)
where list_invoice.shipment_id is null;
