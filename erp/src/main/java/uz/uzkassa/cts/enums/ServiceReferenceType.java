package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ServiceReferenceType {
    APAY("A-Pay"),
    SMARTPOS("SMARTPOS"),
    SUPPLY("Supply"),
    CTO("Cto");

    private final String name;
    private final String code;

    ServiceReferenceType(String name) {
        this.code = this.name();
        this.name = name;
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ServiceReferenceType forValue(Map<String, String> value) {
        return ServiceReferenceType.valueOf(value.get("code"));
    }
}
