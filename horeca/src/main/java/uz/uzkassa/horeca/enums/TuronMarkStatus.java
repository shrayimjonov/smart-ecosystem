package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TuronMarkStatus implements Serializable {
    EMITTED("Эмитирован"),
    APPLIED("Нанесен"), //
    INTRODUCED("Введен в оборот"), //
    WRITTEN_OFF("Утилизирован"),
    WITHDRAWN("Выведен из оборота"),
    RESERVED_NOT_USED("Зарезервировано. Не использовать"),
    INTRODUCED_RETURNED("Возвращен в оборот"), //
    DISAGGREGATED("Дезагрегирован"),
    WAIT_SHIPMENT("Ожидает отгрузки"),
    EXPORTED("Используется для документов эспорта"),
    APPLIED_NOT_PAID("Нанесен, не оплачен"),
    ERROR("Ошибка"); //system status

    private final String text;


    public static TuronMarkStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown TuronMarkStatus enum value: " + code));
    }

    @JsonCreator
    public static TuronMarkStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for TuronMarkStatus");
    }


    @JsonValue
    public String toValue() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
