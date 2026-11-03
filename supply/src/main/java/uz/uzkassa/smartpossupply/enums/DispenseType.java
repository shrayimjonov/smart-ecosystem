package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DispenseType implements Serializable {

    WITH_RECEIPT(1) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Retsept bo'yicha";
                case cyrillic -> "Рецепт бўйича";
                default -> "По рецепту";
            };
        }
    },
    WITHOUT_RECEIPT(2) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Retseptsiz sotiladigan";
                case cyrillic -> "Рецептсиз сотиладиган";
                default -> "Без рецепта";
            };
        }
    };

    private final Integer id;
    private final String name;
    private final String code;


    DispenseType(Integer id) {
        this.id = id;
        this.name = this.getName();
        this.code = this.name();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static DispenseType buildById(Integer id) {
        if (id == null) {
            return null;
        }
        return Arrays.stream(values()).filter(dispenseType -> dispenseType.id.equals(id)).findFirst().orElse(null);
    }

    @JsonCreator
    public static DispenseType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for DispenseType");
    }

    public static DispenseType get(String code) {
        if (StringUtils.hasText(code)) {
            return Arrays.stream(values())
                .filter(dispenseType -> dispenseType.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DispenseType enum value: " + code));
        }
        return null;
    }
}
