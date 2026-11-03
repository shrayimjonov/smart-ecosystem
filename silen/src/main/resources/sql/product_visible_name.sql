
update product p
set visible_name=''
where deleted is false;--2,734

update product p
set visible_name= concat(p.visible_name, rtrim(p.name))
where deleted is false;
--2,734
--       p.id = 'ad263a70-6eb3-4c15-b417-dbf78bf6f326';


update product p
set visible_name=(select concat(trim(p.visible_name), ' ', trim(pa.attribute_value), trim(attr.postfix), ',')
                  from product_attributes pa
                           left join attributes attr on pa.attribute_id = attr.id
                  where pa.product_id = p.id
                    and attr.sorter = 1
                    and attr.id = 3)
where deleted is false
  and type in ('LiquorVodka',
               'WaterAndSoftDrinks',
               'Champagne',
               'Vodka',
               'Wine',
               'Liquor',
               'Tincture',
               'WineMaterial',
               'Beer',
               'NaturalWine',
               'Cognac');
--2,552
-- p.id = 'ad263a70-6eb3-4c15-b417-dbf78bf6f326';


update product p
set visible_name=(select concat(trim(p.visible_name), ' ', trim(pa.attribute_value), trim(attr.postfix), ',')
                  from product_attributes pa
                           left join attributes attr on pa.attribute_id = attr.id
                  where pa.product_id = p.id
                    and attr.sorter = 2
                    and attr.id=2)
where deleted is false
  and type in ('LiquorVodka',
               'Vodka',
               'Wine',
               'AlcoholTechnical',
               'CognacAlcohol',
               'Champagne',
               'Liquor',
               'Tincture',
               'Beer',
               'WineMaterial',
               'Cognac',
               'Alcohol',
               'NaturalWine',
               'Ethereum');
-- p.id = 'ad263a70-6eb3-4c15-b417-dbf78bf6f326';

update product p
set visible_name=(select concat(trim(p.visible_name), ' ', trim(pa.attribute_value), trim(attr.postfix), '.')
                  from product_attributes pa
                           left join attributes attr on pa.attribute_id = attr.id
                  where pa.product_id = p.id
                    and attr.sorter = 3
                    and attr.id = 1)
where deleted is false
  and type in ('LiquorVodka',
               'WaterAndSoftDrinks',
               'Champagne',
               'Vodka',
               'Wine',
               'Medicines',
               'Liquor',
               'Tincture',
               'WineMaterial',
               'Beer',
               'NaturalWine',
               'Cognac');
-- p.id = 'ad263a70-6eb3-4c15-b417-dbf78bf6f326';
