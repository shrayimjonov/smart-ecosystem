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
public enum AggregationOrderStatus implements Serializable {
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
    IN_PROGRESS {
        @Override
        public String getName(LanguageEnum language) {
            return "В процессе";
        }
    },
    CREATED {
        @Override
        public String getName(LanguageEnum language) {
            return "Завершено";
        }
    };

    private final String name;
    private final String code;

    AggregationOrderStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AggregationOrderStatus forValue(Map<String, String> value) {
        return AggregationOrderStatus.valueOf(value.get("code"));
    }
}
