create sequence if not exists sequence_generator;

create sequence if not exists virtual_serial_number_seq maxvalue 9999999;

create sequence if not exists product_id_sequence;

create sequence if not exists category_id_sequence;

create table if not exists activity_type
(
    id               bigserial primary key,
    code             bigint,
    name_ru          varchar(255),
    name_uz_cyrillic varchar(255),
    name_uz_latin    varchar(255),
    parent_id        bigint
        constraint activity_type_parent_fk references activity_type
);

create table if not exists application_route
(
    id                 bigint      not null primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    annotation_name    varchar(255)
        constraint application_route_annotation_name_un_idx unique,
    end_point          varchar(255),
    menu_name          varchar(255),
    parent_id          bigint
        constraint application_route_parent_fk references application_route,
    position           integer,
    section            boolean default false
);

create table if not exists authority
(
    name varchar(50) not null primary key
);

create table if not exists company
(
    id                  bigint      not null primary key,
    created_by          varchar(50) not null,
    created_date        timestamp,
    last_modified_by    varchar(50),
    last_modified_date  timestamp,
    address             varchar(500),
    brand               varchar(255),
    business_type       varchar(255),
    city_id             bigint,
    inn                 varchar(14)
        constraint company_inn_un_idx unique,
    name                varchar(255),
    nds_percent         numeric(5, 2),
    oked                varchar(50),
    pays_nds            boolean default true,
    warehouse_enabled   boolean default true,
    auto_print_enabled  boolean default true,
    region_id           bigint,
    bank_id             bigint,
    owner_id            bigint,
    fiscal              boolean default false,
    sync_date           timestamp,
    cto_id              bigint,
    old_inn             varchar(14),
    binded              boolean default false,
    apay_id             varchar,
    cadastre_file_id    varchar,
    application_file_id varchar,
    account_number      varchar(255),
    application_status  varchar(255),
    mfo                 varchar(255),
    balance             integer default 0,
    fiscal_number       varchar,
    has_certificate     boolean default false,
    ofd_file_id         varchar(255),
    commitent           boolean default false,
    free_trial          boolean default false,
    logo_id             bigint,
    manager_id          bigint,
    logo_url            varchar,
    trial_date          timestamp,
    admin               boolean default false,
    billing_type        varchar(50),
    apay_tariff_id      varchar(50),
    support_id          bigint,
    true_token          text,
    token_date          timestamp
);

create unique index if not exists company_inn_un_idx
    on company (inn);

create table if not exists company_activity_type
(
    company_id       bigint not null
        constraint fk4t2ua3tffj5v2w9cwwlhqy0tt
            references company,
    activity_type_id bigint not null
        constraint fkrv55uxuqy8g0qq6dwqas2t59k
            references activity_type,
    primary key (company_id, activity_type_id)
);

create table if not exists company_payment_types
(
    company_id   bigint       not null
        constraint fkqfn0tkiq1gfp4q80g2vfu8pn7
            references company,
    payment_type varchar(255) not null,
    primary key (company_id, payment_type)
);

create table if not exists data_entry
(
    id           bigserial
        primary key,
    company_id   bigint
        constraint fkcfjmydihpok9dkokpli4w4rx7
            references company,
    deleted      boolean default false,
    status       varchar(255),
    agreement_id bigint,
    approve_date timestamp
);

create index if not exists data_entry_company_idx
    on data_entry (company_id);

create table if not exists loyalty
(
    id                 bigserial
        primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    card_type          varchar(255),
    company_id         bigint      not null,
    creator_id         bigint,
    deleted            boolean default false,
    name               varchar(255),
    status             varchar(255)
);

create table if not exists package_type
(
    id          bigserial
        primary key,
    checksum    bigint,
    code        bigint,
    description varchar(255),
    name        varchar(255) not null,
    name_uz     varchar(255) not null
);

create index if not exists package_type_name_idx
    on package_type (name);

create table if not exists permission
(
    code varchar(255) not null
        primary key
);

create table if not exists product_price_history
(
    id                bigint not null
        primary key,
    branch_id         bigint,
    new_price         numeric(19, 2),
    old_price         numeric(19, 2),
    product_id        bigint,
    updated_by_id     bigint,
    updated_date_time timestamp,
    city_id           bigint
);

create index if not exists price_history_product_idx
    on product_price_history (product_id);

create index if not exists price_history_pro_bra_idx
    on product_price_history (product_id, branch_id);

create table if not exists receipt
(
    id                bigint      not null
        primary key,
    branch_id         bigint      not null,
    company_id        bigint      not null,
    created_by_id     bigint,
    created_date_time timestamp,
    discount_percent  numeric(25, 2),
    fiscal_sign       varchar(255),
    fiscal_url        text,
    origin_uid        varchar(50),
    receipt_date_time timestamp,
    status            varchar(50),
    terminal_id       bigint,
    total_card        numeric(25, 4),
    total_cash        numeric(25, 4),
    total_cost        numeric(25, 4),
    total_discount    numeric(19, 2),
    total_nds         numeric(25, 4),
    total_paid        numeric(25, 4),
    uid               varchar(50) not null
        constraint uk_p1r2axaqheim1u4qdvteleltn
            unique,
    lat               double precision,
    lon               double precision,
    total_excise      numeric(25, 4),
    shift_number      bigint,
    source            varchar(50),
    force_to_print    boolean default false,
    read_only         boolean default false,
    customer_name     varchar(255),
    customer_phone    varchar(255),
    additional        json,
    bill_id           varchar(255),
    factura_id        varchar(255)
        constraint uk_kybc7vycb4bgxcn1rd8h1w7f7
            unique,
    send_sms          boolean default false
);

create index if not exists receipt_date_time_idx
    on receipt using brin (receipt_date_time);

create index if not exists receipt_terminal_status_idx
    on receipt (terminal_id, status);

create index if not exists rec_br_ter_sta_idx
    on receipt (branch_id, terminal_id, status);

create index if not exists rec_com_br_sta_idx
    on receipt (company_id, branch_id, status);

create index if not exists rec_br_sta_idx
    on receipt (branch_id, status);

create index if not exists rec_br_sta_sour_idx
    on receipt (branch_id, status, source);

create table if not exists receipt_detail
(
    id                bigint         not null
        primary key,
    amount            numeric(25, 4) not null,
    discount          numeric(19, 2),
    discount_percent  numeric(19, 2),
    excise            numeric(25, 4),
    excise_rate       numeric(19, 2),
    nds               numeric(25, 4),
    nds_percent       numeric(19, 2),
    price             numeric(25, 4) not null,
    product_id        bigint,
    product_name      text,
    qty               numeric(25, 4) not null,
    receipt_id        bigint
        constraint fk4qgqei3yfmquqnhwutybgv0dj
            references receipt,
    status            varchar(100),
    unit_id           bigint,
    unit_name         varchar(255),
    product_barcode   varchar(255),
    category_id       bigint,
    category_name     varchar(255),
    receipt_date_time timestamp,
    vat_barcode       varchar(255),
    committent_tin    varchar(14),
    extra_detail      jsonb
);

create table if not exists detail_marks
(
    receipt_detail_id bigint not null
        constraint fkpx92wnp2gcew1cxyul9b9doyq
            references receipt_detail,
    marks             varchar(255)
);

create index if not exists detail_marks_receipt_detail_id_idx
    on detail_marks (receipt_detail_id);

create index if not exists receipt_detail_product_idx
    on receipt_detail (product_id);

create index if not exists receipt_detail_receipt_idx
    on receipt_detail (receipt_id);

create table if not exists receipt_payments
(
    receipt_id   bigint       not null
        constraint fknpygyk34yjsblp0chkq5ty04r
            references receipt,
    amount       numeric(25, 4),
    payments_key varchar(255) not null,
    primary key (receipt_id, payments_key)
);

create table if not exists region
(
    id          bigserial
        primary key,
    code        bigint
        constraint region_code_idx
            unique,
    name_ru     varchar(255)
        constraint region_name_idx
            unique
        constraint uk_p2n016ruvilvsoaqqcivbxx8i
            unique,
    name_uz     varchar(255)
        constraint uk_kmsb5h2xsp3b53qadwmc8fg4i
            unique,
    region_code varchar
);

create table if not exists city
(
    id          bigserial
        primary key,
    code        bigint,
    name_ru     varchar(255),
    name_uz     varchar(255),
    region_code bigint
        constraint fkbf7x9d18dh2ij1edcuqwmhuwk
            references region (code),
    city_code   varchar(255),
    constraint ukadv26e1s2bedibd2fa4qergi6
        unique (code, region_code)
);

create index if not exists city_name_idx
    on city (name_ru);

create index if not exists city_region_idx
    on city (region_code);

create table if not exists register
(
    id            bigint not null
        primary key,
    branch_id     bigint not null,
    product_id    bigint not null,
    balance       numeric(19, 2),
    company_id    bigint,
    unit_id       bigint,
    marking_count bigint,
    mxik          varchar(17),
    city_id       bigint,
    product_name  text,
    sales_price   numeric(19, 2),
    cost_price    numeric(19, 2),
    category_id   bigint,
    has_mark      boolean,
    constraint uk1ch9pbxe6gwgm52033mjumwcm
        unique (branch_id, product_id)
);

create index if not exists register_branch_idx
    on register (branch_id);

create index if not exists register_branch_product_idx
    on register (branch_id, product_id);

create index if not exists register_product_idx
    on register (product_id);

create table if not exists shift
(
    id              bigint not null
        primary key,
    branch_id       bigint,
    end_date_time   timestamp,
    number          bigint,
    start_date_time timestamp,
    status          varchar(255),
    user_id         bigint,
    fiscal_number   bigint,
    terminal_id     bigint
);

create index if not exists shift_branch_idx
    on shift (branch_id);

create index if not exists shift_date_status_idx
    on shift (start_date_time, end_date_time, status);

create index if not exists shift_number_branch_idx
    on shift (number, branch_id);

create index if not exists shift_number_idx
    on shift (number);

create index if not exists shift_status_idx
    on shift (status);

create index if not exists shift_status_user_idx
    on shift (status, user_id);

create index if not exists shift_user_idx
    on shift (user_id);

create table if not exists shift_report
(
    id                 bigint      not null
        primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    nds                numeric(19, 2),
    branch_id          bigint,
    card               numeric(19, 2),
    card_returned      numeric(19, 2),
    cash               numeric(19, 2),
    cash_returned      numeric(19, 2),
    end_date_time      timestamp,
    nds_returned       numeric(19, 2),
    number             bigint,
    receipts           bigint,
    receipts_returned  bigint,
    start_date_time    timestamp,
    terminal_id        bigint,
    user_id            bigint
);

create index if not exists shift_report_terminal_branch_idx
    on shift_report (terminal_id, branch_id);

create index if not exists shift_report_branch_idx
    on shift_report (branch_id);

create table if not exists stock_adjustment
(
    id                 bigint      not null
        primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    branch_id          bigint      not null,
    description        varchar(255),
    reason             varchar(255),
    user_id            bigint
);

create table if not exists stock_adjustment_item
(
    id                  bigint not null
        primary key,
    product_id          bigint not null,
    qty                 numeric(19, 2),
    stock_adjustment_id bigint not null
        constraint fkc3jj79l0oscothk8vgs5rxel8
            references stock_adjustment,
    unit_id             bigint,
    branch_id           bigint,
    city_id             bigint
);

create table if not exists transfer
(
    id                 bigint       not null
        primary key,
    created_by         varchar(50)  not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    approved_by_id     bigint,
    cancelled_by_id    bigint,
    company_id         bigint,
    description        varchar(255),
    from_branch_id     bigint       not null,
    status             varchar(255) not null,
    to_branch_id       bigint       not null,
    transfer_date      timestamp
);

create index if not exists transfer_company_idx
    on transfer (company_id);

create index if not exists transfer_from_branch_idx
    on transfer (from_branch_id);

create index if not exists transfer_from_to_branch_idx
    on transfer (from_branch_id, to_branch_id);

create index if not exists transfer_status_idx
    on transfer (status);

create index if not exists transfer_to_branch_idx
    on transfer (to_branch_id);

create table if not exists transfer_detail
(
    id          bigint not null
        primary key,
    asked_qty   numeric(19, 2),
    product_id  bigint not null,
    real_qty    numeric(19, 2),
    transfer_id bigint not null
        constraint fk9oeu1clo1tyxonahctnvay6us
            references transfer,
    unit_id     bigint not null,
    sales_price numeric(19, 2),
    markings    jsonb,
    branch_id   bigint,
    city_id     bigint
);

