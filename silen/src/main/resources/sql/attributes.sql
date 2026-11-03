INSERT INTO public.attributes (id, created_by, created_date, last_modified_by, last_modified_date, deleted, additional,
                               name, parent_id, postfix, prefix, visible, sorter)
VALUES (1, 'a5ap2021', '2022-08-12 16:23:11.404000', 'a5ap2021', '2022-08-17 12:07:06.223000', false,
        '{"type": "SELECT", "options": ["Стеклянная тара", "ПЭТ тара", "Алюминиевая тара"]}', 'Потребительская тара',
        null, null, null, true, 1);
INSERT INTO public.attributes (id, created_by, created_date, last_modified_by, last_modified_date, deleted, additional,
                               name, parent_id, postfix, prefix, visible, sorter)
VALUES (2, 'a5ap2021', '2022-08-12 17:32:02.121000', 'a5ap2021', '2022-08-17 12:05:28.561000', false, null, 'Градус',
        null, '%', '', true, 2);
INSERT INTO public.attributes (id, created_by, created_date, last_modified_by, last_modified_date, deleted, additional,
                               name, parent_id, postfix, prefix, visible, sorter)
VALUES (3, 'a5ap2021', '2022-08-12 16:40:52.401000', 'a5ap2021', '2022-08-17 12:04:55.443000', false, null, 'Объем',
        null, 'л', '', true, 3);

INSERT INTO public.attributes (id, created_by, created_date, last_modified_by, last_modified_date, deleted, additional,
                               name, parent_id, postfix, prefix, visible, sorter)
VALUES (4, 'a5ap2021', '2022-08-11 15:30:48.266000', 'a5ap2021', '2022-08-17 12:05:48.932000', false,
    '{"code": "TRANSFORM_TO", "type": "CUSTOM"}', 'Преобразовать в', null, null, null, false, 4);
INSERT INTO public.attributes (id, created_by, created_date, last_modified_by, last_modified_date, deleted, additional,
                               name, parent_id, postfix, prefix, visible, sorter)
VALUES (5, 'a5ap2021', '2022-08-12 16:39:11.282000', 'a5ap2021', '2022-08-17 12:06:49.155000', false,
        '{"type": "SELECT", "options": ["стеклянный флакон", "полимерный флакон", "флакон", "блистер ", "ампула", "туба", "пенал", "бутылка", "пакет", "коробка", "пробирка", "упаковка ячейковая контурная"]}',
        'Потребительская тара', null, null, null, false, 5);
INSERT INTO public.attributes (id, created_by, created_date, last_modified_by, last_modified_date, deleted, additional,
                               name, parent_id, postfix, prefix, visible, sorter)
VALUES (6, 'a5ap2021', '2022-08-12 16:33:57.820000', 'a5ap2021', '2022-08-17 12:05:13.051000', false, null, 'Объем',
        null, 'дал', '', false, 6);
INSERT INTO public.attributes (id, created_by, created_date, last_modified_by, last_modified_date, deleted, additional,
                               name, parent_id, postfix, prefix, visible, sorter)
VALUES (7, 'a5ap2021', '2022-08-12 16:40:41.280000', 'a5ap2021', '2022-08-17 12:04:30.802000', false, null, 'Объем',
        null, 'мл', '', false, 7);
INSERT INTO public.attributes (id, created_by, created_date, last_modified_by, last_modified_date, deleted, additional,
                               name, parent_id, postfix, prefix, visible, sorter)
VALUES (8, 'a5ap2021', '2022-08-12 16:42:41.260000', 'a5ap2021', '2022-08-17 12:06:25.144000', false, null,
        'Количество', null, 'шт', '', false, 8);
INSERT INTO public.attributes (id, created_by, created_date, last_modified_by, last_modified_date, deleted, additional,
                               name, parent_id, postfix, prefix, visible, sorter)
VALUES (9, 'a5ap2021', '2022-08-12 16:43:07.454000', 'a5ap2021', '2022-08-17 12:08:15.725000', false,
        '{"type": "SELECT", "options": ["Пенал", "Коробка"]}', 'Вторичная упаковка', null, null, null, false, 9);



INSERT INTO public.attributes_product_type (attribute_id, product_types)
VALUES (3, 'LiquorVodka'),
       (3, 'WaterAndSoftDrinks'),
       (3, 'Champagne'),
       (3, 'Vodka'),
       (3, 'Wine'),
       (3, 'Medicines'),
       (3, 'Liquor'),
       (3, 'Tincture'),
       (3, 'WineMaterial'),
       (3, 'Beer'),
       (3, 'NaturalWine'),
       (3, 'Cognac');

INSERT INTO public.attributes_product_type (attribute_id, product_types)
VALUES (2, 'LiquorVodka'),
       (2, 'Vodka'),
       (2, 'Wine'),
       (2, 'AlcoholTechnical'),
       (2, 'CognacAlcohol'),
       (2, 'Champagne'),
       (2, 'Liquor'),
       (2, 'Tincture'),
       (2, 'Beer'),
       (2, 'WineMaterial'),
       (2, 'Cognac'),
       (2, 'Alcohol'),
       (2, 'NaturalWine'),
       (2, 'Ethereum');

INSERT INTO public.attributes_product_type (attribute_id, product_types)
VALUES (1, 'LiquorVodka'),
       (1, 'WaterAndSoftDrinks'),
       (1, 'Champagne'),
       (1, 'Vodka'),
       (1, 'Wine'),
       (1, 'Liquor'),
       (1, 'Tincture'),
       (1, 'WineMaterial'),
       (1, 'Beer'),
       (1, 'NaturalWine'),
       (1, 'Cognac');

INSERT INTO public.attributes_product_type (attribute_id, product_types)
VALUES (7, 'Medicines');

INSERT INTO public.attributes_product_type (attribute_id, product_types)
VALUES (8, 'Cigarette'),
       (8, 'Medicines');

INSERT INTO public.attributes_product_type (attribute_id, product_types)
VALUES (4, 'Alcohol');

INSERT INTO public.attributes_product_type (attribute_id, product_types)
VALUES (9, 'LiquorVodka'),
       (9, 'Vodka'),
       (9, 'Wine'),
       (9, 'Cigarette'),
       (9, 'WaterAndSoftDrinks'),
       (9, 'Champagne'),
       (9, 'Medicines'),
       (9, 'Liquor'),
       (9, 'Tincture'),
       (9, 'Beer'),
       (9, 'WineMaterial'),
       (9, 'Cognac'),
       (9, 'NaturalWine');

INSERT INTO public.attributes_product_type (attribute_id, product_types)
VALUES (5, 'Medicines');

INSERT INTO public.attributes_product_type (attribute_id, product_types)
VALUES (6, 'AlcoholTechnical'),
       (6, 'Alcohol'),
       (6, 'Ethereum');
