package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.12.2022 19:01
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum FileSizeType implements Serializable {
    LOW {
        @Override
        public String getName(LanguageEnum language) {
            return "LOW";
        }
    },
    MEDIUM {
        @Override
        public String getName(LanguageEnum language) {
            return "MEDIUM";
        }
    },
    HIGH {
        @Override
        public String getName(LanguageEnum language) {
            return "HIGH";
        }
    },
    ORIGINAL {
        @Override
        public String getName(LanguageEnum language) {
            return "ORIGINAL";
        }
    };

    private final String name;
    private final String code;

    FileSizeType() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static FileSizeType forValue(Map<String, String> value) {
        return FileSizeType.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
