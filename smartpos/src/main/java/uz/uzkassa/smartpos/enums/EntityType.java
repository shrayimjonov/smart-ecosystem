package uz.uzkassa.smartpos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityType {
    USER("User"),
    COMPANY("Company"),
    CONTRACT("Договор"),
    POWER_OF_ATTORNEY("Доверенность"),
    BRANCH("Branch"),
    TELEGRAM_ACCOUNT("Telegram Account"),
    TERMINAL_LOG("Terminal log"),
    BILL_ACT("Bill act"),
    BILL_AGREEMENT("Bill agreement"),
    BILL_HUMO_REQUEST("Bill humo request"),
    BILL_HUMO_CONTRACT("Bill humo contract"),
    OFD_APPLICATION("Ofd application"),
    COMPANY_AGREEMENT("Company agreement"),
    RECEIPT_TEMPLATE("Receipt template"),
    TERMINAL_TRANSFER("Terminal transfer"),
    EPS_SERVICES("Eps services"),
    INSTRUCTOR("Instructor");


    final String name;
}
