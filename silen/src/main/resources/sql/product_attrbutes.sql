
/**
    Потребительская тара 8 => 'LiquorVodka', 'WaterAndSoftDrinks', 'Champagne', 'Vodka', 'Wine', 'Liquor', 'Tincture', 'WineMaterial', 'Beer', 'NaturalWine', 'Cognac'
    Градус 18 => 'LiquorVodka', 'Vodka', 'Wine', 'AlcoholTechnical', 'CognacAlcohol', 'Champagne', 'Liquor', 'Tincture', 'Beer', 'WineMaterial', 'Cognac', 'Alcohol', 'NaturalWine', 'Ethereum'
    Объем 13 => 'LiquorVodka', 'WaterAndSoftDrinks', 'Champagne', 'Vodka', 'Wine', 'Medicines', 'Liquor', 'Tincture', 'WineMaterial', 'Beer', 'NaturalWine', 'Cognac'
*/

-- select p.id as product_id, capacity as attribute_value, title_alcohol, r.name
-- from product p
--          left join reference r on p.package_type_id = r.id
-- where type in ('LiquorVodka', 'WaterAndSoftDrinks', 'Champagne', 'Vodka', 'Wine', 'Medicines', 'Liquor', 'Tincture',
--                'WineMaterial', 'Beer', 'NaturalWine', 'Cognac');


-- truncate table product_attributes;


-- Потребительская тара  attribute_id=1; attribute_value=product.package_type_id.name; product_id=product.id;
insert into product_attributes (id, created_by, created_date, last_modified_by, attribute_id,
                                attribute_value, product_id)
select uuid_in(md5(random()::text || random()::text)::cstring),
       p.created_by,
       p.created_date,
       p.last_modified_by,
       1      as attribute_id,
       r.name as attribute_value,
       p.id   as product_id
from product p
         left join reference r on p.package_type_id = r.id
where type in ('LiquorVodka', 'WaterAndSoftDrinks', 'Champagne', 'Vodka', 'Wine', 'Medicines', 'Liquor', 'Tincture',
               'WineMaterial', 'Beer', 'NaturalWine', 'Cognac')
;

-- delete from product_attributes where attribute_id=18;

-- Градус attribute_id=2; attribute_value=product.title_alcohol; product_id=product.id;
insert into product_attributes (id, created_by, created_date, last_modified_by, attribute_id,
                                attribute_value, product_id)
select uuid_in(md5(random()::text || random()::text)::cstring),
       p.created_by,
       p.created_date,
       p.last_modified_by,
       2      as attribute_id,
       p.title_alcohol as attribute_value,
       p.id   as product_id
from product p
where type in ('LiquorVodka', 'Vodka', 'Wine', 'AlcoholTechnical', 'CognacAlcohol', 'Champagne', 'Liquor', 'Tincture', 'Beer', 'WineMaterial', 'Cognac', 'Alcohol', 'NaturalWine', 'Ethereum')
;


-- Объем attribute_id=3; attribute_value=product.capacity; product_id=product.id;
insert into product_attributes (id, created_by, created_date, last_modified_by, attribute_id,
                                attribute_value, product_id)
select uuid_in(md5(random()::text || random()::text)::cstring),
       p.created_by,
       p.created_date,
       p.last_modified_by,
       3      as attribute_id,
       p.capacity as attribute_value,
       p.id   as product_id
from product p
where type in ('LiquorVodka', 'WaterAndSoftDrinks', 'Champagne', 'Vodka', 'Wine', 'Medicines', 'Liquor', 'Tincture', 'WineMaterial', 'Beer', 'NaturalWine', 'Cognac')
;

