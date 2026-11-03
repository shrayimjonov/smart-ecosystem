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
 * Date: 01.12.2023
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum PlanRouteAttachmentType implements Serializable {
    INSIDE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Javondan rasm";
                case cyrillic -> "Жавондан расм";
                default -> "Фотография с полок";
            };
        }
    },
    OUTSIDE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tashqaridan rasm";
                case cyrillic -> "Ташқаридан расм";
                default -> "Фотография снаружи";
            };
        }
    };

    private final String name;
    private final String code;

    PlanRouteAttachmentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static PlanRouteAttachmentType forValue(Map<String, String> value) {
        return PlanRouteAttachmentType.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