create table if not exists unit
(
    id          bigserial
        primary key,
    code        bigint
        constraint uk_csaj16dw8fhmuj2q7n9qagq35
            unique,
    countable   boolean default false,
    description varchar(255),
    name        varchar(255) not null,
    name_uz     varchar(255) not null
);

create index if not exists unit_name_idx
    on unit (name);

create table if not exists user_session
(
    id              bigint not null
        primary key,
    end_date_time   timestamp,
    shift_id        bigint,
    start_date_time timestamp,
    status          varchar(255),
    user_id         bigint,
    terminal_id     bigint
);

create index if not exists user_session_date_status_idx
    on user_session (start_date_time, end_date_time, status);

create index if not exists user_session_status_idx
    on user_session (status);

create index if not exists user_session_status_user_idx
    on user_session (status, user_id);

create index if not exists user_session_user_idx
    on user_session (user_id);

create table if not exists vat
(
    id                 bigint      not null
        primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    name               varchar(255),
    percent            numeric(19, 2),
    constraint ukpn6b5ik8yq53h1h3kpok29dv6
        unique (name, percent)
);

create table if not exists operator_permission
(
    id                 bigserial
        primary key,
    activated          boolean not null,
    client_id          bigint,
    code               varchar(255),
    company_id         bigint,
    deleted            boolean default false,
    expire_date        timestamp,
    tin                varchar(255),
    user_id            bigint,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    created_by         varchar(50)
);

create index if not exists tin_idx
    on operator_permission (tin);

create index if not exists user_id_idx
    on operator_permission (user_id);

create index if not exists user_id_tin_idx
    on operator_permission (user_id, tin);

create table if not exists sms
(
    id            bigserial
        primary key,
    deleted       boolean default false,
    message       text,
    provider_type varchar(255),
    recipient     varchar(255),
    status        varchar(255),
    created_date  timestamp
);

create table if not exists product_for_marking
(
    marking              varchar(255) not null
        primary key,
    branch_id            bigint,
    children             jsonb,
    product_id           bigint,
    status               varchar(255),
    parent               varchar(255),
    created_date         timestamp default now(),
    error_message        text,
    income_of_product_id bigint
);

create index if not exists product_for_marking_product_id_and_branch_id_idx
    on product_for_marking (product_id, branch_id);

create index if not exists product_for_marking_created_date_idx
    on product_for_marking using brin (created_date);

create index if not exists product_for_marking_parent
    on product_for_marking (parent);

create table if not exists draft_invoice
(
    id                         bigserial
        primary key,
    created_by                 varchar(50) not null,
    created_date               timestamp,
    last_modified_by           varchar(50),
    last_modified_date         timestamp,
    account                    varchar(255),
    address                    varchar(255),
    agent_factura_id           varchar(24),
    agent_fio                  varchar(255),
    agent_pinfl                varchar(255),
    agent_tin                  varchar(255),
    bank                       varchar(255),
    contract_date              date,
    contract_number            varchar(255),
    country_id                 varchar(255),
    empowerment_date_of_expire date,
    empowerment_date_of_issue  date,
    empowerment_no             varchar(255),
    factura_product_id         varchar(24),
    has_comittent              boolean,
    has_excise                 boolean,
    has_lgota                  boolean,
    has_marking                boolean,
    has_medical                boolean,
    has_vat                    boolean,
    invoice_date               date,
    item_released_fio          varchar(255),
    item_released_pinfl        varchar(255),
    item_released_tin          varchar(255),
    lot_id                     varchar(255),
    name                       varchar(255),
    number                     varchar(255),
    old_factura_date           date,
    old_factura_id             varchar(24),
    old_factura_no             varchar(255),
    single_sided_type          integer,
    tin                        varchar(255),
    type                       integer,
    version                    integer default 1,
    buyer_account              varchar(255),
    buyer_accountant           varchar(255),
    buyer_address              varchar(255),
    buyer_bank_id              varchar(255),
    buyer_branch_code          varchar(255),
    buyer_branch_name          varchar(255),
    buyer_director             varchar(255),
    buyer_district_id          varchar(255),
    buyer_mobile               varchar(255),
    buyer_name                 varchar(255),
    buyer_oked                 varchar(255),
    buyer_tax_gap              numeric(25, 2),
    buyer_tin                  varchar(255),
    buyer_vat_reg_code         varchar(255),
    buyer_vat_reg_status       integer,
    buyer_work_phone           varchar(255),
    seller_account             varchar(255),
    seller_accountant          varchar(255),
    seller_address             varchar(255),
    seller_bank_id             varchar(255),
    seller_branch_code         varchar(255),
    seller_branch_name         varchar(255),
    seller_director            varchar(255),
    seller_district_id         varchar(255),
    seller_mobile              varchar(255),
    seller_name                varchar(255),
    seller_oked                varchar(255),
    seller_tax_gap             numeric(25, 2),
    seller_tin                 varchar(255),
    seller_vat_reg_code        varchar(255),
    seller_vat_reg_status      integer,
    seller_work_phone          varchar(255)
);

create table if not exists invoice
(
    id                 bigserial
        primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    approved_by        varchar(255),
    approved_date      timestamp,
    approved_number    varchar(255),
    factura_id         varchar(24)
        constraint uk_ecr8xm0w2b1078dqtgi80jhl5
            unique,
    invoice_date       date,
    notes              text,
    number             varchar(255),
    seller_name        varchar(255),
    seller_tin         varchar(255),
    send_by            varchar(255),
    send_date          timestamp,
    send_number        varchar(255),
    signature_content  text,
    status             varchar(20),
    total              numeric(25, 2),
    factura_product_id varchar(24),
    invoice_id         bigint
        constraint fkbw9y8tlrymtai7ibuhhi517s3
            references draft_invoice,
    buyer_name         varchar(255),
    buyer_tin          varchar(255),
    deleted            boolean default false,
    facture_id         varchar(255),
    invoice_number     varchar(255),
    template           text,
    import_mark_status varchar(20)
);

create table if not exists invoice_item
(
    id                     bigserial
        primary key,
    barcode                varchar(32),
    base_price             numeric(25, 2),
    catalog_code           varchar(32),
    catalog_name           text,
    comittent_name         varchar(50),
    comittent_tin          varchar(9),
    comittent_vat_reg_code varchar(12),
    excise_rate            numeric(25, 2),
    excise_total           numeric(25, 2),
    lgota_id               integer,
    lgota_name             varchar(255),
    lgota_type             integer,
    lgota_vat_sum          numeric default 0,
    measure_id             bigint,
    name                   varchar(255),
    order_no               varchar(5),
    package_code           varchar(255),
    package_name           varchar(255),
    price                  numeric(25, 2),
    product_type           integer,
    profit_rate            numeric(25, 2),
    qty                    numeric(25, 2),
    serial                 varchar(20),
    subtotal               numeric(25, 2),
    total                  numeric(25, 2),
    vat_rate               numeric(25, 2),
    vat_total              numeric(25, 2),
    without_vat            boolean,
    invoice_id             bigint
        constraint fkhm3kptk6wxaeo8sfr2eaed7nc
            references draft_invoice,
    branch_id              bigint,
    origin                 integer default 2
);

create table if not exists kiz
(
    invoice_item_id bigint not null
        constraint fkelvcvxvv492wqfl079ik27wci
            references invoice_item,
    kiz             varchar(255)
);

create table if not exists nom_upak
(
    invoice_item_id bigint not null
        constraint fknuvctmcxbsbpa7babpg5ej6w5
            references invoice_item,
    nom_upak        varchar(255)
);

create table if not exists trans_upak
(
    invoice_item_id  bigint not null
        constraint fk3nwsxh022907shbd65q3yjv5x
            references invoice_item,
    ident_trans_upak varchar(255)
);

create table if not exists billing_permission
(
    id                 bigserial
        primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    amount             numeric(19, 2),
    company_id         bigint
        constraint fkafpxfk02ttjictduyfblunu6u
            references company,
    end_date           timestamp,
    service_type       varchar(255),
    start_date         timestamp,
    unit_count         integer,
    tin                varchar,
    pinfl              varchar,
    bonus_amount       numeric(25, 4),
    name_ru            varchar,
    name_uz_cyrillic   varchar,
    name_uz_latin      varchar
);

create table if not exists company_balances
(
    company_id   bigint       not null
        constraint fk6h84ybaksymdumgccng6e77ip
            references company,
    balance      integer,
    balances_key varchar(255) not null,
    primary key (company_id, balances_key)
);

create table if not exists company_permissions
(
    company_id      bigint       not null
        constraint fkmg9n4q60rdflepotvygq02uue
            references company,
    expire_date     timestamp,
    permissions_key varchar(255) not null,
    primary key (company_id, permissions_key)
);

create table if not exists mxik
(
    mxik          varchar(17) not null
        primary key,
    barcode       varchar(17),
    name_cyril    varchar(1000),
    name_ru       varchar(1000),
    name_uz_latin varchar(1000),
    packages      jsonb,
    group_name    varchar(255),
    class_name    varchar(255),
    position      varchar(255),
    sub_position  varchar(255)
);

create index if not exists mxik_code_idx
    on mxik (mxik);

create index if not exists mxik_barcode_idx
    on mxik (barcode);

create index if not exists mxik_name_idx
    on mxik (name_ru, name_uz_latin, name_cyril);

create table if not exists bank
(
    id      varchar(255) not null
        primary key,
    name_ru varchar(255),
    name_uz varchar(255)
);

create table if not exists branch_group
(
    id                 bigserial
        primary key,
    created_by         varchar(50)  not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    company_id         bigint       not null
        constraint fk68xsq53oqqtr6b1571rfevw0q
            references company,
    name               varchar(255) not null,
    constraint ukh0yxfp0q0sy6m6cep3e35lspg
        unique (company_id, name)
);

create table if not exists file_upload
(
    id            bigserial
        primary key,
    object_name   varchar(255),
    url           varchar,
    entity_id     bigint,
    entity_type   varchar(255),
    content_type  varchar(255),
    size          bigint,
    document_type varchar(255)
);

create table if not exists receipt_template
(
    id                        bigserial
        primary key,
    company_id                bigint,
    footer_alignment          varchar(255),
    footer_image              bytea,
    footer_image_content_type varchar(255),
    footer_text               varchar(255),
    header_alignment          varchar(255),
    header_image              bytea,
    header_image_content_type varchar(255),
    header_text               varchar(255),
    header_id                 bigint
        constraint receipt_template_header_id_fk
            references file_upload,
    footer_id                 bigint
        constraint receipt_template_footer_id_fk
            references file_upload
);

create table if not exists requisition
(
    id                bigserial
        primary key,
    service_name      varchar(255),
    phone             varchar,
    order_date        timestamp,
    claim             text,
    claim_description text,
    accepted          boolean,
    accepted_date     timestamp
);

create table if not exists excise
(
    mxik  varchar(255) not null
        primary key
        constraint excise_mxik_fk
            references mxik,
    name  text         not null,
    price numeric(25, 4)
);

-- create table if not exists promotion
-- (
--     id                 bigint                not null
--         primary key,
--     created_by         varchar(255)          not null,
--     created_date       timestamp,
--     last_modified_by   varchar(255),
--     last_modified_date timestamp,
--     promotion_name     varchar(255)          not null,
--     date_active_from   date                  not null,
--     date_active_to     date,
--     company_id         bigint                not null,
--     branch_id          bigint                not null,
--     repeat_type        varchar(255),
--     include_vat        boolean default true  not null,
--     count_repetitions  boolean default false not null,
--     promotion_status   varchar(255)
-- );
--
-- create table if not exists promotion_repeats
-- (
--     promotion_id bigint not null,
--     repeats      integer
-- );

create table if not exists terminal_transfer
(
    id                 bigserial
        primary key,
    description        varchar(1000),
    status             varchar(50) not null,
    transfer_date      timestamp,
    from_branch_id     bigint,
    to_branch_id       bigint,
    created_by         varchar(50),
    created_date       timestamp default now(),
    last_modified_by   varchar(50),
    last_modified_date timestamp default now(),
    file_id            bigint
);

create table if not exists pricing
(
    id    bigserial
        primary key,
    model varchar(50) not null
        unique,
    price numeric
);


