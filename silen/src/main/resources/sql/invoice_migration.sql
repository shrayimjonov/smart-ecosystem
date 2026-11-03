insert into organization (id, tin, name, account, bank_id, address, mobile, work_phone, oked, district_id,
                          director, accountant, vat_reg_code, vat_reg_status, tax_gap, created_by, created_date,
                          last_modified_by, last_modified_date)
select distinct on (seller_tin) uuid_generate_v4(), seller_tin, seller_name, seller_account,
                                case when btrim(seller_bank_id) = '' then null else seller_bank_id end,
                                seller_address, seller_mobile, seller_work_phone, seller_oked,
                                case when btrim(seller_district_id) = '' then null else seller_district_id end,
                                seller_director, seller_accountant, seller_vat_reg_code, seller_vat_reg_status,
                                seller_tax_gap, created_by, created_date, last_modified_by, last_modified_date
from invoice where seller_tin not in (select tin from organization where deleted is not true);

insert into organization (id, tin, name, account, bank_id, address, mobile, work_phone, oked, district_id,
                          director, accountant, vat_reg_code, vat_reg_status, tax_gap, created_by, created_date,
                          last_modified_by, last_modified_date)
select distinct on (buyer_tin) uuid_generate_v4(), buyer_tin, buyer_name, buyer_account,
                               case when btrim(buyer_bank_id) = '' then null else buyer_bank_id end,
                               buyer_address, buyer_mobile, buyer_work_phone, buyer_oked,
                               case when btrim(buyer_district_id) = '' then null else buyer_district_id end,
                               buyer_director, buyer_accountant, buyer_vat_reg_code, buyer_vat_reg_status,
                               buyer_tax_gap, created_by, created_date, last_modified_by, last_modified_date
from invoice where buyer_tin not in (select tin from organization where deleted is not true);

insert into list_invoice (created_by, created_date, last_modified_by, last_modified_date,
                          approved_by, approved_date, approved_number, buyer_id, error_message,
                          factura_id, invoice_id, notes, seller_id, send_by, send_date, send_number,
                          shipment_id, signature_content, status, total)
select created_by, created_date, last_modified_by, last_modified_date, approved_by, approved_date, approved_number,
       (select id from organization where tin = buyer_tin and deleted is not true), error_message, factura_id, id, notes,
       (select id from organization where tin = seller_tin and deleted is not true), send_by, send_date, send_number, shipment_id,
       signature_content, status, total
from invoice;

alter table invoice drop column approved_by;
alter table invoice drop column approved_date;
alter table invoice drop column approved_number;
alter table invoice drop column error_message;
alter table invoice drop column factura_id;
alter table invoice drop column notes;
alter table invoice drop column send_by;
alter table invoice drop column send_date;
alter table invoice drop column send_number;
alter table invoice drop column shipment_id;
alter table invoice drop column signature_content;
alter table invoice drop column status;
alter table invoice drop column total;
alter table invoice drop column seller_tin;
alter table invoice drop column seller_name;
alter table invoice drop column seller_account;
alter table invoice drop column seller_bank_id;
alter table invoice drop column seller_address;
alter table invoice drop column seller_mobile;
alter table invoice drop column seller_work_phone;
alter table invoice drop column seller_oked;
alter table invoice drop column seller_district_id;
alter table invoice drop column seller_director;
alter table invoice drop column seller_accountant;
alter table invoice drop column seller_vat_reg_code;
alter table invoice drop column seller_vat_reg_status;
alter table invoice drop column seller_tax_gap;
alter table invoice drop column buyer_tin;
alter table invoice drop column buyer_name;
alter table invoice drop column buyer_account;
alter table invoice drop column buyer_bank_id;
alter table invoice drop column buyer_address;
alter table invoice drop column buyer_mobile;
alter table invoice drop column buyer_work_phone;
alter table invoice drop column buyer_oked;
alter table invoice drop column buyer_district_id;
alter table invoice drop column buyer_director;
alter table invoice drop column buyer_accountant;
alter table invoice drop column buyer_vat_reg_code;
alter table invoice drop column buyer_vat_reg_status;
alter table invoice drop column buyer_tax_gap;


