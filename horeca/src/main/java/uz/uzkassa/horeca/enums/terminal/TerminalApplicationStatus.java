package uz.uzkassa.horeca.enums.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.horeca.enums.LanguageEnum;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum TerminalApplicationStatus {
    NEW {
        @Override
        public String getName(LanguageEnum language) {
            if (LanguageEnum.ru == language) {
                return "Новый";
            }
            return "Yangi";
        }
    },
    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            if (LanguageEnum.ru == language) {
                return "В ожидании";
            }
            return "Kutilmoqda";
        }
    },
    APPROVED {
        @Override
        public String getName(LanguageEnum language) {
            if (LanguageEnum.ru == language) {
                return "Одобренный";
            }
            return "Tasdiqlangan";
        }
    };

    private final String name;
    private final String code;

    TerminalApplicationStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageEnum.ru);
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static TerminalApplicationStatus forCodeValue(Map<String, String> value) {
        return TerminalApplicationStatus.valueOf(value.get("code"));
    }
}
