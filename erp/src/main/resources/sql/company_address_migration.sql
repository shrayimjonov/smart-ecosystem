INSERT INTO address (deleted, created_date, creator_id, last_modified_date, last_modifier_id, entity_id, entity_type, type, landmark, street, house, apartment, longitude, latitude, region_id, district_id, neighborhood_id)
SELECT false, now(), 1, now(), 1,id, 'COMPANY', 'LEGAL', landmark, street, house, apartment, longitude, latitude, region_id, district_id, neighborhood_id
FROM company;

alter table company drop column landmark;
alter table company drop column street;
alter table company drop column house;
alter table company drop column apartment;
alter table company drop column longitude;
alter table company drop column latitude;
alter table company drop column region_id;
alter table company drop column district_id;
alter table company drop column neighborhood_id;