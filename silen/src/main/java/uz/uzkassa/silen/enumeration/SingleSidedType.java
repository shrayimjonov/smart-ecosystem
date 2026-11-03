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
public enum SingleSidedType {
    PHYSICAL(1,"На физ. лицо", "Jismoniy shaxsga"),
    EXPORT(2,"На экспорт", "Xizmatlar eksporti"),
    IMPORT(3,"На импорт", "Xizmatlar importi"),
    GOVERNMENT(4,"Реализация, связанная с гос.секретом", "Davlat siriga bog'liq realizatsiya"),
    FINANCE(5,"Финансовые услуги", "Moliyaviy xizmatlar"),
    SELL_UNDER_MARKET(6,"Реализация ниже рыночной стоимости", "Bozor narxidan past narxda sotish"),
    SELL_UNDER_CUSTOM(7,"Реализация ниже таможенной стоимости", "Bojxona narxidan past narxda sotish");

    private final Integer id;
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
        if (name == null || name.trim().length() == 0) {
            name = this.getRuText();
        }
        return name;
    }

    public static SingleSidedType fromId(Integer id) {
        if (id != null) {
            for (SingleSidedType item : SingleSidedType.values()) {
                if (id.equals(item.getId())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static SingleSidedType fromString(String code) {
        if (code != null) {
            for (SingleSidedType item : SingleSidedType.values()) {
                if (code.equalsIgnoreCase(item.getRuText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(SingleSidedType.values().length);
        for (SingleSidedType item : SingleSidedType.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto(){
        return new EnumDTO(getId(), name(), getName());
    }

    @JsonCreator
    public static SingleSidedType forValue(String value) {
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
