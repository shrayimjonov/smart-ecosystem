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
public enum SortOrderType implements Serializable {

    LIST {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ro'yxat";
                case cyrillic -> "Рўйхат";
                default -> "Список";
            };
        }
    },
    TREE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Daraxt";
                case cyrillic -> "Дарахт";
                default -> "Дерево";
            };
        }
    };

    @Schema
    private final String name;

    @Schema
    private final String code;

    SortOrderType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static SortOrderType forValue(Map<String, String> value) {
        return SortOrderType.valueOf(value.get("code"));
    }
}