create table if not exists bill
(
    id                 bigserial
        primary key,
    amount             numeric,
    status             varchar(50),
    branch_id          bigint,
    customer_id        bigint,
    customer_branch_id bigint,
    manager_id         bigint,
    terminal_id        bigint,
    created_date       timestamp default now(),
    bill_id            varchar(50),
    fiscal_module_id   bigint,
    by_act             boolean   default false,
    act_id             bigint
        constraint bill_act_fk
            references file_upload,
    agreement_id       bigint
        constraint bill_agreement_fk
            references file_upload,
    humo_request_id    bigint,
    humo_contract_id   bigint,
    fiscal_url         varchar(255),
    by_agreement       boolean   default false,
    doc_flow_status    varchar,
    error_message      varchar(250)
);

create table if not exists ofd_application
(
    id                   bigserial
        primary key,
    ofd_id               bigint,
    application_file_url varchar(255),
    status               varchar(50),
    application_type     varchar(50),
    customer_id          bigint,
    customer_branch_id   bigint,
    terminal_id          bigint,
    fiscal_module_id     bigint,
    manager_id           bigint,
    reason               text,
    swap_reason          varchar(50),
    reason_text          text,
    created_date         timestamp   default now(),
    created_by           varchar(50) default 'system'::character varying,
    last_modified_date   timestamp   default now(),
    last_modified_by     varchar(50) default 'system'::character varying,
    admin_branch_id      bigint,
    file_id              bigint
);

create table if not exists device_history
(
    id           bigserial
        primary key,
    device_id    bigint,
    device_type  varchar(50),
    history      text,
    history_date timestamp default now()
);

create table if not exists bonus
(
    id        bigserial
        primary key,
    role      varchar(50)                                      not null,
    condition varchar(100)                                     not null,
    type      varchar(100) default 'AMOUNT'::character varying not null,
    amount    numeric                                          not null,
    unique (role, condition)
);

create table if not exists neighborhood
(
    id          bigserial
        constraint neighborhood_pk
            primary key,
    code        bigint not null
        constraint neighborhood_code_uk
            unique,
    name        varchar(1000),
    city_code   bigint not null,
    region_code bigint not null,
    constraint neighborhood_city_fk
        foreign key (city_code, region_code) references city (code, region_code)
);

create table if not exists branch
(
    id                   bigint       not null
        primary key,
    created_by           varchar(50)  not null,
    created_date         timestamp,
    last_modified_by     varchar(50),
    last_modified_date   timestamp,
    activity_type_id     bigint,
    address              varchar(500),
    city_id              bigint,
    company_id           bigint       not null,
    deleted              boolean default false,
    description          varchar(500),
    main_branch          boolean default false,
    name                 varchar(255) not null,
    phone                varchar(255),
    reason_for_deletion  varchar(255),
    region_id            bigint,
    fiscal               boolean default false,
    registered           boolean default false,
    cto_id               bigint
        constraint branch_cto_idx
            unique,
    latitude             varchar(255),
    longitude            varchar(255),
    status               varchar(255),
    city_key_id          bigint,
    serial_number        varchar(255),
    cashbox_created_date timestamp,
    group_id             bigint,
    ofd_id               bigint,
    ofd_status           varchar(50),
    cadastre_file_id     bigint,
    rent_file_id         bigint,
    manager_id           bigint,
    apay_status          varchar(50),
    neighborhood_id      bigint
        constraint branch_neighborhood_fk
            references neighborhood
);

create index if not exists branch_company_idx
    on branch (company_id);

create unique index if not exists branch_cto_id_uindex
    on branch (cto_id);

create index if not exists branch_name_idx
    on branch (name);

create index if not exists branch_group_idx
    on branch (group_id);

create table if not exists category
(
    id                 bigint not null primary key,
    created_by         varchar(50),
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    name               text   not null,
    parent_id          bigint,
    company_type       varchar(50)
);

create table if not exists category_branch
(
    branch_id          bigint      not null
        constraint category_branch_branch_fk
            references branch,
    category_id        bigint      not null,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    enabled            boolean default true,
    primary key (branch_id, category_id)
);

create table if not exists data_entry_item
(
    id                 bigserial
        primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    branch_id          bigint
        constraint fk5uygj1w3dwyu2jmygiic1nvga
            references branch,
    data_entry_id      bigint
        constraint fkhjg0f6v92kdiit4c25b0aa3x0
            references data_entry,
    deleted            boolean default false,
    item_count         integer default 0,
    updater_id         bigint,
    service_type       varchar(255)
);

create index if not exists data_entry_item_idx
    on data_entry_item (data_entry_id);

create table if not exists income_of_product
(
    id                 bigint      not null
        primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    description        varchar(255),
    document_date      date,
    document_no        varchar(255),
    to_branch_id       bigint      not null,
    supplier_id        bigint,
    customer_id        bigint,
    income_date        timestamp,
    data_entry_item_id bigint
        constraint fkaiwba4rsoc3mb30c9hn3bxius
            references data_entry_item,
    data_entry_status  varchar(255),
    factura_id         varchar
        unique,
    status             varchar(25),
    act_id             bigint
);

create index if not exists income_of_product_branch_idx
    on income_of_product (to_branch_id);

create index if not exists income_of_product_document_idx
    on income_of_product (document_no);

create table if not exists terminal
(
    id                 bigint       not null
        primary key,
    created_by         varchar(50)  not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    branch_id          bigint
        constraint fkccka5ncsl3i2vr63n23y0epu6
            references branch,
    serial_number      varchar(255) not null,
    synced             boolean default false,
    status             varchar(50),
    transfer_id        bigint,
    model              varchar(50),
    fiscal_module_id   bigint,
    admin_branch_id    bigint
        constraint terminal_admin_branch_fk
            references branch,
    bill_id            bigint,
    customer_tin       varchar(14),
    customer_name      varchar(255),
    hash               varchar(512)
        unique,
    agreement_id       bigint,
    deleted            boolean default false
);

create unique index if not exists terminal_serial_idx
    on terminal (serial_number);

create table if not exists users
(
    id                 bigint                not null
        primary key,
    created_by         varchar(50)           not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    activated          boolean default false not null,
    activation_key     varchar(20),
    branch_id          bigint
        constraint fkixo09sv3j1j6hfox3cx6d2ggg
            references branch,
    company_id         bigint,
    deleted            boolean default false,
    first_name         varchar(50),
    lang_key           varchar(10),
    last_name          varchar(50),
    login              varchar(50)
        constraint uk_ow0gan20590jrb00upg3va2fn
            unique,
    password_hash      varchar(60)           not null,
    patronymic         varchar(50),
    reset_date         timestamp,
    reset_key          varchar(20),
    owner              boolean default false,
    telegram_id        bigint
        constraint uk_dus03vmwyluiy7k2p2gcrqbms
            unique,
    telegram_login     varchar(12)
        constraint uk_t6w1w11lqbsp7nggqcgbyrm21
            unique,
    telegram_name      varchar(255),
    refresh_date       timestamp,
    refresh_token      varchar(255),
    data_entry_item_id bigint
        constraint fk2nxsnyu1rmct2yyxccju77tqv
            references data_entry_item,
    data_entry_status  varchar(255),
    failed_attempts    integer,
    lock_time          timestamp,
    latest_terminal    varchar(50),
    image_id           bigint
        constraint users_image_id_fk
            references file_upload
);

create table if not exists telegram_account
(
    id                 bigint      not null
        primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    agreement_id       bigint,
    company_id         bigint
        constraint fk30o2m0nswg9j0rum8bgqd0w4a
            references company,
    confirmed          boolean,
    enabled            boolean,
    lang_key           varchar(255),
    name               varchar(255),
    phone              varchar(255),
    telegram_id        bigint,
    user_id            bigint
        constraint fkoeo7gdpb3q6bpvcv186s4rl44
            references users,
    agreement_name     varchar(255),
    data_entry_item_id bigint
        constraint fkt3hbnq1hp0dcn1sll2mwvwxg4
            references data_entry_item,
    data_entry_status  varchar(255)
);

create table if not exists user_authority
(
    user_id        bigint      not null
        constraint fkhi46vu7680y1hwvmnnuh4cybx
            references users,
    authority_name varchar(50) not null
        constraint fk6ktglpl5mjosa283rvken2py5
            references authority,
    primary key (user_id, authority_name)
);

create table if not exists user_permission
(
    id              bigserial
        primary key,
    has_access      boolean default false,
    permission_code varchar(255) not null
        constraint fk535himedu4s9sy2yg792ul4bg
            references permission,
    user_id         bigint       not null
        constraint fkn8ba4v3gvw1d82t3hofelr82t
            references users,
    constraint uke280b989p6vahsq4ilxfh1e5c
        unique (user_id, permission_code)
);

create table if not exists product_branch
(
    product_id         bigint                 not null,
    branch_id          bigint                 not null
        constraint fkexcwsjgcmm5e23mfok7qak1he
            references branch,
    created_by         varchar(50)            not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    unit_id            bigint,
    category_id        bigint,
    name               text,
    barcode            varchar(255),
    vat_barcode        varchar(255),
    sales_price        numeric(25, 4),
    vat_rate           numeric(19, 2),
    excise_amount      numeric,
    favourite          boolean  default false,
    has_mark           boolean  default false,
    has_excise         boolean  default false,
    service            boolean  default false,
    custom             boolean  default false,
    deleted            boolean  default false not null,
    city_id            bigint
        constraint product_branch_partition_city_id_fkey1
            references city,
    data_entry_item_id bigint
        constraint fkevnvca60fiwcertk6uphykptx
            references data_entry_item,
    data_entry_status  varchar(255),
    committent_tin     varchar(14),
    credit_enabled     boolean  default false,
    credit_settings    jsonb,
    advance_enabled    boolean  default false,
    advance_settings   jsonb,
    default_package    jsonb,
    child_product_id   bigint,
    origin             smallint default 2,
    constraint uk_po8kp1mv05qndbhm0xbb6wue2
        unique (product_id, branch_id, city_id)
)
    partition by LIST (city_id);

