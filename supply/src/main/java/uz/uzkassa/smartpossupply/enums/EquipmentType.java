package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum EquipmentType implements Serializable {

    REFRIGERATOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Muzlatgich";
                case cyrillic -> "Музлатгич";
                default -> "Холодильник";
            };
        }
    },
    SHELF {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Polka";
                case cyrillic -> "Полка";
                default -> "Полка";
            };
        }
    };

    @Schema
    private final String name;

    @Schema
    private final String code;

    EquipmentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static EquipmentType forValue(Map<String, String> value) {
        return EquipmentType.valueOf(value.get("code"));
    }
}
