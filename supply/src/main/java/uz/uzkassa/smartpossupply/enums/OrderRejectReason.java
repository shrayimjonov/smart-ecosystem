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
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum OrderRejectReason implements Serializable {

    NO_DECISION_MAKER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qaror qabul qiluvchi shaxs mavjud emas";
                case cyrillic -> "Қарор қабул қилувчи шахс мавжуд эмас";
                default -> "Нет ЛПР на месте";
            };
        }
    },
    LOTS_OF_GOODS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarlar ko`p";
                case cyrillic -> "Товарлар кўп";
                default -> "Много товара";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yopiq T.T";
                case cyrillic -> "Ёпиқ Т.Т";
                default -> "Закрыта Т.Т";
            };
        }
    },
    OTHER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Boshqa";
                case cyrillic -> "Бошқа";
                default -> "Другoe";
            };
        }
    };


    private final String name;
    private final String code;

    OrderRejectReason() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static OrderRejectReason forValue(Map<String, String> value) {
        return OrderRejectReason.valueOf(value.get("code"));
    }
}
