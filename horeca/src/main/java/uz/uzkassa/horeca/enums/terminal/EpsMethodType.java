package uz.uzkassa.horeca.enums.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.horeca.enums.LanguageEnum;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum EpsMethodType implements Serializable {
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

    EpsMethodType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageEnum.ru);
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static EpsMethodType forValue(Map<String, String> value) {
        return EpsMethodType.valueOf(value.get("code"));
    }
}
