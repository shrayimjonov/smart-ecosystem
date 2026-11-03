insert into reference(id, created_date, created_by, code, name) values (uuid_generate_v4(), now(), 'system', '_EQUIPMENT_KIND', 'Вид оборудование');
insert into reference(id, created_date, created_by, code, name) values (uuid_generate_v4(), now(), 'system', '_EQUIPMENT_TYPE', 'Тип оборудование');
insert into reference(id, created_date, created_by, code, name) values (uuid_generate_v4(), now(), 'system', '_EQUIPMENT_BRAND', 'Бренд оборудование');
insert into reference(id, created_date, created_by, code, name) values (uuid_generate_v4(), now(), 'system', '_EQUIPMENT_MODEL', 'Модель оборудование');
insert into reference(id, created_date, created_by, code, name) values (uuid_generate_v4(), now(), 'system', '_PACKAGE_TYPE', 'Тип упаковки');

insert into reference(id, created_date, created_by, code, name, parent_id) values (uuid_generate_v4(), now(), 'system', 'ALUMINUM_PACKAGING', 'Алюминиевая тара', '_PACKAGE_TYPE');
insert into reference(id, created_date, created_by, code, name, parent_id) values (uuid_generate_v4(), now(), 'system', 'GLASS_CONTAINER', 'Стеклянная тара', '_PACKAGE_TYPE');
insert into reference(id, created_date, created_by, code, name, parent_id) values (uuid_generate_v4(), now(), 'system', 'PET_CONTAINER', 'ПЭТ тара', '_PACKAGE_TYPE');


