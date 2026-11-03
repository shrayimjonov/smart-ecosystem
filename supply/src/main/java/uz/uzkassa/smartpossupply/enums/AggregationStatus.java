package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum AggregationStatus implements Serializable {
    DRAFT {
        @Override
        public String getName(LanguageEnum language) {
            return "Создано";
        }
    },
    ERROR {
        @Override
        public String getName(LanguageEnum language) {
            return "Ошибка";
        }
    },
    EMPTY {
        @Override
        public String getName(LanguageEnum language) {
            return "Пустой";
        }
    },
    CREATED {
        @Override
        public String getName(LanguageEnum language) {
            return "Агрегировано";
        }
    },
    UTILIZED {
        @Override
        public String getName(LanguageEnum language) {
            return "Утилизировано";
        }
    },
    RETURNED {
        @Override
        public String getName(LanguageEnum language) {
            return "Возвращен";
        }
    },
    SHIPPED {
        @Override
        public String getName(LanguageEnum language) {
            return "Отгружено";
        }
    },
    IN_GROUP {
        @Override
        public String getName(LanguageEnum language) {
            return "Внутри группы аггрегатции";
        }
    },
    IN_STOCK {
        @Override
        public String getName(LanguageEnum language) {
            return "ВнутриСкладе";
        }
    },
    IN_SHIPMENT {
        @Override
        public String getName(LanguageEnum language) {
            return "Внутри отгрузки";
        }
    };

    private final String name;
    private final String code;

    AggregationStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AggregationStatus forValue(Map<String, String> value) {
        return AggregationStatus.valueOf(value.get("code"));
    }
}
