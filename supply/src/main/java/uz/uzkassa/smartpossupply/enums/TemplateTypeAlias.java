package uz.uzkassa.smartpossupply.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum TemplateTypeAlias implements Serializable {

    TRANSACTION_NUMBER("{TRANSACTION_NUMBER}"),
    CREATOR("{CREATOR}"),
    PRODUCT_NAME("{PRODUCT_NAME}"),
    BARCODE("{BARCODE}"),
    BRANCH_NAME("{BRANCH_NAME}"),
    WAREHOUSE_NAME("{WAREHOUSE_NAME}"),
    AGENT("{AGENT}"),
    ORDER_NUMBER("{ORDER_NUMBER}"),
    CLIENT_NAME("{CLIENT_NAME}"),
    CLIENT_TIN("{CLIENT_TIN}"),
    DRIVER_NAME("{DRIVER_NAME}"),
    CAR_INFO("{CAR_INFO}"),
    SUBSCRIPTION_SERVICES("{SUBSCRIPTION_SERVICES}");

    private final String code;

    TemplateTypeAlias(String code) {
        this.code = code;
    }
}
