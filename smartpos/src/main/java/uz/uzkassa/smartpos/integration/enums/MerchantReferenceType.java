package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MerchantReferenceType {
    SUPPLY("Supply"),
    SMARTPOS("Smartpos");

    private final String name;
    private final String code;

    MerchantReferenceType(String name) {
        this.code = this.name();
        this.name = name;
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static MerchantReferenceType forValue(Map<String, String> value) {
        return MerchantReferenceType.valueOf(value.get("code"));
    }
}
