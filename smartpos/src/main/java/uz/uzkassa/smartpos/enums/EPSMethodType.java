package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EPSMethodType implements Serializable {
    SHOW_QR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "QR kodni ko'rsatish";
                case cyrillic -> "QR кодни кўрсатиш";
                default -> "Показать QR-код";
            };
        }
    },

    SCAN_QR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "QR kodni skanerlash";
                case cyrillic -> "QR кодни сканерлаш";
                default -> "Сканируйте QR-код";
            };
        }
    },

    SHOW_AND_SCAN_QR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "QR kodni ko'rsatish va skanerlash";
                case cyrillic -> "QR кодни кўрсатиш ва сканерлаш";
                default -> "Показать и сканируйте QR-код";
            };
        }
    };

    private final String name;
    private final String code;

    EPSMethodType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static EPSMethodType forValue(Map<String, String> value) {
        return EPSMethodType.valueOf(value.get("code"));
    }
}
