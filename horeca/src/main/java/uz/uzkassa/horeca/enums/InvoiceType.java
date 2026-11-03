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

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum InvoiceType implements Serializable {
    INCOMING {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Kiruvchi hisob-faktura";
                case cyrillic:
                    return "Кирувчи ҳисоб-фактура";
                default:
                    return "Полученные счет-фактуры";
            }
        }
    },
    OUTGOING {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Chiquvchi hisob-faktura";
                case cyrillic:
                    return "Чиқувчи ҳисоб-фактура";
                default:
                    return "Отправленные cчет-фактуры";
            }
        }
    };

    private final String name;
    private final String code;

    InvoiceType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static InvoiceType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown InvoiceType enum value: " + code));
    }

    @JsonCreator
    public static InvoiceType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for InvoiceType");
    }
}
