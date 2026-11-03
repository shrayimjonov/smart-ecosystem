package uz.uzkassa.cts.enums.billing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityType {
    USER("User"),
    COMPANY("Company"),
    CONTRACT("Договор"),
    INVOICE_PAYMENT("Счет на оплату"),
    POWER_OF_ATTORNEY("Доверенность"),
    PRODUCT("Product"),
    BRANCH("Branch"),
    PURCHASE_ORDER("Заказ"),
    CATEGORY("Category"),
    MANUFACTURER("Manufacturer"),
    TEMPLATE("Template"),
    TELEGRAM_ACCOUNT("Telegram Account"),
    PROMOTION("Promotion"),
    LEAD("Lead"),
    CUSTOMER("Customer");


    final String name;
}
