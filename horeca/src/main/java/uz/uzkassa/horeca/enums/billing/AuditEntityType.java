package uz.uzkassa.horeca.enums.billing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum AuditEntityType implements Serializable {
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
    PROMOTION("Promotion");

    final String name;
}
