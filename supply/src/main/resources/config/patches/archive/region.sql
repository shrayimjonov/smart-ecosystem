INSERT INTO "region" (code,name_ru,name_uz) VALUES
(3, 'Андижанская Область', 'Andijon Viloyati'),
(6, 'Бухарская Область', 'Buxoro Viloyati'),
(8, 'Джизакская Область', 'Jizzax Viloyati'),
(10, 'Кашкадарьинская Область', 'Qashqadaryo Viloyati'),
(12, 'Навоийская Область', 'Navoiy Viloyati'),
(14, 'Наманганская Область', 'Namangan Viloyati'),
(18, 'Самаркандская Область', 'Samarqand Viloyati'),
(22, 'Сурхандарьинская Область', 'Surxondaryo Viloyati'),
(24, 'Сырдарьинская Область', 'Sirdaryo Viloyati'),
(26, 'Город Ташкент', 'Toshkent Shahar'),
(27, 'Ташкентская Область', 'Toshkent Viloyati'),
(30, 'Ферганская Область', 'Fargona Viloyati'),
(33, 'Хорезмская Область', 'Xorazm Viloyati'),
(35, 'Респ. Каракалпакстан', 'Qoraqalpogiston Resp.');


--set chart codes

update region set chart_code = 'uz-ta' where region.code=27;
update region set chart_code = 'uz-qa' where region.code=10;
update region set chart_code = 'uz-sa' where region.code=18;
update region set chart_code = 'uz-qr' where region.code=35;
update region set chart_code = 'uz-fa' where region.code=30;
update region set chart_code = 'uz-si' where region.code=24;
update region set chart_code = 'uz-bu' where region.code=6;
update region set chart_code = 'uz-ji' where region.code=8;
update region set chart_code = 'uz-su' where region.code=22;
update region set chart_code = 'uz-kh' where region.code=33;
update region set chart_code = 'uz-ng' where region.code=14;
update region set chart_code = 'uz-nw' where region.code=12;
update region set chart_code = 'uz-tk' where region.code=26;
update region set chart_code = 'uz-an' where region.code=3;
