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
public enum MarkOrderCreateMethodType implements Serializable {
    SELF_MADE {
        @Override
        public String getName(LanguageEnum language) {
            return "Самостоятельно";
        }
    },
    CEM {
        @Override
        public String getName(LanguageEnum language) {
            return "ЦЭМ";
        }
    },
    CM {
        @Override
        public String getName(LanguageEnum language) {
            return "Контрактное производство";
        }
    },
    CL {
        @Override
        public String getName(LanguageEnum language) {
            return "Логистический склад";
        }
    },
    CA {
        @Override
        public String getName(LanguageEnum language) {
            return "Комиссионная площадка";
        }
    },
    ;

    private final String name;
    private final String code;

    MarkOrderCreateMethodType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static MarkOrderCreateMethodType forValue(Map<String, String> value) {
        return MarkOrderCreateMethodType.valueOf(value.get("code"));
    }
}