create table product_branch_1 partition of product_branch for values in (1);
create table product_branch_2 partition of product_branch for values in (2);
create table product_branch_3 partition of product_branch for values in (3);
create table product_branch_4 partition of product_branch for values in (4);
create table product_branch_5 partition of product_branch for values in (5);
create table product_branch_6 partition of product_branch for values in (6);
create table product_branch_7 partition of product_branch for values in (7);
create table product_branch_8 partition of product_branch for values in (8);
create table product_branch_9 partition of product_branch for values in (9);
create table product_branch_10 partition of product_branch for values in (10);
create table product_branch_11 partition of product_branch for values in (11);
create table product_branch_12 partition of product_branch for values in (12);
create table product_branch_13 partition of product_branch for values in (13);
create table product_branch_14 partition of product_branch for values in (14);
create table product_branch_15 partition of product_branch for values in (15);
create table product_branch_16 partition of product_branch for values in (16);
create table product_branch_17 partition of product_branch for values in (17);
create table product_branch_18 partition of product_branch for values in (18);
create table product_branch_19 partition of product_branch for values in (19);
create table product_branch_20 partition of product_branch for values in (20);
create table product_branch_21 partition of product_branch for values in (21);
create table product_branch_22 partition of product_branch for values in (22);
create table product_branch_23 partition of product_branch for values in (23);
create table product_branch_24 partition of product_branch for values in (24);
create table product_branch_25 partition of product_branch for values in (25);
create table product_branch_26 partition of product_branch for values in (26);
create table product_branch_27 partition of product_branch for values in (27);
create table product_branch_28 partition of product_branch for values in (28);
create table product_branch_29 partition of product_branch for values in (29);
create table product_branch_30 partition of product_branch for values in (30);
create table product_branch_31 partition of product_branch for values in (31);
create table product_branch_32 partition of product_branch for values in (32);
create table product_branch_33 partition of product_branch for values in (33);
create table product_branch_34 partition of product_branch for values in (34);
create table product_branch_35 partition of product_branch for values in (35);
create table product_branch_36 partition of product_branch for values in (36);
create table product_branch_37 partition of product_branch for values in (37);
create table product_branch_38 partition of product_branch for values in (38);
create table product_branch_39 partition of product_branch for values in (39);
create table product_branch_40 partition of product_branch for values in (40);
create table product_branch_41 partition of product_branch for values in (41);
create table product_branch_42 partition of product_branch for values in (42);
create table product_branch_43 partition of product_branch for values in (43);
create table product_branch_44 partition of product_branch for values in (44);
create table product_branch_45 partition of product_branch for values in (45);
create table product_branch_46 partition of product_branch for values in (46);
create table product_branch_47 partition of product_branch for values in (47);
create table product_branch_48 partition of product_branch for values in (48);
create table product_branch_49 partition of product_branch for values in (49);
create table product_branch_50 partition of product_branch for values in (50);
create table product_branch_51 partition of product_branch for values in (51);
create table product_branch_52 partition of product_branch for values in (52);
create table product_branch_53 partition of product_branch for values in (53);
create table product_branch_54 partition of product_branch for values in (54);
create table product_branch_55 partition of product_branch for values in (55);
create table product_branch_56 partition of product_branch for values in (56);
create table product_branch_57 partition of product_branch for values in (57);
create table product_branch_58 partition of product_branch for values in (58);
create table product_branch_59 partition of product_branch for values in (59);
create table product_branch_60 partition of product_branch for values in (60);
create table product_branch_61 partition of product_branch for values in (61);
create table product_branch_62 partition of product_branch for values in (62);
create table product_branch_63 partition of product_branch for values in (63);
create table product_branch_64 partition of product_branch for values in (64);
create table product_branch_65 partition of product_branch for values in (65);
create table product_branch_66 partition of product_branch for values in (66);
create table product_branch_67 partition of product_branch for values in (67);
create table product_branch_68 partition of product_branch for values in (68);
create table product_branch_69 partition of product_branch for values in (69);
create table product_branch_70 partition of product_branch for values in (70);
create table product_branch_71 partition of product_branch for values in (71);
create table product_branch_72 partition of product_branch for values in (72);
create table product_branch_73 partition of product_branch for values in (73);
create table product_branch_74 partition of product_branch for values in (74);
create table product_branch_75 partition of product_branch for values in (75);
create table product_branch_76 partition of product_branch for values in (76);
create table product_branch_77 partition of product_branch for values in (77);
create table product_branch_78 partition of product_branch for values in (78);
create table product_branch_79 partition of product_branch for values in (79);
create table product_branch_80 partition of product_branch for values in (80);
create table product_branch_81 partition of product_branch for values in (81);
create table product_branch_82 partition of product_branch for values in (82);
create table product_branch_83 partition of product_branch for values in (83);
create table product_branch_84 partition of product_branch for values in (84);
create table product_branch_85 partition of product_branch for values in (85);
create table product_branch_86 partition of product_branch for values in (86);
create table product_branch_87 partition of product_branch for values in (87);
create table product_branch_88 partition of product_branch for values in (88);
create table product_branch_89 partition of product_branch for values in (89);
create table product_branch_90 partition of product_branch for values in (90);
create table product_branch_91 partition of product_branch for values in (91);
create table product_branch_92 partition of product_branch for values in (92);
create table product_branch_93 partition of product_branch for values in (93);
create table product_branch_94 partition of product_branch for values in (94);
create table product_branch_95 partition of product_branch for values in (95);
create table product_branch_96 partition of product_branch for values in (96);
create table product_branch_97 partition of product_branch for values in (97);
create table product_branch_98 partition of product_branch for values in (98);
create table product_branch_99 partition of product_branch for values in (99);
create table product_branch_100 partition of product_branch for values in (100);
create table product_branch_101 partition of product_branch for values in (101);
create table product_branch_102 partition of product_branch for values in (102);
create table product_branch_103 partition of product_branch for values in (103);
create table product_branch_104 partition of product_branch for values in (104);
create table product_branch_105 partition of product_branch for values in (105);
create table product_branch_106 partition of product_branch for values in (106);
create table product_branch_107 partition of product_branch for values in (107);
create table product_branch_108 partition of product_branch for values in (108);
create table product_branch_109 partition of product_branch for values in (109);
create table product_branch_110 partition of product_branch for values in (110);
create table product_branch_111 partition of product_branch for values in (111);
create table product_branch_112 partition of product_branch for values in (112);
create table product_branch_113 partition of product_branch for values in (113);
create table product_branch_114 partition of product_branch for values in (114);
create table product_branch_115 partition of product_branch for values in (115);
create table product_branch_116 partition of product_branch for values in (116);
create table product_branch_117 partition of product_branch for values in (117);
create table product_branch_118 partition of product_branch for values in (118);
create table product_branch_119 partition of product_branch for values in (119);
create table product_branch_120 partition of product_branch for values in (120);
create table product_branch_121 partition of product_branch for values in (121);
create table product_branch_122 partition of product_branch for values in (122);
create table product_branch_123 partition of product_branch for values in (123);
create table product_branch_124 partition of product_branch for values in (124);
create table product_branch_125 partition of product_branch for values in (125);
create table product_branch_126 partition of product_branch for values in (126);
create table product_branch_127 partition of product_branch for values in (127);
create table product_branch_128 partition of product_branch for values in (128);
create table product_branch_129 partition of product_branch for values in (129);
create table product_branch_130 partition of product_branch for values in (130);
create table product_branch_131 partition of product_branch for values in (131);
create table product_branch_132 partition of product_branch for values in (132);
create table product_branch_133 partition of product_branch for values in (133);
create table product_branch_134 partition of product_branch for values in (134);
create table product_branch_135 partition of product_branch for values in (135);
create table product_branch_136 partition of product_branch for values in (136);
create table product_branch_137 partition of product_branch for values in (137);
create table product_branch_138 partition of product_branch for values in (138);
create table product_branch_139 partition of product_branch for values in (139);
create table product_branch_140 partition of product_branch for values in (140);
create table product_branch_141 partition of product_branch for values in (141);
create table product_branch_142 partition of product_branch for values in (142);
create table product_branch_143 partition of product_branch for values in (143);
create table product_branch_144 partition of product_branch for values in (144);
create table product_branch_145 partition of product_branch for values in (145);
create table product_branch_146 partition of product_branch for values in (146);
create table product_branch_147 partition of product_branch for values in (147);
create table product_branch_148 partition of product_branch for values in (148);
create table product_branch_149 partition of product_branch for values in (149);
create table product_branch_150 partition of product_branch for values in (150);
create table product_branch_151 partition of product_branch for values in (151);
create table product_branch_152 partition of product_branch for values in (152);
create table product_branch_153 partition of product_branch for values in (153);
create table product_branch_154 partition of product_branch for values in (154);
create table product_branch_155 partition of product_branch for values in (155);
create table product_branch_156 partition of product_branch for values in (156);
create table product_branch_157 partition of product_branch for values in (157);
create table product_branch_158 partition of product_branch for values in (158);
create table product_branch_159 partition of product_branch for values in (159);
create table product_branch_160 partition of product_branch for values in (160);
create table product_branch_161 partition of product_branch for values in (161);
create table product_branch_162 partition of product_branch for values in (162);
create table product_branch_163 partition of product_branch for values in (163);
create table product_branch_164 partition of product_branch for values in (164);
create table product_branch_165 partition of product_branch for values in (165);
create table product_branch_166 partition of product_branch for values in (166);
create table product_branch_167 partition of product_branch for values in (167);
create table product_branch_168 partition of product_branch for values in (168);
create table product_branch_169 partition of product_branch for values in (169);
create table product_branch_170 partition of product_branch for values in (170);
create table product_branch_171 partition of product_branch for values in (171);
create table product_branch_172 partition of product_branch for values in (172);
create table product_branch_173 partition of product_branch for values in (173);
create table product_branch_174 partition of product_branch for values in (174);
create table product_branch_175 partition of product_branch for values in (175);
create table product_branch_176 partition of product_branch for values in (176);
create table product_branch_177 partition of product_branch for values in (177);
create table product_branch_178 partition of product_branch for values in (178);
create table product_branch_179 partition of product_branch for values in (179);
create table product_branch_180 partition of product_branch for values in (180);
create table product_branch_181 partition of product_branch for values in (181);
create table product_branch_182 partition of product_branch for values in (182);
create table product_branch_183 partition of product_branch for values in (183);
create table product_branch_184 partition of product_branch for values in (184);
create table product_branch_185 partition of product_branch for values in (185);
create table product_branch_186 partition of product_branch for values in (186);
create table product_branch_187 partition of product_branch for values in (187);
create table product_branch_188 partition of product_branch for values in (188);
create table product_branch_189 partition of product_branch for values in (189);
create table product_branch_190 partition of product_branch for values in (190);
create table product_branch_191 partition of product_branch for values in (191);
create table product_branch_192 partition of product_branch for values in (192);
create table product_branch_193 partition of product_branch for values in (193);
create table product_branch_194 partition of product_branch for values in (194);
create table product_branch_195 partition of product_branch for values in (195);
create table product_branch_196 partition of product_branch for values in (196);
create table product_branch_197 partition of product_branch for values in (197);
create table product_branch_198 partition of product_branch for values in (198);
create table product_branch_199 partition of product_branch for values in (199);
create table product_branch_200 partition of product_branch for values in (200);
create table product_branch_201 partition of product_branch for values in (201);
create table product_branch_202 partition of product_branch for values in (202);
create table product_branch_203 partition of product_branch for values in (203);
create table product_branch_204 partition of product_branch for values in (204);
create table product_branch_205 partition of product_branch for values in (205);
create table product_branch_206 partition of product_branch for values in (206);
create table product_branch_207 partition of product_branch for values in (207);
create table product_branch_208 partition of product_branch for values in (208);
create table product_branch_209 partition of product_branch for values in (209);
create table product_branch_210 partition of product_branch for values in (210);

create table if not exists income_of_product_detail
(
    id                   bigint not null
        primary key,
    cost_price           numeric(19, 2),
    income_of_product_id bigint not null
        constraint fk9y0fu4n8n0xviqwd66nfibri1
            references income_of_product,
    markup               numeric(19, 2),
    product_id           bigint not null,
    qty                  numeric(19, 2),
    sales_price          numeric(19, 2),
    unit_id              bigint,
    markings             text,
    branch_id            bigint,
    city_id              bigint
);

create index if not exists income_of_product_detail_income_of_product_idx
    on income_of_product_detail (income_of_product_id);

create index if not exists income_of_product_detail_product_idx
    on income_of_product_detail (product_id);

create table if not exists product_unit
(
    id                 bigserial
        primary key,
    base               boolean default false,
    branch_id          bigint not null,
    coefficient        double precision,
    count              integer,
    product_id         bigint not null,
    sorder             integer,
    unit_id            bigint not null,
    city_id            bigint,
    created_by         varchar(50),
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp
);

create index if not exists product_unit_product_idx
    on product_unit (product_id);

create index if not exists product_unit_unit_idx
    on product_unit (unit_id);

create index if not exists pu_product_branch_idx
    on product_unit (product_id, branch_id);
--
-- create table if not exists promotion_bonuses
-- (
--     bonus_on      varchar(255)   not null,
--     sort_index    integer        not null,
--     unit_id       bigint,
--     product_count integer,
--     bonus_amount  numeric(25, 4) not null,
--     bonus_type    varchar(255)   not null,
--     all_products  boolean default false,
--     promotion_id  bigint         not null,
--     branch_id     bigint,
--     product_id    bigint,
--     city_id       bigint,
--     unique (sort_index, promotion_id)
-- );
-- --
-- create table if not exists promotion_conditions
-- (
--     promotion_on          varchar(255)     not null,
--     unit_id               bigint           not null,
--     condition_amount_from double precision not null,
--     sort_index            integer          not null,
--     condition_amount_to   double precision,
--     promotion_id          bigint           not null,
--     branch_id             bigint,
--     product_id            bigint,
--     city_id               bigint,
--     unique (sort_index, promotion_id)
-- );

create table if not exists fiscal_module
(
    id                 bigserial
        primary key,
    serial_number      varchar(255) not null
        unique,
    branch_id          bigint,
    status             varchar(50),
    created_by         varchar(50),
    created_date       timestamp default now(),
    last_modified_by   varchar(50),
    last_modified_date timestamp default now(),
    transfer_id        bigint,
    terminal_id        bigint,
    admin_branch_id    bigint
        constraint fiscal_module_admin_branch_fk
            references branch,
    bill_id            bigint
);

create table if not exists employee_bonus
(
    id                 bigserial
        primary key,
    employee_id        bigint  not null
        constraint employee_bonus_employee_fk
            references users,
    company_id         bigint
        constraint employee_bonus_company_fk
            references company,
    bonus_id           bigint
        constraint employee_bonus_bonus_fk
            references bonus,
    amount             numeric not null,
    created_by         varchar(255),
    created_date       timestamp,
    last_modified_by   varchar(255),
    last_modified_date timestamp,
    terminal_id        bigint
);

create table if not exists inventory_item
(
    id   bigserial
        constraint inventory_item_pk
            primary key,
    code varchar(255) not null
        constraint inventory_item_uk
            unique,
    name varchar(255) not null
);

