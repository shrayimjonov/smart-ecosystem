package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.07.2023
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ProductOrigin implements Serializable {
    SELF_PRODUCED(1) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "O‘zi ishlab chiqargan";
                case cyrillic:
                    return "Ўзи ишлаб чиқарган";
                default:
                    return "Сам произвел";
            }
        }
    },
    PURCHASED(2) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Sotib olingan";
                case cyrillic:
                    return "Сотиб олинган";
                default:
                    return "Куплено";
            }
        }
    },
    SERVICE(3) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Xizmat ko‘rsatish";
                case cyrillic:
                    return "Хизмат кўрсатиш";
                default:
                    return "Услуга";
            }
        }
    },
    NOT_PARTICIPATE(4) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Ishtirok etmayman";
                case cyrillic:
                    return "Иштирок этмайман";
                default:
                    return "Не участвую";
            }
        }
    };

    private final Integer id;
    private final String name;
    private final String code;

    ProductOrigin(Integer id) {
        this.id = id;
        this.code = this.name();
        this.name = this.getName();
    }

    public static ProductOrigin get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ProductOrigin enum value: " + code));
    }

    @JsonCreator
    public static ProductOrigin fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ProductOrigin");
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static ProductOrigin getById(Integer id) {
        return Arrays.stream(ProductOrigin.values()).filter(productOrigin -> productOrigin.getId().equals(id)).findFirst().orElse(null);
    }
}
