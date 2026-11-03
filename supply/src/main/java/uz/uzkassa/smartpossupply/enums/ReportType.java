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
 * Date: 20.12.2023
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum ReportType implements Serializable {

    BY_SERIAL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Seriya bo'yich xisobot";
                case cyrillic -> "Серия бўйич хисобот";
                default -> "Отчет по сериям";
            };
        }
    },
    BY_CONTRACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Shartnoma bo'yicha xisobot";
                case cyrillic -> "Шартнома бўйича хисобот";
                default -> "Отчет по контрактам";
            };
        }
    },
    BY_COMITTENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Komittent bo'yicha xisobot";
                case cyrillic -> "Комитент бўйича хисобот";
                default -> "Отчет по комитента";
            };
        }
    },
    BY_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Umumiy qoldiq";
                case cyrillic -> "Умумий қолдиқ";
                default -> "Общие остатки";
            };
        }
    };

    private final String name;
    private final String code;

    ReportType() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ReportType forValue(Map<String, String> value) {
        return ReportType.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