create table if not exists inventory
(
    id                bigserial
        constraint inventory_pk
            primary key,
    inventory_item_id bigint         not null
        constraint inventory_item_fk
            references inventory_item,
    status            varchar(50)    not null,
    branch_id         bigint
        constraint inventory_branch_fk
            references branch,
    amount            numeric(25, 4) not null
);

create table if not exists basket_item
(
    id                  bigserial
        constraint basket_item_pk
            primary key,
    last_modified_date  timestamp default now(),
    supplier_product_id bigint         not null,
    company_id          bigint         not null
        constraint basket_item_company_fk
            references company,
    supply_tin          varchar(50)    not null,
    supply_name         varchar(500)   not null,
    supply_branch_id    bigint,
    warehouse_id        bigint,
    name                varchar(500)   not null,
    barcode             varchar(50),
    vat_barcode         varchar(50)    not null,
    unit_code           bigint         not null,
    qty                 numeric(25, 4) not null,
    price               numeric(25, 4) not null,
    vat_rate            numeric(25, 4),
    supply_id           bigint         not null,
    photos              jsonb          not null,
    unit_id             bigint         not null,
    unit_name           varchar(50)    not null,
    selected            boolean   default true,
    constraint basket_item_company_product_uk
        unique (supplier_product_id, company_id)
);

create table if not exists orders
(
    id                 bigint         not null
        constraint order_pk
            primary key,
    created_by         varchar(255),
    created_date       timestamp,
    last_modified_by   varchar(255),
    last_modified_date timestamp,
    order_number       varchar(255),
    order_date         timestamp default now(),
    contract_id        bigint,
    customer_id        bigint         not null,
    supplier_id        bigint         not null,
    supplier_tin       varchar(50)    not null,
    supplier_name      varchar(255)   not null,
    branch_id          bigint,
    branch_name        varchar(255),
    warehouse_id       bigint,
    status             varchar(50)    not null,
    sale_type          varchar(50),
    payment_type       varchar(50),
    description        text,
    delivery_branch_id bigint
        constraint orders_delivery_branch_fk
            references branch,
    delivery_type      varchar(255),
    street             varchar(255),
    house              varchar(255),
    apartment          varchar(255),
    longitude          varchar(255),
    latitude           varchar(255),
    region_id          bigint,
    region_name        varchar(255),
    district_id        bigint,
    district_name      varchar(255),
    total_price        numeric(25, 2) not null,
    region_code        bigint,
    district_code      bigint,
    delivery_date      timestamp
);

create table if not exists order_product
(
    id                 bigint         not null,
    created_by         varchar(255),
    created_date       timestamp,
    last_modified_by   varchar(255),
    last_modified_date timestamp,
    order_product_id   bigint         not null
        constraint order_product_pk
            primary key,
    name               varchar(500)   not null,
    barcode            varchar(255),
    vat_barcode        varchar(255)   not null,
    has_mark           boolean default false,
    unit_id            bigint         not null,
    unit_name          varchar(255)   not null,
    unit_code          varchar(255),
    qty                numeric(25, 2) not null,
    price              numeric(25, 2) not null,
    vat_rate           numeric(25, 2),
    order_id           bigint         not null
        constraint order_product_order_fk
            references orders,
    photos             jsonb
);

create table if not exists terminal_log
(
    id            bigserial
        constraint terminal_log_pk
            primary key,
    serial_number varchar(255),
    db_file_id    bigint
);

create table if not exists shipment
(
    id                 bigserial
        primary key,
    created_by         varchar(50)  not null,
    created_date       timestamp default now(),
    last_modified_by   varchar(50),
    last_modified_date timestamp default now(),
    number             varchar(255) not null,
    int_number         integer,
    shipment_date      timestamp,
    status             varchar(255) not null,
    company_id         bigint       not null,
    customer_tin       varchar(25)  not null,
    customer_name      varchar(500) not null,
    description        varchar(1024),
    invoice_id         bigint,
    unique (company_id, number, customer_tin)
);

create table if not exists shipment_item
(
    id                 bigserial
        primary key,
    created_by         varchar(50)    not null,
    created_date       timestamp      default now(),
    last_modified_by   varchar(50),
    last_modified_date timestamp      default now(),
    shipment_id        bigint         not null,
    product_id         bigint         not null,
    branch_id          bigint         not null,
    city_id            bigint         not null,
    qty                numeric(25, 2) default 0,
    vat_barcode        varchar(50)    not null,
    barcode            varchar(50),
    name               varchar(512)   not null,
    package_code       varchar(255),
    package_name       varchar(255),
    price              numeric(25, 4) not null,
    excise             numeric(25, 4),
    vat_rate           numeric(25, 2),
    origin             varchar(255),
    unit_id            bigint         not null
        constraint shipment_item_unit_fk
            references unit
);

create table if not exists shipment_marking
(
    shipment_item_id bigint       not null,
    marking          varchar(255) not null,
    primary key (shipment_item_id, marking)
);

create table if not exists shipment_aggregation
(
    shipment_item_id bigint       not null,
    aggregation      varchar(255) not null,
    primary key (shipment_item_id, aggregation)
);

create table if not exists one_c_company
(
    id            bigint       not null
        primary key,
    tin           varchar(50)  not null,
    name          varchar(256) not null,
    first_time    timestamp,
    last_time     timestamp default now(),
    receipt_count bigint    default 0
);

create table if not exists telegram_branches
(
    telegram_id bigint not null
        references telegram_account,
    branch_id   bigint not null
        references branch,
    unique (telegram_id, branch_id)
);

create table if not exists marking_history
(
    id                 bigserial
        primary key,
    created_by         varchar(255)            not null,
    created_date       timestamp default now() not null,
    last_modified_by   varchar(255)            not null,
    last_modified_date timestamp default now() not null,
    document_type      varchar(50),
    reason             varchar(50),
    document           text,
    company_id         bigint                  not null,
    branch_id          bigint,
    status             varchar(255),
    document_date      date
);

create table if not exists company_marks
(
    company_id bigint       not null
        references company,
    marking    varchar(255) not null,
    status     varchar(255),
    error      text,
    primary key (company_id, marking)
);

create table if not exists marking_history_item
(
    id                 bigserial
        primary key,
    clear_code         varchar(255) not null
        unique,
    original_code      varchar(255) not null
        unique,
    children           jsonb,
    status             varchar(50),
    marking_history_id bigint
);

create table if not exists company_bill
(
    id                 bigserial
        primary key,
    created_by         varchar(50),
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    company_id         bigint,
    operator_id        bigint  not null,
    tin                varchar(50),
    name               varchar(255),
    qty                integer not null
);

create table if not exists company_agreement
(
    id                 bigserial
        primary key,
    created_by         varchar(50),
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    company_id         bigint not null,
    operator_id        bigint not null,
    company_bill_id    bigint,
    branch_id          bigint not null,
    file_id            bigint
);


create table if not exists usage_plan
(
    id          bigserial
        primary key,
    company_id  bigint,
    tariff      varchar(50),
    start_date  timestamp,
    end_date    timestamp,
    operator_id bigint
);

create table if not exists eps_services
(
    id              uuid         not null
        primary key,
    name            varchar(255) not null,
    type            varchar      not null,
    status          varchar(255) not null,
    logo_id         varchar(255),
    logo_url        text,
    created_by      varchar(50),
    created_date    timestamp,
    updated_by      varchar(50),
    updated_date    timestamp,
    file_id         bigint,
    from_apay       boolean,
    default_service boolean default false,
    position        smallint,
    qr_method       varchar(255)
);

create table if not exists card_type
(
    prefix varchar(255) not null
        primary key
);

create table if not exists act
(
    id                         bigserial
        primary key,
    company_id                 bigint not null,
    status                     varchar(255),
    acceptance_transfer_act_id varchar(255),
    seller_pinfl               varchar(255),
    buyer_tin_or_pinfl         varchar(255),
    total_price                numeric(25, 4),
    seller_name                varchar(255),
    seller_address             varchar(255),
    seller_account             varchar(255),
    seller_bank_id             varchar(255),
    buyer_name                 varchar(255),
    buyer_address              varchar(255),
    buyer_account              varchar(255),
    buyer_bank_id              varchar(255),
    buyer_vat_reg_code         varchar(255),
    buyer_vat_reg_status       integer,
    buyer_branch_code          varchar(255),
    buyer_branch_name          varchar(255),
    act_no                     varchar(255),
    act_date                   varchar(255),
    contract_no                varchar(255),
    contract_date              varchar(255)
);

create table if not exists act_product
(
    id           bigserial
        primary key,
    act_id       bigint not null,
    ord_no       integer,
    name         text,
    catalog_code varchar(255),
    catalog_name text,
    barcode      varchar(255),
    package_code varchar(255),
    package_name varchar(255),
    count        numeric(25, 4),
    price        numeric(25, 4),
    summa        numeric(25, 4),
    delivery_sum numeric(25, 4),
    serial       varchar(255),
    marks        jsonb,
    aggregations jsonb,
    blocks       jsonb
);

create table if not exists balance_history
(
    id               bigint not null
        constraint balance_history_pkey
            primary key,
    back             numeric(19, 2),
    balance_date     date,
    balance_end      numeric(19, 2),
    balance_start    numeric(19, 2),
    branch_id        bigint not null,
    income           numeric(19, 2),
    product_id       bigint not null,
    sale             numeric(19, 2),
    stock_adjustment numeric(19, 2),
    transfer_in      numeric(19, 2),
    transfer_out     numeric(19, 2),
    city_id          bigint,
    constraint balance_history_branch_id_product_id_city_id_balance_da_key
        unique (branch_id, product_id, city_id, balance_date)
);

create table user_access_company
(
    id bigint not null primary key
);

insert into unit (id, code, countable, description, name, name_uz)
values (14, 100013, false, 'пг. м.', 'пг. м.', 'pg. m.'),
       (3, 100002, true, 'килограмм', 'кг', 'kg'),
       (6, 100005, false, 'коробка', 'кор', 'kor'),
       (1, 100000, true, 'литр', 'л', 'l'),
       (10, 100009, true, 'куб', 'куб', 'kub'),
       (19, 100018, false, 'ампула', 'амп', 'amp'),
       (15, 100014, true, 'часы', 'час', 'soat'),
       (9, 100008, true, 'проценты', '%', '%'),
       (21, 100020, false, 'флакон', 'фл', 'fl'),
       (20, 100019, false, 'тюбик', 'тюб', 'tyub'),
       (7, 100006, false, 'упаковка', 'уп', 'up'),
       (27, 100026, false, 'мешок', 'мешок', 'qop'),
       (31, 100031, false, 'комплект / набор', 'комплект / набор', 'komplekt / nabor'),
       (30, 100029, true, 'сум', 'сум', 'so''m'),
       (5, 100004, true, 'миллилитр', 'мл', 'ml'),
       (8, 100007, false, 'блок', 'бл', 'bl'),
       (29, 100028, true, 'порция', 'порция', 'portsiya'),
       (33, 100033, false, 'пара', 'пара', 'juft'),
       (32, 100032, true, 'банка', 'банка', 'banka'),
       (16, 100015, true, 'тонна', 'тон', 'ton'),
       (22, 100021, false, 'пластиковые банки / бутылки', 'пб', 'pb'),
       (23, 100022, false, 'комплект', 'компл', 'kompl'),
       (2, 100001, false, 'штук', 'шт', 'sht'),
       (35, 101111, false, 'человек', 'чел', 'odam'),
       (26, 100025, true, 'куб.м', 'куб.м', 'куб.м'),
       (28, 100027, false, 'пачка', 'пачка', 'pachka'),
       (36, 100036, true, 'км', 'км ', 'km'),
       (13, 100012, true, 'сотих', 'сотих', 'sotix'),
       (11, 100010, true, 'гектар', 'га', 'ga'),
       (12, 100011, true, 'кв. м.', 'кв. м.', 'кв. м.'),
       (4, 100003, true, 'грамм', 'гр', 'gr'),
       (18, 100017, true, 'сутка', 'сут', 'сут'),
       (17, 100016, true, 'кВа', 'кВа', 'kVa'),
       (25, 100024, true, 'метр', 'м', 'м'),
       (24, 100023, true, 'фут', 'фут', 'фут')
on conflict do nothing;

insert into public.authority (name)
values ('ROLE_USER'),
       ('ROLE_ADMIN'),
       ('ROLE_OWNER'),
       ('ROLE_BRANCH_ADMIN'),
       ('ROLE_CASHIER'),
       ('ROLE_SMARTPOS_ADMIN'),
       ('ROLE_SUPPORT'),
       ('ROLE_SALES_DIRECTOR'),
       ('ROLE_SALES_MANAGER'),
       ('ROLE_REGION_MANAGER'),
       ('ROLE_SUPPORT_MANAGER')
