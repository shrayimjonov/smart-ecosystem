package uz.uzkassa.cts.enums.apay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.cts.enums.Language;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum APayApplicationStatus {
    NEW {
        @Override
        public String getName(Language language) {
            if (language == Language.ru) {
                return "Новый";
            }
            return "Yangi";
        }
    },
    PENDING {
        @Override
        public String getName(Language language) {
            if (language == Language.ru) {
                return "В ожидании";
            }
            return "Kutilmoqda";
        }
    },
    APPROVED {
        @Override
        public String getName(Language language) {
            if (language == Language.ru) {
                return "Одобренный";
            }
            return "Tasdiqlangan";
        }
    };

    private final String name;
    private final String code;

    APayApplicationStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(Language.ru);
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static APayApplicationStatus forValue(Map<String, String> value) {
        return APayApplicationStatus.valueOf(value.get("code"));
    }
}
