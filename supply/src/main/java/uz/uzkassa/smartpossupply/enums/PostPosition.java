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
 * Date: 19.01.2023 19:43
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum PostPosition implements Serializable {

    CENTER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Markaz";
                case cyrillic -> "Марказ";
                default -> "Центр";
            };
        }
    },
    RIGHT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O'ng";
                case cyrillic -> "O'ng";
                default -> "Право";
            };
        }
    },
    LEFT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chap";
                case cyrillic -> "Чап";
                default -> "Левый";
            };
        }
    },
    BOTTOM {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ost";
                case cyrillic -> "Ост";
                default -> "Низ";
            };
        }
    },
    TOP {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuqori";
                case cyrillic -> "Юқори";
                default -> "Верх";
            };
        }
    };

    private final String name;
    private final String code;

    PostPosition() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static PostPosition forValue(Map<String, String> value) {
        return PostPosition.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
