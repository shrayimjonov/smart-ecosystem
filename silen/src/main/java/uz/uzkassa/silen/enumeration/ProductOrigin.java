package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum ProductOrigin {
    SELF_MADE(1,"Производитель", "O'zi ishlab chiqargan"),
    IMPORT(2,"Куплено", "Sotib olingan"),
    SERVICE(3,"Услуга", "Xizmat kursatish"),
    NEUTRAL(4,"Не участвую", "Ishtirok etmayman");

    private final int code;
    private final String ruText;
    private final String uzText;

    public String getName(){
        final String locale = SecurityUtils.getCurrentLocale();
        String name;
        if (locale.equals("uz")) {
            name = this.getUzText();
        } else {
            name = this.getRuText();
        }
        if (name == null || name.trim().isEmpty()) {
            name = this.getRuText();
        }
        return name;
    }

    public static ProductOrigin fromCode(int code) {
        for (ProductOrigin item : ProductOrigin.values()) {
            if (code == item.getCode()) {
                return item;
            }
        }
        return NEUTRAL;
    }

    public static ProductOrigin fromString(String code) {
        if (code != null) {
            for (ProductOrigin item : ProductOrigin.values()) {
                if (code.equalsIgnoreCase(item.getRuText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(ProductOrigin.values().length);
        for (ProductOrigin item : ProductOrigin.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto(){
        return new EnumDTO(getCode(), name(), getName());
    }

    @JsonCreator
    public static ProductOrigin forValue(String value) {
        return fromString(value);
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
