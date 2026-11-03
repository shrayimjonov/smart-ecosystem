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
public enum CisType implements Serializable {
    UNIT {
        @Override
        public String getName(LanguageEnum language) {
            return "Единица товара";
        }
    },
    GROUP {
        @Override
        public String getName(LanguageEnum language) {
            return "Групповая потребительская упаковка";
        }
    },
    LEVEL1{
        @Override
        public String getName(LanguageEnum language) {
            return "Блок";
        }
    },
    LEVEL2{
        @Override
        public String getName(LanguageEnum language) {
            return "Коробка";
        }
    },
    LEVEL3{
        @Override
        public String getName(LanguageEnum language) {
            return "Паллета";
        }
    }
    ;

    private final String name;
    private final String code;

    CisType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static CisType forValue(Map<String, String> value) {
        return CisType.valueOf(value.get("code"));
    }
}
