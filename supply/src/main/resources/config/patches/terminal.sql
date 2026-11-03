drop table fiscalization cascade ;
drop table device_model cascade ;
drop table device cascade ;
drop table fiscal_module cascade ;


alter table terminal alter column branch_id drop not null;

alter table terminal add column if not exists company_id bigint not null;

CREATE INDEX IF NOT EXISTS terminal_deleted_idx  ON terminal (deleted);