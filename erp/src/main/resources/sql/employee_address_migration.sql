INSERT INTO address (deleted, created_date, creator_id, last_modified_date, last_modifier_id, entity_id, entity_type, type, landmark, street, house, apartment, longitude, latitude, region_id, district_id, neighborhood_id)
SELECT false, now(), 1, now(), 1,id, 'EMPLOYEE', 'PERMANENT', landmark, street, house, apartment, longitude, latitude, region_id, district_id, neighborhood_id
FROM jhi_user;

alter table jhi_user drop column landmark;
alter table jhi_user drop column street;
alter table jhi_user drop column house;
alter table jhi_user drop column apartment;
alter table jhi_user drop column longitude;
alter table jhi_user drop column latitude;
alter table jhi_user drop column region_id;
alter table jhi_user drop column district_id;
alter table jhi_user drop column neighborhood_id;