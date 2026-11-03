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
public enum FacturaType {
    STANDART(0,"Стандартный", "Standart"),
    ADDITIONAL(1,"Дополнительный", "Qo'shimcha"),
    EXPENSE_COVER(2,"Возмещение расходов", "Xarajatlarni qoplash"),
    WITHOUT_PAYMENT(3,"Без оплаты", "To'lovsiz"),
    EDITED(4,"Исправленный", "Tuzatuvchi");

    private Integer id;
    private String ruText;
    private String uzText;

    public String getName(){
        final String locale = SecurityUtils.getCurrentLocale();
        String name;
        switch (locale) {
            case "uz":
                name = this.getUzText();
                break;
            default:
                name = this.getRuText();
                break;
        }
        return name;
    }

    public static FacturaType fromId(Integer id) {
        if (id != null) {
            for (FacturaType item : FacturaType.values()) {
                if (id.equals(item.getId())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static FacturaType fromString(String code) {
        if (code != null) {
            for (FacturaType item : FacturaType.values()) {
                if (code.equalsIgnoreCase(item.getRuText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(FacturaType.values().length);
        for (FacturaType item : FacturaType.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto(){
        return new EnumDTO(getId(), name(), getName());
    }

    @JsonCreator
    public static FacturaType forValue(String value) {
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
