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
public enum WhiteListAction {

    FM_REGISTER_BALANCE_CHECK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Fiskal modul registratsiya balansi tekshiruvi";
                case uz_cyrillic -> "Фискал модул регистрация баланси текшируви";
                default -> "Проверка баланса регистра фискального модуля";
            };
        }
    },
    FM_ONE_TIME_OPERATION_REPLACEMENT_BURNT_OUT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Замена ФМ (Сгорел) - Единоразовые операции ФМ";
                case uz_cyrillic -> "Замена ФМ (Сгорел) - Единоразовые операции ФМ";
                default -> "Замена ФМ (Сгорел) - Единоразовые операции ФМ";
            };
        }
    },
    REMOVE_DEVICE_WITH_LOSS {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Снятие ККМ с налоговой (C утери)";
                case uz_cyrillic -> "Снятие ККМ с налоговой (C утери)";
                default -> "Снятие ККМ с налоговой (C утери)";
            };
        }
    },
    CONNECT_MERCHANT_ACQUIRING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Подключение торгового эквайринга";
                case uz_cyrillic -> "Подключение торгового эквайринга";
                default -> "Подключение торгового эквайринга";
            };
        }
    },
    REMOVE_DEVICE_WITHOUT_LOSS {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Снятие ККМ с налоговой (Без утери)";
                case uz_cyrillic -> "Снятие ККМ с налоговой (Без утери)";
                default -> "Снятие ККМ с налоговой (Без утери)";
            };
        }
    },
    REGISTER_DEVICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Регистрация ККМ в налоговой";
                case uz_cyrillic -> "Регистрация ККМ в налоговой";
                default -> "Регистрация ККМ в налоговой";
            };
        }
    },
    FM_ONE_TIME_OPERATION_REPLACEMENT_LOST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Замена ФМ (Утеря) - Единоразовые операции ФМ";
                case uz_cyrillic -> "Замена ФМ (Утеря) - Единоразовые операции ФМ";
                default -> "Замена ФМ (Утеря) - Единоразовые операции ФМ";
            };
        }
    };

    private final String code;
    private final String name;

    WhiteListAction() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static WhiteListAction get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown WhiteListAction enum value: " + code));
    }

    @JsonCreator
    public static WhiteListAction fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for WhiteListAction");
    }
}
