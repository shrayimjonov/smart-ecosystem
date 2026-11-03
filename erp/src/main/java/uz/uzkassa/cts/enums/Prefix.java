package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum Prefix implements Serializable {
    ORDER {
        public String getName(Language language) {
            return "ЗA";
        }
    },
    STOCK_IN {
        @Override
        public String getName(Language language) {
            return "ПР";
        }
    },
    STOCK_OUT {
        @Override
        public String getName(Language language) {
            return "ОТ";
        }
    },
    STOCK_RETURN {
        @Override
        public String getName(Language language) {
            return "BT";
        }
    },
    STOCK_TRANSFER {
        @Override
        public String getName(Language language) {
            return "ПЕР";
        }
    },
    STOCK_ADJUSTMENT {
        @Override
        public String getName(Language language) {
            return "КОР";
        }
    },
    VACANCY_APPLICATION {
        @Override
        public String getName(Language language) {
            return "З";
        }
    },
    VACANCY {
        @Override
        public String getName(Language language) {
            return "ВА";
        }
    },
    CANDIDATE {
        @Override
        public String getName(Language language) {
            return "КА";
        }
    },
    LEAVE_REQUEST {
        @Override
        public String getName(Language language) {
            return "ЗНО"; // ЗНО = Запрос На Отпуск
        }
    },
    EVENT_REQUEST {
        @Override
        public String getName(Language language) {
            return "ЗНС"; // ЗНС = Запрос На Событие
        }
    },
    BENEFIT_REQUEST {
        @Override
        public String getName(Language language) {
            return "ЗНП"; // ЗНП = Запрос На Пособие
        }
    },
    EQUIPMENT_REQUEST {
        @Override
        public String getName(Language language) {
            return "ЗНИ"; // Запрос На Инвентарь
        }
    },
    REPAIR_TICKET {
        @Override
        public String getName(Language language) {
            return "ТИК";
        }
    },
    EMPLOYEE {
        @Override
        public String getName(Language language) {
            return "EMP";
        }
    };


    private final String name;
    private final String code;

    Prefix() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static Prefix get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(prefix -> prefix.getCode().equalsIgnoreCase(code))
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
