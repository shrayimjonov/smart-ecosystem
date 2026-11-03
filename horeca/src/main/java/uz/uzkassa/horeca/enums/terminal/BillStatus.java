package uz.uzkassa.horeca.enums.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * User: Shuxratjon Rayimjonov
 * Date: 04.05.2024
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
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
