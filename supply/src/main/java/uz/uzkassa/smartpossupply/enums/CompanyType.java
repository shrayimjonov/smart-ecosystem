package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum CompanyType implements Serializable {
    MANUFACTURER(1) {
        @Override
        public String getName(LanguageEnum language) {
            return "Производитель";
        }
    },
    IMPORTER(2) {
        @Override
        public String getName(LanguageEnum language) {
            return "Импортёр";
        }
    },
    WHOLESALE(3) {
        @Override
        public String getName(LanguageEnum language) {
            return "Оптовые базы";
        }
    },

    DISTRIBUTION(3) {
        @Override
        public String getName(LanguageEnum language) {
            return "Дистрибьютор";
        }
    },
    RETAIL(4) {
        @Override
        public String getName(LanguageEnum language) {
            return "Розничная торговля";
        }
    },
    AGENT(5) {
        @Override
        public String getName(LanguageEnum language) {
            return "Посредник";
        }
    },
    SERVICE(6) {
        @Override
        public String getName(LanguageEnum language) {
            return "Поставщик услуг";
        }
    },
    OTHER(7) {
        @Override
        public String getName(LanguageEnum language) {
            return "Другие";
        }
    };


    private final String name;
    private final String code;
    private final Integer id;

    CompanyType(Integer id) {
        this.code = this.name();
        this.name = this.getName();
        this.id = id;
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static CompanyType forValue(Map<String, String> value) {
        return CompanyType.valueOf(value.get("code"));
    }
}