on conflict do nothing;

insert into public.permission (code)
values ('STA_CHEQUE'),
       ('STA_CHEQUE_LIST'),
       ('STA_CHEQUE_LIST_FILT_DATE'),
       ('STA_CHEQUE_LIST_FILT_SEARCH'),
       ('STA_CHEQUE_LIST_FILT_COMPANY'),
       ('STA_CHEQUE_LIST_FILT_BRANCH'),
       ('STA_CHEQUE_LIST_FILT_EMPLOYEE'),
       ('STA_CHEQUE_LIST_FILT_CHEQUE_TYPE'),
       ('STA_CHEQUE_LIST_FILT_PRICE'),
       ('STA_CHEQUE_DETAILS'),
       ('STA_CHEQUE_BY_BRANCH'),
       ('STA_CHEQUE_BY_BRANCH_LIST'),
       ('STA_CHEQUE_BY_BRANCH_LIST_STAT_BRANCH'),
       ('STA_CHEQUE_BY_BRANCH_LIST_STAT_CHEQUE'),
       ('STA_CHEQUE_BY_BRANCH_LIST_STAT_REVENUE'),
       ('STA_CHEQUE_BY_BRANCH_LIST_FILT_DATE'),
       ('STA_CHEQUE_BY_BRANCH_LIST_FILT_SEARCH'),
       ('STA_CHEQUE_BY_BRANCH_LIST_FILT_COMPANY'),
       ('STA_CHEQUE_BY_BRANCH_LIST_FILT_REGION'),
       ('STA_CHEQUE_BY_BRANCH_LIST_FILT_CITY'),
       ('STA_CHEQUE_BY_BRANCH_LIST_FILT_ACTIVITY'),
       ('STA_CHEQUE_BY_COMPANY'),
       ('STA_CHEQUE_BY_COMPANY_LIST'),
       ('STA_CHEQUE_BY_COMPANY_LIST_STAT_BRANCH'),
       ('STA_CHEQUE_BY_COMPANY_LIST_STAT_CHEQUE'),
       ('STA_CHEQUE_BY_COMPANY_LIST_STAT_REVENUE'),
       ('STA_CHEQUE_BY_COMPANY_LIST_FILT_DATE'),
       ('STA_CHEQUE_BY_COMPANY_LIST_FILT_SEARCH'),
       ('STA_CHEQUE_BY_COMPANY_LIST_FILT_REGION'),
       ('STA_CHEQUE_BY_COMPANY_LIST_FILT_CITY'),
       ('MON_COMPANY'),
       ('MON_COMPANY_LIST'),
       ('MON_COMPANY_LIST_FILT_DATE'),
       ('MON_COMPANY_LIST_FILT_SEARCH'),
       ('MON_COMPANY_LIST_FILT_ACTIVITY'),
       ('MON_COMPANY_LIST_FILT_BUSINESS'),
       ('MON_COMPANY_DETAILS'),
       ('MON_COMPANY_DETAILS_BRANCHES'),
       ('MON_COMPANY_DETAILS_KKMS'),
       ('MON_COMPANY_DETAILS_USERS'),
       ('MON_COMPANY_DETAILS_LOGS'),
       ('MON_COMPANY_DETAILS_CATALOG'),
       ('MON_COMPANY_DETAILS_CHEQUES'),
       ('MON_COMPANY_DETAILS_STOCK'),
       ('MON_COMPANY_DETAILS_STOCK_BY_DAY'),
       ('MON_COMPANY_DETAILS_INCOMES'),
       ('MON_COMPANY_DETAILS_INCOMES_ADD'),
       ('MON_COMPANY_DETAILS_PROMOTION'),
       ('MON_COMPANY_DETAILS_PROMOTION_LIST'),
       ('MON_COMPANY_DETAILS_PROMOTION_ADD'),
       ('MON_COMPANY_DETAILS_PROMOTION_DETAILS'),
       ('MON_EMPLOYEE'),
       ('MON_EMPLOYEE_LIST'),
       ('MON_EMPLOYEE_LIST_FILT_SEARCH'),
       ('MON_EMPLOYEE_LIST_FILT_COMPANY'),
       ('MON_EMPLOYEE_LIST_FILT_BRANCH'),
       ('MON_EMPLOYEE_EDIT'),
       ('MON_EMPLOYEE_UPDATE_LOGIN'),
       ('MON_EMPLOYEE_DELETE_TOKEN'),
       ('MON_BRANCH'),
       ('MON_BRANCH_LIST'),
       ('MON_BRANCH_LIST_FILT_DATE'),
       ('MON_BRANCH_LIST_FILT_COMPANY'),
       ('MON_BRANCH_LIST_FILT_SEARCH'),
       ('MON_BRANCH_DETAILS'),
       ('MON_KKM'),
       ('MON_KKM_LIST'),
       ('MON_KKM_LIST_FILT_SEARCH'),
       ('MON_KKM_LIST_FILT_COMPANY'),
       ('MON_KKM_LIST_FILT_BRANCH'),
       ('MON_KKM_SYNC'),
       ('MON_KKM_DELETE'),
       ('MON_LOG'),
       ('MON_LOG_LIST'),
       ('MON_PRODUCT'),
       ('MON_PRODUCT_LIST'),
       ('MON_PRODUCT_LIST_FILT_DATE'),
       ('MON_PRODUCT_LIST_FILT_REGION'),
       ('MON_PRODUCT_LIST_FILT_CITY'),
       ('MON_PRODUCT_LIST_FILT_SEARCH'),
       ('MAN_USER'),
       ('MAN_USER_LIST'),
       ('MAN_USER_LIST_FILT_SEARCH'),
       ('MAN_USER_ADD'),
       ('MAN_USER_EDIT'),
       ('MAN_EXCISE'),
       ('MAN_EXCISE_LIST'),
       ('MAN_EXCISE_LIST_FILT_SEARCH'),
       ('MAN_EXCISE_ADD'),
       ('MAN_EXCISE_EDIT'),
       ('MAN_VAT'),
       ('MAN_VAT_LIST'),
       ('MAN_VAT_ADD'),
       ('MAN_VAT_EDIT'),
       ('MAN_VAT_DELETE'),
       ('MAN_UNIT'),
       ('MAN_UNIT_LIST'),
       ('MAN_SINGLE_CAT'),
       ('MAN_SINGLE_CAT_UPLOAD'),
       ('MAN_SINGLE_CAT_DOWNLOAD'),
       ('STA_CHEQUE_BY_BRANCH_LIST_STAT'),
       ('STA_CHEQUE_BY_COMPANY_LIST_STAT'),
       ('MON_COMPANY_LIST_FILT'),
       ('MON_EMPLOYEE_LIST_FILT'),
       ('MON_BRANCH_LIST_FILT'),
       ('MON_KKM_LIST_FILT'),
       ('MON_PRODUCT_LIST_FILT'),
       ('MAN_USER_LIST_FILT'),
       ('MAN_EXCISE_LIST_FILT'),
       ('STA_CHEQUE_LIST_FILT'),
       ('STA_CHEQUE_BY_BRANCH_LIST_FILT'),
       ('STA_CHEQUE_BY_COMPANY_LIST_FILT'),
       ('MON_COMPANY_DETAILS_STAT'),
       ('MON_COMPANY_DETAILS_STAT_CHEQUES'),
       ('MON_COMPANY_DETAILS_STAT_BRANCHES'),
       ('MON_COMPANY_DETAILS_STAT_KKM'),
       ('MON_COMPANY_DETAILS_STAT_REVENUE'),
       ('MON_COMPANY_DETAILS_STAT_PRODUCTS'),
       ('MON_COMPANY_DETAILS_STAT_TOTAL_COST_PRICE'),
       ('MON_COMPANY_DETAILS_STAT_TOTAL_SALES_PRICE'),
       ('MON_TG_USERS'),
       ('MON_TG_USERS_LIST'),
       ('MON_TG_USERS_LIST_FILT'),
       ('MON_TG_USERS_LIST_FILT_COMPANY'),
       ('MON_TG_USERS_LIST_FILT_BRANCH'),
       ('MON_TG_USERS_LIST_FILT_SEARCH'),
       ('STA_CHEQUE_LIST_STAT'),
       ('STA_CHEQUE_LIST_STAT_RECEIPTS'),
       ('STA_CHEQUE_LIST_STAT_NDS'),
       ('STA_CHEQUE_LIST_STAT_POSITIONS_IN_RECEIPTS'),
       ('STA_CHEQUE_LIST_STAT_REVENUE'),
       ('STA_CHEQUE_LIST_STAT_CASH'),
       ('STA_CHEQUE_LIST_STAT_CARD'),
       ('STA_CHEQUE_LIST_STAT_CARD_UZCARD'),
       ('STA_CHEQUE_LIST_STAT_CARD_HUMO'),
       ('STA_CHEQUE_LIST_STAT_CARD_LOYALTY'),
       ('STA_CHEQUE_LIST_STAT_CARD_OTHER'),
       ('STA_CHEQUE_LIST_STAT_DISCOUNT'),
       ('sadfasdf'),
       ('sdfgsdfg'),
       ('dsfgsdfg'),
       ('sdfgdsfg'),
       ('321321_EDIT'),
       ('2mas'),
       ('MON_MENU'),
       ('MON_COMPANY_DETAILS_FREE_TRIAL'),
       ('MON_COMPANY_DETAILS_BRANCHES_DELETE_WEBKASS'),
       ('MAIN_MENU'),
       ('STA_MENU'),
       ('MON_AGREEMENTS'),
       ('MAN_MENU'),
       ('MAN_MENU_TEST'),
       ('MON_APPLICATIONS'),
       ('MON_APPLICATIONS_LIST'),
       ('MON_APPLICATIONS_LIST_FILTER'),
       ('MON_APPLICATIONS_LIST_FILTER_SEARCH'),
       ('MON_APPLICATIONS_LIST_FILTER_STATUS'),
       ('MON_AGREEMENTS_LIST'),
       ('MON_AGREEMENTS_LIST_FILTER'),
       ('MON_AGREEMENTS_LIST_FILTER_COMPANY'),
       ('MON_AGREEMENTS_LIST_FILTER_SERVICE'),
       ('MON_AGREEMENTS_LIST_FILTER_STATUS'),
       ('MON_AGREEMENTS_LIST_FILTER_SEARCH'),
       ('MON_AGREEMENTS_DELETE'),
       ('MON_APPLICATIONS_ACCEPT'),
       ('MON_APPLICATIONS_DELETE'),
       ('MON_1C_COMPANIES'),
       ('MON_1C_COMPANIES_LIST'),
       ('MON_1C_COMPANIES_FILTER'),
       ('MAN_COMPANY'),
       ('MAN_BRANCHES'),
       ('MAN_EMPLOYEES'),
       ('MAN_BONUSES'),
       ('MAN_COMPANY_INFO'),
       ('MAN_COMPANY_BRANCHES'),
       ('MAN_COMPANY_EMPLOYEES'),
       ('MAN_COMPANY_CLIENTS'),
       ('MAN_COMPANY_WAREHOUSE'),
       ('MAN_COMPANY_WAREHOUSE_STOCK'),
       ('MAN_COMPANY_WAREHOUSE_STATS'),
       ('MAN_COMPANY_WAREHOUSE_KKM'),
       ('MAN_COMPANY_WAREHOUSE_FM'),
       ('MAN_COMPANY_WAREHOUSE_TRANSFERS'),
       ('MAN_COMPANY_APPLICATION'),
       ('MAN_COMPANY_MODELS_KKM'),
       ('MAN_BONUSES_ADD'),
       ('MON_BILLING'),
       ('MON_BILLING_INVOICES'),
       ('MON_BILLING_TRANSACTIONS'),
       ('MON_BILLING_INVOICES_REFRESH_STATUS'),
       ('MAN_BILLS'),
       ('MAN_BILLS_CREATE'),
       ('MAN_BILL_DOWNLOAD'),
       ('PAYMENT_PROVIDERS'),
       ('PAYMENT_PROVIDERS_ADD_EDIT'),
       ('MAN_CARD_TYPES'),
       ('MAN_CARD_TYPES_ADD'),
       ('MAN_USER_EDIT_PERMISSIONS'),
       ('MON_COMPANY_DETAILS_APPLICATIONS_NITS'),
       ('MON_COMPANY_DETAILS_APPLICATIONS_NITS_CANCEL'),
       ('STA_CHEQUE_BY_BRANCH_LIST_FILT_PAYMENT_TYPES'),
       ('STA_CHEQUE_BY_COMPANY_LIST_FILT_PAYMENT_TYPE'),
       ('STA_CHEQUE_LIST_FILT_PAYMENT_TYPES'),
       ('REFERRAL_PROGRAM'),
       ('REFERRAL_PROGRAM_SETTINGS'),
       ('REFERRAL_PROGRAM_SETTINGS_ADD_EDIT'),
       ('REFERRAL_PROGRAM_SETTINGS_EDIT_STATUS'),
       ('MAN_TERMINAL_DB_FILES'),
       ('MAN_TERMINAL_DB_FILES_ADD'),
       ('MAN_TERMINAL_DB_FILES_EDIT'),
       ('MAN_TERMINAL_DB_FILES_DELETE'),
       ('CATALOG_CHANGE_PRICE'),
       ('MON_COMPANY_DETAILS_INCOMES_DELETE'),
       ('MAN_NOTIFICATIONS')
