package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReportStatus implements Serializable {
    PENDING {
        @Override
        public String getName(String language) {
            switch (language) {
                case "uz":
                    return "Tasdiqlanish jarayonida";
                case "cyrillic":
                    return "Тасдиқланиш жараёнида";
                default:
                    return "В ожидании";
            }
        }
    },
    ERROR {
        @Override
        public String getName(String language) {
            switch (language) {
                case "uz":
                    return "Xatolik";
                case "cyrillic":
                    return "Xатолик";
                default:
                    return "Ошибка";
            }
        }
    },
    COMPLETED {
        @Override
        public String getName(String language) {
            switch (language) {
                case "uz":
                    return "Bajarildi";
                case "cyrillic":
                    return "Бажарилди";
                default:
                    return "Завершен";
            }
        }
    };

    private final String name;
    private final String code;

    ReportStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(String language);

    public String getName() {
        return getName(SecurityUtils.getCurrentLocale());
    }
}
