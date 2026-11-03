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
public enum MarkOrderReleaseMethodType implements Serializable {
    IMPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Oʻzbekistonga olib kirilgan (Import)";
                case cyrillic -> "Ўзбекистонга олиб кирилган (Импорт)";
                default -> "Ввезен в Узбекистан (Импорт)";
            };
        }
    },
    REMAINS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qoldiqlar markirovkasi";
                case cyrillic -> "Қолдиқлар маркировкаси";
                default -> "Маркировка остатков";
            };
        }
    },
    COMMISSION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Komissiya";
                case cyrillic -> "Комиссия";
                default -> "Комиссия";
            };
        }
    },
    PRODUCTION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ishlab chiqarilishi Oʻzbekistonda";
                case cyrillic -> "Ишлаб чиқарилиши Ўзбекистонда";
                default -> "Производство в Узбекистане";
            };
        }
    };

    private final String name;
    private final String code;

    MarkOrderReleaseMethodType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static MarkOrderReleaseMethodType forValue(Map<String, String> value) {
        return MarkOrderReleaseMethodType.valueOf(value.get("code"));
    }
}
