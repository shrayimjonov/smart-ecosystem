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
public enum BufferStatus implements Serializable {
    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            return "Буфер КМ находится в ожидании";
        }
    },
    ACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return "Буфер создан";
        }
    },

    EXHAUSTED {
        @Override
        public String getName(LanguageEnum language) {
            return "Буфер и пулы не содержат больше кодов";
        }
    },
    REJECTED {
        @Override
        public String getName(LanguageEnum language) {
            return "Буфер более не доступен для работы";
        }
    },
    CLOSED {
        @Override
        public String getName(LanguageEnum language) {
            return "Буфер закрыт";
        }
    };

    private final String name;
    private final String code;

    BufferStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static BufferStatus forValue(Map<String, String> value) {
        return BufferStatus.valueOf(value.get("code"));
    }
}
