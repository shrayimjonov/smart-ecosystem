package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum MarkStatus implements Serializable {
    EMITTED {
        @Override
        public String getName(LanguageEnum language) {
            return "Эмитирован";
        }
    },
    APPLIED {
        @Override
        public String getName(LanguageEnum language) {
            return "Нанесен";
        }
    },
    INTRODUCED {
        @Override
        public String getName(LanguageEnum language) {
            return "Введен в оборот";
        }
    },
    WRITTEN_OFF {
        @Override
        public String getName(LanguageEnum language) {
            return "Утилизирован";
        }
    },
    WITHDRAWN {
        @Override
        public String getName(LanguageEnum language) {
            return "Выведен из оборота";
        }
    },
    RESERVED_NOT_USED {
        @Override
        public String getName(LanguageEnum language) {
            return "Зарезервировано. Не использовать";
        }
    },
    INTRODUCED_RETURNED {
        @Override
        public String getName(LanguageEnum language) {
            return "Возвращен в оборот";
        }
    },
    DISAGGREGATED {
        @Override
        public String getName(LanguageEnum language) {
            return "Дезагрегирован";
        }
    },
    WAIT_SHIPMENT {
        @Override
        public String getName(LanguageEnum language) {
            return "Ожидает отгрузки";
        }
    },
    EXPORTED {
        @Override
        public String getName(LanguageEnum language) {
            return "Используется для документов эспорта";
        }
    },
    APPLIED_NOT_PAID {
        @Override
        public String getName(LanguageEnum language) {
            return "Нанесен, не оплачен";
        }
    },
    ERROR {
        @Override
        public String getName(LanguageEnum language) {
            return "Ошибка";
        }
    };

    private final String name;
    private final String code;

    MarkStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static MarkStatus forValue(Map<String, String> value) {
        return MarkStatus.valueOf(value.get("code"));
    }
}
