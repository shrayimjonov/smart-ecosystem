package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum APayStatus {
    PENDING("PENDING", "Ожидание"),
    PAID("PAID", "Оплачено"),
    CANCELED("CANCELED", "Отменено");

    private final String code;
    private final String name;

    APayStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @JsonCreator
    static APayStatus findValue(String code) {
        return APayStatus.valueOf(code);
    }
}
