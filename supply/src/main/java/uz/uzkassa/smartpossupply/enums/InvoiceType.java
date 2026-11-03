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
public enum InvoiceType implements Serializable {
    INCOMING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kiruvchi hisob-faktura";
                case cyrillic -> "Кирувчи ҳисоб-фактура";
                default -> "Полученные счет-фактуры";
            };
        }
    },
    OUTGOING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chiquvchi hisob-faktura";
                case cyrillic -> "Чиқувчи ҳисоб-фактура";
                default -> "Отправленные cчет-фактуры";
            };
        }
    };

    private final String name;
    private final String code;

    InvoiceType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static InvoiceType forValue(Map<String, String> value) {
        return InvoiceType.valueOf(value.get("code"));
    }
}