on conflict do nothing;

insert into region (id, code, name_ru, name_uz, region_code)
values (1, 3, 'Андижанская Область', 'Андижон Вилояти', null),
       (2, 6, 'Бухарская Область', 'Бухоро Вилояти', null),
       (3, 8, 'Джизакская Область', 'Жиззах Вилояти', null),
       (4, 10, 'Кашкадарьинская Область', 'Қашқадарё Вилояти', null),
       (5, 12, 'Навоийская Область', 'Навоий Вилояти', null),
       (6, 14, 'Наманганская Область', 'Наманган Вилояти', null),
       (7, 18, 'Самаркандская Область', 'Самарқанд Вилояти', null),
       (8, 22, 'Сурхандарьинская Область', 'Сурхондарё Вилояти', null),
       (9, 24, 'Сырдарьинская Область', 'Сирдарё Вилояти', null),
       (10, 26, 'Город Ташкент', 'Тошкент Шаҳар', null),
       (11, 27, 'Ташкентская Область', 'Тошкент Вилояти', null),
       (12, 30, 'Ферганская Область', 'Фарғона Вилояти', null),
       (13, 33, 'Хорезмская Область', 'Хоразм Вилояти', null),
       (14, 35, 'Респ. Каракалпакстан', 'Қорақалпоғистон Респ.', null)
on conflict do nothing;

