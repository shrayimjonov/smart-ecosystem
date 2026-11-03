package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.12.2022 19:01
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PhotoType implements Serializable {
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

    PhotoType() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static PhotoType forValue(Map<String, String> value) {
        return PhotoType.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }
}
