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

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum Prefix implements Serializable {
    ORDER {
        public String getName(LanguageEnum language) {
            return "ЗA";
        }
    },
    RESERVATION {
        public String getName(LanguageEnum language) {
            return "БР";
        }
    },
    STOCK_IN {
        @Override
        public String getName(LanguageEnum language) {
            return "ПР";
        }
    },
    STOCK_OUT {
        @Override
        public String getName(LanguageEnum language) {
            return "ОТ";
        }
    },
    STOCK_TRANSFER {
        @Override
        public String getName(LanguageEnum language) {
            return "ПЕР";
        }
    },
    STOCK_ADJUSTMENT {
        @Override
        public String getName(LanguageEnum language) {
            return "КОР";
        }
    },
    SHIFT {
        @Override
        public String getName(LanguageEnum language) {
            return "СМ";
        }
    };

    private final String name;
    private final String code;

    Prefix() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static Prefix get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Prefix enum value: " + code));
    }

    @JsonCreator
    public static Prefix fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for Prefix");
    }
}
