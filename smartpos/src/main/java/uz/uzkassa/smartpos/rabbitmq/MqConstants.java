package uz.uzkassa.smartpos.rabbitmq;


public interface MqConstants {

    String DELAY_EXCHANGE_NAME = "delayedExchange";

    String X_DELAY = "x-delay";
    String TYPE_ID = "__TypeId__";

    String RECEIPT_QUEUE = "receipt_queue";
    String SUPPLY_RECEIPT_QUEUE = "supply_receipt_queue";

    String PRODUCT_CREATE_QUEUE = "product_create_queue";
    String VAT_UPDATE_QUEUE = "vat_update_queue";
    String MARK_UPDATE_QUEUE = "mark_update_queue";
    String COMMITTENT_TIN_UPDATE_QUEUE = "committent_tin_update_queue";
    String PRODUCT_ORIGIN_UPDATE_QUEUE = "product_origin_update_queue";
    String CATEGORY_DELETE_QUEUE = "category_delete_queue";
    String DELETE_PRODUCT_REFERENCES = "delete_product_references";
    String CREATE_PAYMENT = "create_payment";
    String CONFIRM_PAYMENT = "confirm_payment";
    String CREATE_HOME_SERVICE_PRODUCT = "create_home_service_product";
    String CREATE_FASHION_CATALOG = "create_fashion_catalog";
    String PRODUCT_ITEMS_TRANSFER_QUEUE = "product_items_transfer_queue";

    //Warehouse
    String REGISTER_QUEUE = "register_queue";
    String REGISTER_UPDATE_QUEUE = "register_update_queue";
    String BALANCE_HISTORY_QUEUE = "balance_history_queue";
    String INVOICE_UPDATE_QUEUE = "invoice_update_queue";

    String RECEIPT_KAFKA_QUEUE = "receipt_kafka_queue";
    String UPDATE_PINFL_QUEUE = "update_pinfl_queue";
    String INCOME_BATCH_QUEUE = "income_batch_queue";
    String RECEIPT_MARKING_BATCH_QUEUE = "receipt_marking_batch_queue";
    String UNBLOCK_TERMINALS_QUEUE = "unblock_terminals_queue";
    String BRANCH_REGISTER_WITH_APAY = "branch_register_with_apay";

    String FASHION_REGISTER_QUEUE = "fashion_register_queue";
    String FASHION_REPORT_QUEUE = "fashion_report_queue";
    String UPDATE_PRODUCT_PRICES_BY_INCOME_QUEUE = "update_product_prices_by_income_queue";
    String DELETE_FASHION_DATA_BY_COLOR_GROUP = "delete_fashion_data_by_color_group";
    String DELETE_FASHION_DATA_BY_PRODUCT_ITEM = "delete_fashion_data_by_product_item";
    String FASHION_TRANSFER_QUEUE = "fashion_transfer_queue";
    String MARKING_INCOME_SOLD_QUEUE = "marking_income_sold_queue";
    String RECEIPT_AUTO_PRINT_QUEUE = "receipt_auto_print_queue";
    String SALES_WAREHOUSE_QUEUE = "sales_warehouse_queue";
    String SALES_CLICKHOUSE_QUEUE = "sales_clickhouse_queue";
    String SALES_RECEIPT_QUEUE = "sales_receipt_queue";
    String SALES_FROM_RECEIPT_QUEUE = "sales_from_receipt_queue";
    String ONE_C_RECEIPT_QUEUE = "one_c_receipt_queue";
    String NON_FISCAL_RECEIPT_QUEUE = "non_fiscal_receipt_queue";
    String RECEIPT_WRONG_PAYMENTS_QUEUE = "receipt_wrong_payments_queue";
}
