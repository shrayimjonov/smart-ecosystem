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
public enum LeaveRequestReasonAbbreviation {
    HOLIDAY {
        @Override
        public String getName(Language language) {
            return "В";
        }
    },
    VACATION {
        @Override
        public String getName(Language language) {
            return "ОТ";
        }
    },
    STUDY {
        @Override
        public String getName(Language language) {
            return "ОУ";
        }
    },
    PARENTAL {
        @Override
        public String getName(Language language) {
            return "ОР";
        }
    },
    UNPAID {
        @Override
        public String getName(Language language) {
            return "БС";
        }
    },
    MEDICAL {
        @Override
        public String getName(Language language) {
            return "БЛ";
        }
    },
    BUSINESS_TRIP {
        @Override
        public String getName(Language language) {
            return "К";
        }
    },
    ABSENCE {
        @Override
        public String getName(Language language) {
            return "П";
        }
    };

    private final String name;
    private final String code;

    LeaveRequestReasonAbbreviation() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static LeaveRequestReasonAbbreviation get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown LeaveRequestReasonAbbreviation enum value: " + code));
    }

    @JsonCreator
    public static LeaveRequestReasonAbbreviation fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for LeaveRequestReasonAbbreviation");
    }
}
