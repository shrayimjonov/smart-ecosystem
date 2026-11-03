package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;
import java.util.Map;

public enum TransferType implements Serializable {
    IN,
    OUT,
    RETURN,
    ADJUSTMENT,
    TRANSFER_FROM,
    TRANSFER_TO;

    @JsonCreator
    @SuppressWarnings("unused")
    public static TransferType forValue(Map<String, String> value) {
        return TransferType.valueOf(value.get("code"));
    }
}
