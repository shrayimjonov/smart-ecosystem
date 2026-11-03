package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum TransferStatus {
    SENT("Отправлен"),
    REQUESTED("Запрос"),
    APPROVED("Подтвержден"),
    CANCELLED("Отменен");

    private final String nameRu;
    private final String code;

    TransferStatus(String nameRu) {
        this.nameRu = nameRu;
        this.code = name();
    }

    @JsonCreator
    public static TransferStatus forValue(Map<String, String> value) {
        return TransferStatus.valueOf(value.get("code"));
    }

}
