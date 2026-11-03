package uz.uzkassa.cts.enums.apay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum APayMerchantReferenceType {
    SUPPLY("Supply"),
    SMARTPOS("Smartpos"),
    CTO("Cto");

    private final String name;
    private final String code;

    APayMerchantReferenceType(String name) {
        this.code = this.name();
        this.name = name;
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static APayMerchantReferenceType forValue(Map<String, String> value) {
        return APayMerchantReferenceType.valueOf(value.get("code"));
    }
}
