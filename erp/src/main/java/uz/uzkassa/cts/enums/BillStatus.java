package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BillStatus {
    PENDING("Ожидание"),
    PAID("Оплачено"),
    CANCELED("Отменено");

    private final String name;
    private final String code;

    BillStatus(String name) {
        this.code = this.name();
        this.name = name;
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static BillStatus forValue(Map<String, String> value) {
        return BillStatus.valueOf(value.get("code"));
    }
}
