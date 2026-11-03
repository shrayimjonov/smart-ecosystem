package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BillStatusEnum {
    PENDING("Ожидание"),
    PAID("Оплачено"),
    CANCELED("Отменено");

    private final String name;
    private final String code;

    BillStatusEnum(String name) {
        this.code = this.name();
        this.name = name;
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static BillStatusEnum forValue(Map<String, String> value) {
        return BillStatusEnum.valueOf(value.get("code"));
    }
}
