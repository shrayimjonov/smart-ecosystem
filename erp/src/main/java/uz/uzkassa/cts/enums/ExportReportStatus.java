package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExportReportStatus {
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tasdiqlanish jarayonida";
                case uz_cyrillic -> "Тасдиқланиш жараёнида";
                default -> "В ожидании";
            };
        }
    },
    ERROR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xatolik";
                case uz_cyrillic -> "Xатолик";
                default -> "Ошибка";
            };
        }
    },
    COMPLETED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bajarildi";
                case uz_cyrillic -> "Бажарилди";
                default -> "Завершен";
            };
        }
    };

    private final String name;
    private final String code;

    ExportReportStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ExportReportStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ReportStatus enum value: " + code));
    }

    @JsonCreator
    public static ExportReportStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ReportStatus");
    }
}