insert into public.city (id, code, name_ru, name_uz, region_code, city_code)
values (8, 13, 'Город Янгиер', 'Янгиер Шаҳар', 24, '1324'),
       (9, 1, 'Мирабадский Район', 'Миробод Тумани', 26, '0126'),
       (10, 2, 'Мирзо Улугбекский Район', 'Мирзо Улуғбек Тумани', 26, '0226'),
       (11, 3, 'Юнусабадский Район', 'Юнусобод Тумани', 26, '0326'),
       (12, 4, 'Яккасарайский Район', 'Яккасарой Тумани', 26, '0426'),
       (13, 5, 'Шайхантахурский Район', 'Шайхонтоҳур Тумани', 26, '0526'),
       (14, 6, 'Чиланзарский Район', 'Чилонзор Тумани', 26, '0626'),
       (15, 5, 'Байсунский Район', 'Бойсун Тумани', 22, '0522'),
       (16, 7, 'Сергелийский Район', 'Сирғали Тумани', 26, '0726'),
       (17, 6, 'Музрабадский Район', 'Музрабод Тумани', 22, '0622'),
       (18, 8, 'Яшнабадский Район', 'Яшнобод Тумани', 26, '0826'),
       (19, 9, 'Алмазарский Район', 'Олмазор Тумани', 26, '0926'),
       (20, 10, 'Учтепинский Район', 'Учтепа Тумани', 26, '1026'),
       (21, 11, 'Бектемирский Район', 'Бектемир Тумани', 26, '1126'),
       (22, 1, 'Бекабадский Район', 'Бекобод Тумани', 27, '0127'),
       (23, 2, 'Букинский Район', 'Бўка Тумани', 27, '0227'),
       (24, 3, 'Бостанлыкский Район', 'Бўстонлиқ Тумани', 27, '0327'),
       (25, 4, 'Зангиатинский Район', 'Зангиота Тумани', 27, '0427'),
       (26, 5, 'Юкори Чирчикский Район', 'Юқори Чирчиқ Тумани', 27, '0527'),
       (27, 6, 'Кибрайский Район', 'Қибрай Тумани', 27, '0627'),
       (28, 7, 'Аккурганский Район', 'Оққўрғон Тумани', 27, '0727'),
       (29, 8, 'Ахангаранский Район', 'Оҳангарон Тумани', 27, '0827'),
       (30, 9, 'Паркентский Район', 'Паркент Тумани', 27, '0927'),
       (31, 10, 'Пскентский Район', 'Пискент Тумани', 27, '1027'),
       (32, 12, 'Урта-Чирчикский Район', 'Ўрта Чирчиқ Тумани', 27, '1227'),
       (33, 13, 'Чиназский Район', 'Чиноз Тумани', 27, '1327'),
       (34, 7, 'Денаусский Район', 'Денов Тумани', 22, '0722'),
       (35, 14, 'Куйи-Чирчикский Район', 'Қуйи Чирчиқ Тумани', 27, '1427'),
       (36, 15, 'Янгийульский Район', 'Янгийўл Тумани', 27, '1527'),
       (37, 16, 'Город Ангрен', 'Ангрен Шаҳар', 27, '1627'),
       (38, 17, 'Город Бекабад', 'Бекобод Шаҳар', 27, '1727'),
       (39, 18, 'Город Алмалык', 'Олмалиқ Шаҳар', 27, '1827'),
       (40, 20, 'Город Чирчик', 'Чирчиқ Шаҳар', 27, '2027'),
       (41, 1, 'Город Кувасай', 'Қувасой Шаҳар', 30, '0130'),
       (42, 3, 'Город Коканд', 'Қўқон Шаҳар', 30, '0330'),
       (43, 4, 'Город Маргилан', 'Марғилон Шаҳар', 30, '0430'),
       (44, 5, 'Город Фергана', 'Фарғона Шаҳар', 30, '0530'),
       (45, 6, 'Бешарыкский Район', 'Бешариқ Тумани', 30, '0630'),
       (46, 7, 'Багдадский Район', 'Боғдод Тумани', 30, '0730'),
       (47, 8, 'Джаркурганский Район', 'Жарқўрғон Тумани', 22, '0822'),
       (48, 9, 'Кумкурганский Район', 'Қумқўрғон Тумани', 22, '0922'),
       (49, 8, 'Бувайдинский Район', 'Бувайда Тумани', 30, '0830'),
       (50, 9, 'Дангаринский Район', 'Дангара Тумани', 30, '0930'),
       (51, 10, 'Язьяванский Район', 'Ёзёвон Тумани', 30, '1030'),
       (52, 11, 'Кувинский Район', 'Қува Тумани', 30, '1130'),
       (53, 12, 'Алтыарыкский Район', 'Олтиариқ Тумани', 30, '1230'),
       (54, 13, 'Куштепинский Район', 'Қўштепа Тумани', 30, '1330'),
       (55, 14, 'Риштанский Район', 'Риштон Тумани', 30, '1430'),
       (56, 15, 'Сохский Район', 'Сўх Тумани', 30, '1530'),
       (57, 16, 'Ташлакский Район', 'Тошлоқ Тумани', 30, '1630'),
       (58, 17, 'Узбекистанский Район', 'Ўзбекистон Тумани', 30, '1730'),
       (59, 18, 'Учкуприкский Район', 'Учкўприқ Тумани', 30, '1830'),
       (60, 19, 'Ферганский Район', 'Фарғона Тумани', 30, '1930'),
       (61, 20, 'Фуркатский Район', 'Фурқат Тумани', 30, '2030'),
       (62, 1, 'Город Ургенч', 'Урганч Шаҳар', 33, '0133'),
       (63, 10, 'Кизирикский Район', 'Қизириқ Тумани', 22, '1022'),
       (64, 11, 'Сарыассийский Район', 'Сариосиё Тумани', 22, '1122'),
       (65, 3, 'Питнак Район', 'Питнак Тумани', 33, '0333'),
       (66, 4, 'Ургенчский Район', 'Урганч Тумани', 33, '0433'),
       (67, 5, 'Хивинский Район', 'Хива Тумани', 33, '0533'),
       (68, 6, 'Хазараспский Район', 'Хозарасп Тумани', 33, '0633'),
       (69, 7, 'Гурленский Район', 'Гурлан Тумани', 33, '0733'),
       (71, 9, 'Янгиарыкский Район', 'Янгиариқ Тумани', 33, '0933'),
       (72, 10, 'Кушкупырский Район', 'Қўшкўпир Тумани', 33, '1033'),
       (73, 11, 'Багатский Район', 'Боғот Тумани', 33, '1133'),
       (70, 8, 'Шаватский Район', 'Шовот Тумани', 33, '0833'),
       (74, 12, 'Термезский Район', 'Термиз  Тумани', 22, '1222'),
       (75, 12, 'Ханкинский Район', 'Хонқа Тумани', 33, '1233'),
       (76, 13, 'Янгибазарский Район', 'Янгибозор Тумани', 33, '1333'),
       (77, 1, 'Город Нукус', 'Нукус Шаҳар', 35, '0135'),
       (78, 8, 'Амударьинский Район', 'Амударё Тумани', 35, '0835'),
       (79, 9, 'Берунийский Район', 'Беруний Тумани', 35, '0935'),
       (80, 11, 'Кегейлийский Район', 'Кегейлитумани', 35, '1135'),
       (81, 12, 'Кунградский Район', 'Қўнғрод Тумани', 35, '1235'),
       (82, 13, 'Кандикульский Район', 'Қонликўл Тумани', 35, '1335'),
       (83, 14, 'Муйнакский Район', 'Муйноқ Тумани', 35, '1435'),
       (84, 13, 'Шерабадский Район', 'Шеробод Тумани', 22, '1322'),
       (85, 15, 'Нукусский Район', 'Нукус Тумани', 35, '1535'),
       (86, 16, 'Тахтакупырский Район', 'Тахтакўпир Тумани', 35, '1635'),
       (87, 17, 'Турткульский Район', 'Тўрткўл Тумани', 35, '1735'),
       (88, 18, 'Ходжейлийский Район', 'Хўжайли Тумани', 35, '1835'),
       (89, 19, 'Чимбайский Район', 'Чимбой Тумани', 35, '1935'),
       (90, 14, 'Шурчинский Район', 'Шўрчи Тумани', 22, '1422'),
       (91, 20, 'Шуманайский Район', 'Шуманай Тумани', 35, '2035'),
       (92, 1, 'Окалтынский Район', 'Оқолтин Тумани', 24, '0124'),
       (93, 15, 'Узунский Район', 'Узун Тумани', 22, '1522'),
       (94, 2, 'Баяутский Район', 'Боёвут Тумани', 24, '0224'),
       (95, 3, 'Гулистанский Район', 'Гулистон Тумани', 24, '0324'),
       (96, 5, 'Мирзаабадский Район', 'Мирзаобод Тумани', 24, '0524'),
       (97, 21, 'Элликкалинский', 'Элликқъала  Тумани', 35, '2135'),
       (98, 22, 'Караузякский Район', 'Қораузак Тумани', 35, '2235'),
       (99, 2, 'Город Шаҳрисабз', 'Шаҳрисабз Шаҳар', 10, '0210'),
       (100, 11, 'Ташкентский Район', 'Тошкент Тумани', 27, '1127'),
       (101, 19, 'Город Ахангаран', 'Оҳангарон Шаҳар', 27, '1927'),
       (102, 22, 'Город Янгийуль', 'Янгийўл Шаҳар', 27, '2227'),
       (103, 23, 'Город Нурафшон', 'Нурафшон Шаҳар', 27, '2327'),
       (104, 2, 'Город Хива', 'Хива Шаҳар', 33, '0233'),
       (105, 23, 'Тахиаташский Район', 'Тахиатош Тумани', 35, '2335'),
       (106, 1, 'Город Андижан', 'Андижон Шаҳар', 3, '0103'),
       (107, 3, 'Город Ханабад', 'Хонобод Шаҳар', 3, '0303'),
       (108, 5, 'Город Карасу', 'Қорасув Шаҳар', 3, '0503'),
       (109, 6, 'Алтынкульский Район', 'Олтинкўл Тумани', 3, '0603'),
       (110, 7, 'Андижанский Район', 'Андижон Тумани', 3, '0703'),
       (111, 8, 'Балыкчинский Район', 'Балиқчи Тумани', 3, '0803'),
       (112, 9, 'Бозский Район', 'Бўз Тумани', 3, '0903'),
       (113, 10, 'Булакбашинский Район', 'Булоқбоши Тумани', 3, '1003'),
       (114, 11, 'Джалалкудукский Район', 'Жалолқудуқ Тумани', 3, '1103'),
       (115, 12, 'Избасканский Район', 'Избоскан Тумани', 3, '1203'),
       (116, 13, 'Улугнорский Район', 'Улуғнор Тумани', 3, '1303'),
       (117, 14, 'Кургантепинский Район', 'Қўрғонтепа Тумани', 3, '1403'),
       (118, 15, 'Асакинский Район', 'Асака Тумани', 3, '1503'),
       (119, 16, 'Мархаматский Район', 'Марҳамат Тумани', 3, '1603'),
       (120, 17, 'Шахриханский Район', 'Шаҳрихон Тумани', 3, '1703'),
       (121, 18, 'Пахтаабадский Район', 'Пахтаобод Тумани', 3, '1803'),
       (122, 19, 'Ходжаабадский Район', 'Хўжаобод Тумани', 3, '1903'),
       (123, 1, 'Город Бухара', 'Бухоро Шаҳар', 6, '0106'),
       (124, 3, 'Город Каган', 'Когон Шаҳар', 6, '0306'),
       (125, 4, 'Алатский Район', 'Олот Тумани', 6, '0406'),
       (126, 5, 'Бухарский Район', 'Бухоро Тумани', 6, '0506'),
       (127, 6, 'Вабкентский Район', 'Вобкент Тумани', 6, '0606'),
       (128, 7, 'Гиждуванский Район', 'Ғиждувон Тумани', 6, '0706'),
       (129, 8, 'Джандарский Район', 'Жондор Тумани', 6, '0806'),
       (130, 9, 'Каганский Район', 'Когон Тумани', 6, '0906'),
       (131, 10, 'Каракульский Район', 'Қоракўл Тумани', 6, '1006'),
       (132, 11, 'Пешкунский Район', 'Пешку Тумани', 6, '1106'),
       (133, 12, 'Ромитанский Район', 'Ромитан Тумани', 6, '1206'),
       (1, 4, 'Алтынсайский Район', 'Олтинсой Тумани', 22, '0422'),
       (2, 6, 'Сайхунабадский Район', 'Сайхунобод Тумани', 24, '0624'),
       (3, 7, 'Сырдарьинский Район', 'Сирдарё Тумани', 24, '0724'),
       (4, 8, 'Хавастский Район', 'Ховос Тумани', 24, '0824'),
       (5, 9, 'Сардобинский Район', 'Сардоба Тумани', 24, '0924'),
       (6, 10, 'Город Гулистан', 'Гулистон Шаҳар', 24, '1024'),
       (7, 12, 'Город Ширин', 'Ширин Шаҳар', 24, '1224'),
       (134, 13, 'Шафирканский Район', 'Шофирикон Тумани', 6, '1306'),
       (135, 14, 'Караулбазарский Район', 'Қоровулбозор Тумани', 6, '1406'),
       (145, 10, 'Пахтакорский Район', 'Пахтакор Тумани', 8, '1008'),
       (146, 11, 'Фаришский Район', 'Фориш Тумани', 8, '1108'),
       (147, 12, 'Город Джизак', 'Жиззах Шаҳар', 8, '1208'),
       (148, 13, 'Янгиабадский Район', 'Янгиобод Тумани', 8, '1308'),
       (149, 1, 'Город Карши', 'Қарши Шаҳар', 10, '0110'),
       (150, 4, 'Гузарский Район', 'Ғузор Тумани', 10, '0410'),
       (151, 5, 'Дехканабадский Район', 'Деҳқонобод Тумани', 10, '0510'),
       (152, 6, 'Камашинский Район', 'Қамаши Тумани', 10, '0610'),
       (153, 7, 'Каршинский Район', 'Қарши Тумани', 10, '0710'),
       (154, 8, 'Касанский Район', 'Косон Тумани', 10, '0810'),
       (155, 9, 'Китабский Район', 'Китоб Тумани', 10, '0910'),
       (156, 10, 'Мубарекский Район', 'Муборак Тумани', 10, '1010'),
       (157, 11, 'Нишанский Район', 'Нишон Тумани', 10, '1110'),
       (158, 12, 'Касбинский Район', 'Касби Тумани', 10, '1210'),
       (159, 14, 'Чиракчинский Район', 'Чироқчи Тумани', 10, '1410'),
       (160, 15, 'Шахризабский Район', 'Шахрисабз Тумани', 10, '1510'),
       (161, 16, 'Яккабагский Район', 'Яккабоғ Тумани', 10, '1610'),
       (162, 17, 'Миришкорский Район', 'Миришкор Тумани', 10, '1710'),
       (163, 1, 'Город Навойи', 'Навоий Шаҳар', 12, '0112'),
       (164, 2, 'Город Заравшан', 'Зарафшон Шаҳар', 12, '0212'),
       (165, 3, 'Город Учкудук', 'Учкудуқ Шаҳар', 12, '0312'),
       (166, 4, 'Кенимехский Район', 'Конимех Тумани', 12, '0412'),
       (167, 5, 'Кызылтепинский Район', 'Қизилтепа Тумани', 12, '0512'),
       (168, 7, 'Тамдынский Район', 'Томди Тумани', 12, '0712'),
       (169, 8, 'Навбахорский Район', 'Навбаҳор Тумани', 12, '0812'),
       (170, 9, 'Хатырчинский Район', 'Хатирчи Тумани', 12, '0912'),
       (171, 10, 'Нуратинский Район', 'Нурота Тумани', 12, '1012'),
       (172, 11, 'Карманинский Район', 'Кармана Тумани', 12, '1112'),
       (173, 1, 'Город Наманган', 'Наманган Шаҳар', 14, '0114'),
       (174, 7, 'Мингбулакский Район', 'Мингбулоқ Тумани', 14, '0714'),
       (175, 8, 'Касансайский Район', 'Косонсой Тумани', 14, '0814'),
       (176, 9, 'Наманганский Район', 'Наманган Тумани', 14, '0914'),
       (177, 10, 'Нарынский Район', 'Норин Тумани', 14, '1014'),
       (178, 11, 'Папский Район', 'Поп Тумани', 14, '1114'),
       (179, 12, 'Туракурганский Район', 'Тўрақўрғон Тумани', 14, '1214'),
       (180, 13, 'Уйчинский Район', 'Уйчи Тумани', 14, '1314'),
       (181, 14, 'Учкурганский Район', 'Учқўрғон Тумани', 14, '1414'),
       (182, 15, 'Чартакский Район', 'Чортоқ Тумани', 14, '1514'),
       (183, 16, 'Чустский Район', 'Чуст Тумани', 14, '1614'),
       (184, 17, 'Янгикурганский Район', 'Янгиқўргон Тумани', 14, '1714'),
       (185, 1, 'Город Самарканд', 'Самарқанд Шаҳар', 18, '0118'),
       (186, 4, 'Тайлякский Район', 'Тойлоқ Тумани', 18, '0418'),
       (187, 6, 'Город Каттакурган', 'Каттақўрғон Шаҳар', 18, '0618'),
       (188, 8, 'Акдарьинский Район', 'Оқдарё Тумани', 18, '0818'),
       (189, 9, 'Булунгурский Район', 'Булунғур Тумани', 18, '0918'),
       (190, 11, 'Джамбайский Район', 'Жомбой Тумани', 18, '1118'),
       (191, 12, 'Иштыханский Район', 'Иштихон Тумани', 18, '1218'),
       (136, 1, 'Арнасайский Район', 'Арнасой Тумани', 8, '0108'),
       (137, 2, 'Бахмальский Район', 'Бахмал Тумани', 8, '0208'),
       (138, 3, 'Галляаральский Район', 'Ғаллаорол Тумани', 8, '0308'),
       (139, 4, 'Шароф Рашидовский Район', 'Шароф Рашидов Тумани', 8, '0408'),
       (140, 5, 'Дустликский Район', 'Дўстлик Тумани', 8, '0508'),
       (141, 6, 'Зарбдорский Район', 'Зарбдор Тумани', 8, '0608'),
       (142, 7, 'Зааминский Район', 'Зомин Тумани', 8, '0708'),
       (143, 8, 'Зафарабадский Район', 'Зафаробод Тумани', 8, '0808'),
       (144, 9, 'Мирзачульский Район', 'Мирзачўл Тумани', 8, '0908'),
       (192, 13, 'Каттакурганский Район', 'Каттақўрғон Тумани', 18, '1318'),
       (193, 14, 'Кошрабадский Район', 'Қўшрабод Тумани', 18, '1418'),
       (194, 15, 'Нарпайский Район', 'Нарпай Тумани', 18, '1518'),
       (195, 17, 'Пайарыкский Район', 'Пайариқ Тумани', 18, '1718'),
       (196, 18, 'Пастдаргомский Район', 'Пастдарғом Тумани', 18, '1818'),
       (197, 19, 'Пахтаачинский Район', 'Пахтачи Тумани', 18, '1918'),
       (198, 20, 'Самаркандский Район', 'Самарқанд Тумани', 18, '2018'),
       (199, 21, 'Нуробадский Район', 'Нуробод Тумани', 18, '2118'),
       (200, 22, 'Ургутсский Район', 'Ургут Тумани', 18, '2218'),
       (201, 1, 'Город Термез', 'Термиз Шаҳар', 22, '0122'),
       (202, 3, 'Ангорский Район', 'Ангор Тумани', 22, '0322'),
       (203, 16, 'Бандихонский Район', 'Бандихон Тумани', 22, '1622'),
       (204, 14, 'Тупрок-кала Район', 'Тупроққалъа Тумани', 33, '1433'),
       (205, 10, 'Бозатауский Район', 'Бозатаус Тумани', 35, '1035'),
       (206, 12, 'Янгихаётский Район', 'Янгихаёт Тумани', 26, '1226')
on conflict do nothing;

insert into company (id, created_by, created_date, last_modified_by, last_modified_date, address, business_type,
                     city_id, inn, name, region_id)
values (1, 'system', now(), 'system', now(), 'Tashkent', 'OOO', 9, '123456789', 'Admin Company', 10),
       (2, 'system', now(), 'system', now(), 'Tashkent', 'OOO', 9, '1234567891011', 'User Company', 10)
on conflict do nothing;

insert into branch (id, created_by, created_date, last_modified_by, last_modified_date, address, city_id, city_key_id,
                    company_id,
                    name, region_id, fiscal)
values (1, 'system', now(), 'system', now(), 'Tashkent', 9, 9, 1, 'Admin Branch', 10, true),
       (2, 'system', now(), 'system', now(), 'Tashkent', 9, 9, 2, 'User Branch', 10, true)
on conflict do nothing;

insert into users (id, created_by, created_date, last_modified_by, last_modified_date, activated, branch_id, company_id,
                   login,
                   first_name, last_name, password_hash) /*aA12345678*/
values (1, 'system', now(), 'system', now(), true, 1, 1, 'admin', 'Admin', 'Admin',
        '$2a$12$REDACTEDREDACTEDREDACTEDREDACTEDREDACTEDREDACTEDREDAC'),
       (2, 'system', now(), 'system', now(), true, 2, 2, 'user', 'User', 'User',
        '$2a$12$REDACTEDREDACTEDREDACTEDREDACTEDREDACTEDREDACTEDREDAC')
on conflict do nothing;

insert into user_authority (user_id, authority_name)
values (1, 'ROLE_USER'),
       (1, 'ROLE_ADMIN'),
       (1, 'ROLE_OWNER'),
       (1, 'ROLE_BRANCH_ADMIN'),
       (1, 'ROLE_SMARTPOS_ADMIN'),
       (1, 'ROLE_SALES_DIRECTOR'),
       (1, 'ROLE_SALES_MANAGER'),
       (1, 'ROLE_REGION_MANAGER'),
       (1, 'ROLE_SUPPORT_MANAGER'),
       (2, 'ROLE_USER'),
       (2, 'ROLE_OWNER'),
       (2, 'ROLE_CASHIER')
on conflict do nothing;

update company
set owner_id = 1
where id = 1;
update company
set owner_id = 2
where id = 2;