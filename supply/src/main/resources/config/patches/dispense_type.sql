alter table product add column if not exists dispense_type integer;
alter table factura_product add column if not exists dispense_type integer;

update product set dispense_type='WITH_RECEIPT' where company_id in(SELECT company_id
                                                                    FROM company_settings
                                                                    WHERE values->>'PRODUCT_GROUP' = 'Pharma'
    AND (values->>'ENABLE_DISABLE')::boolean = true);

update factura_product fp set dispense_type=1 where fp.id in(
    select fp.id
    from factura_product fp
             inner join factura_detail fd on fd.id = fp.factura_detail_id
             inner join factura f on f.id = fd.factura_id
             inner join product p on p.id = fp.product_id
    where f.status='DRAFT' and f.deleted=false and p.dispense_type='WITH_RECEIPT');