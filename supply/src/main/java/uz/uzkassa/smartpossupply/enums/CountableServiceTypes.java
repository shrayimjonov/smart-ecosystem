package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CountableServiceTypes implements Serializable {
    MARK_ORDER("SERVICES_MARKS_ORDERS"),
    AGGREGATION("SERVICES_AGGREGATION"),
    INVOICE("SERVICES_INVOICE"),
    SERVICES("SERVICES"),
    AGENTS("AGENTS"),
    EXPEDITOR("EXPEDITOR"),
    CASHIER("CASHIER"),
    B2BMARKETPLACE("B2BMARKETPLACE"),
    ONLINE_KKM("ONLINE_KKM"),
    INTEGRATION("INTEGRATION");

    private final String code;

    CountableServiceTypes(String code) {
        this.code = code;
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static CountableServiceTypes forValue(Map<String, String> value) {
        return CountableServiceTypes.valueOf(value.get("code"));
    }
}
