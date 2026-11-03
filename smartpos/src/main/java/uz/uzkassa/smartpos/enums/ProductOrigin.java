package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.smartpos.dto.EnumDTO;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum ProductOrigin {
    SELF_MADE(1, "Производитель", "O'zi ishlab chiqargan", "Ўзи ишлаб чиқарган"),
    IMPORT(2, "Куплено", "Sotib olingan", "Сотиб олинган"),
    SERVICE(3, "Услуга", "Xizmat kursatish", "Хизмат курсатиш"),
    NEUTRAL(4, "Не участвую", "Ishtirok etmayman", "Иштирок этмайман");

    private final int code;
    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;

    public String getName() {
        return switch (LanguageUtils.getLanguage()) {
            case LanguageEnum.uz -> nameUz;
            case LanguageEnum.cyrillic -> nameCyrillic;
            default -> nameRu;
        };
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
                if (code.equalsIgnoreCase(item.getNameRu()) || code.equals(item.name()) || code.equals(String.valueOf(item.getCode()))) {
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

    public EnumDTO toDto() {
        return new EnumDTO(getCode(), name(), getName(), getNameRu(), getNameUz(), getNameCyrillic());
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
