package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
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
    UN_LOADING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuk tushirishda";
                case cyrillic -> "Юк туширишда";
                default -> "На отгрузке";
            };
        }
    },
    LOADING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuklanmoqda";
                case cyrillic -> "Юкланмоқда";
                default -> "На погрузке";
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
                case uz -> "Rad etilgan";
                case cyrillic -> "Рад этилган";
                default -> "Отклонено";
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
    },
    IN_DELIVERY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yetkazish jarayonida";
                case cyrillic -> "Етказиш жараёнида";
                default -> "В доставке";
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
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static OrderStatus forValue(Map<String, String> value) {
        return OrderStatus.valueOf(value.get("code"));
    }

    public static List<String> toEnumCodes(List<String> statusStrings) {
        if (statusStrings == null || statusStrings.isEmpty()) {
            return Collections.emptyList();
        }

        return statusStrings.stream()
                .map(OrderStatus::valueOf)
                .map(Enum::name)
                .toList();
    }
}
