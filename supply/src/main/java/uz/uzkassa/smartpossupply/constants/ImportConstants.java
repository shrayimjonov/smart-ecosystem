package uz.uzkassa.smartpossupply.constants;

public interface ImportConstants {

    interface CUSTOMER {
        String TIN = "TIN";
        String NAME = "NAME";
        String BRAND = "BRAND";
        String PHONE = "PHONE";
        String ADDRESS = "ADDRESS";
        String LONGITUDE_LATITUDE = "LONGITUDE_LATITUDE";
    }

    interface ORDER {
        String ORDER_NUMBER = "ORDER_NUMBER";
        String BRANCH_ID = "BRANCH_ID";
        String WAREHOUSE_ID = "WAREHOUSE_ID";
        String CREATE_STOCK_IN = "CREATE_STOCK_IN";
        String ORDER_DATE = "ORDER_DATE";
        String CONTRACT_NUMBER = "CONTRACT_NUMBER";
        String CONTRACT_DATE = "CONTRACT_DATE";
        String CUSTOMER_TIN = "CUSTOMER_TIN";
        String CUSTOMER_CODE = "CUSTOMER_CODE";
        String BRANCH_CODE = "BRANCH_CODE";
        String PRODUCT_NAME = "PRODUCT_NAME";
        String PRODUCT_BARCODE = "PRODUCT_BARCODE";
        String PRODUCT_VAT_BARCODE = "PRODUCT_VAT_BARCODE";
        String PRODUCT_SERIAL = "PRODUCT_SERIAL";
        String BASE_SUMMA = "BASE_SUMMA";
        String PROFIT_RATE = "PROFIT_RATE";
        String PRODUCT_UNIT_CODE = "PRODUCT_UNIT_CODE";
        String PRODUCT_UNIT_NAME = "PRODUCT_UNIT_NAME";
        String QTY = "QTY";
        String PRICE = "PRICE";
        String DELIVER_SUM = "DELIVER_SUM";
        String VAT_PRICE = "VAT_PRICE";
        String VAT_RATE = "VAT_RATE";
        String DELIVER_SUM_WITH_VAT = "DELIVER_SUM_WITH_VAT";
    }

    interface PRODUCT {
        String CATALOG_CODE = "CATALOG_CODE";
        String NAME = "NAME";
        String BARCODE = "BARCODE";
        String HAS_MARK = "HAS_MARK";
        String PRODUCT_ORIGIN = "PRODUCT_ORIGIN";
        String PRICE = "PRICE";
        String VAT = "VAT";
        String BRUTTO = "BRUTTO";
        String NETTO = "NETTO";
        String BRANCH_ID = "BRANCH_ID";
        String COMPANY_ID = "COMPANY_ID";
    }
}
