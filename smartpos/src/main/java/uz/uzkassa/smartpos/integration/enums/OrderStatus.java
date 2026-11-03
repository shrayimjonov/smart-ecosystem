package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrderStatus implements Serializable {
    NEW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yangi";
                case cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    DRAFT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qoralama";
                case cyrillic -> "Қоралама";
                default -> "Черновик";
            };
        }
    },
    IN_PROGRESS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Jarayonda";
                case cyrillic -> "Жараёнда";
                default -> "В процессе";
            };
        }
    },
    APPROVED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tasdiqlangan";
                case cyrillic -> "Тасдиқланган";
                default -> "Одобрено";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yetkazib beruvchi tomonidan rad etilgan";
                case cyrillic -> "Етказиб берувчи томонидан рад этилган";
                default -> "Отклонено поставщиком";
            };
        }
    },
    COMPLETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yakunlandi";
                case cyrillic -> "Якунланди";
                default -> "Выполнен";
            };
        }
    },
    CANCELLED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xaridor tomonidan bekor qilindi";
                case cyrillic -> "Харидор томонидан бекор қилинди";
                default -> "Отменено покупателем";
            };
        }
    },
    DELETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O`chirilgan";
                case cyrillic -> "Ўчирилган";
                default -> "Удалено";
            };
        }
    };

    private final String name;
    private final String code;

    OrderStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static OrderStatus forValue(Map<String, String> value) {
        return OrderStatus.valueOf(value.get("code"));
    }
}
