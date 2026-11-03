package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum EntityTypeEnum implements Serializable {
    COMPANY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kompaniya";
                case cyrillic -> "Компания";
                case en -> "Company";
                default -> "Компания";
            };
        }
    },
    COMPANY_BANK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kompaniya bank";
                case cyrillic -> "Компания банк";
                case en -> "Company bank";
                default -> "Банк компании";
            };
        }
    },
    COMPANY_SETTINGS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kompaniya sozlamalari";
                case cyrillic -> "Компания созламалари";
                case en -> "Company settings";
                default -> "Настройки компании";
            };
        }
    },
    BRANCH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Филиал";
                case cyrillic -> "Компания";
                case en -> "Branch";
                default -> "Филиал";
            };
        }
    },
    DEPARTMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bo'lim";
                case cyrillic -> "Бўлим";
                case en -> "Department";
                default -> "Отдел";
            };
        }
    },
    RESERVATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bron qilish";
                case cyrillic -> "Брон қилиш";
                case en -> "Reservation";
                default -> "Бронирование";
            };
        }
    },
    RECEIPT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chek";
                case cyrillic -> "Чек";
                case en -> "Receipt";
                default -> "Чек";
            };
        }
    },
    PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovar";
                case cyrillic -> "Товар";
                case en -> "Product";
                default -> "Товар";
            };
        }
    },
    TECH_CARD {
        @Override
        public String getName(LanguageEnum language) {
            return "Тех. карты";
        }
    },
    SEMI_FINISHED_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return "Полуфабрикаты";
        }
    },
    POST {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz, en -> "Post";
                default -> "Пост";
            };
        }
    },
    ORDER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Buyurtma";
                case cyrillic -> "Буюртма";
                case en -> "Order";
                default -> "Заказ";
            };
        }
    },
    MENU {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Menyu";
                case cyrillic -> "Меню";
                case en -> "Menu";
                default -> "Меню";
            };
        }
    },
    HALL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Zal";
                case cyrillic -> "Зал";
                case en -> "Hall";
                default -> "Зал";
            };
        }
    },
    HALL_ITEM {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Zal buyumlari";
                case cyrillic -> "Зал буюмлари";
                case en -> "Hall item";
                default -> "Предметы зала";
            };
        }
    },
    HALL_EMPLOYEE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Zal ishchilari";
                case cyrillic -> "Зал ишчилари";
                case en -> "Hall employee";
                default -> "Сотрудник зала";
            };
        }
    },
    USER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Foydalanuvchi";
                case cyrillic -> "Фойдаланувчи";
                case en -> "User";
                default -> "Пользователь";
            };
        }
    },
    STOCK_IN {
        @Override
        public String getName(LanguageEnum language) {
            return "Приход";
        }
    },
    STOCK_IN_ITEM {
        @Override
        public String getName(LanguageEnum language) {
            return "Приход";
        }
    },
    STOCK_OUT {
        @Override
        public String getName(LanguageEnum language) {
            return "Отгрузка";
        }
    },
    STOCK_RETURN {
        @Override
        public String getName(LanguageEnum language) {
            return "Возврат";
        }
    },
    STOCK_TRANSFER {
        @Override
        public String getName(LanguageEnum language) {
            return "Передача";
        }
    },
    STOCK_ADJUSTMENT {
        @Override
        public String getName(LanguageEnum language) {
            return "Корректировка";
        }
    },
    WAREHOUSE {
        @Override
        public String getName(LanguageEnum language) {
            return "Склад";
        }
    },
    FACTURA {
        @Override
        public String getName(LanguageEnum language) {
            return "Счет-фактура";
        }
    },
    FACTURA_DETAIL {
        @Override
        public String getName(LanguageEnum language) {
            return "FACTURA_DETAIL";
        }
    },
    CONTRACT {
        @Override
        public String getName(LanguageEnum language) {
            return "Договоры";
        }
    },
    MENU_ITEM {
        @Override
        public String getName(LanguageEnum language) {
            return "Menu Item";
        }
    },
    BILL {
        @Override
        public String getName(LanguageEnum language) {
            return "Bill";
        }
    };

    private final String code;
    private final String name;

    EntityTypeEnum() {
        this.code = this.name();
        this.name = getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static EntityTypeEnum get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown EntityTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static EntityTypeEnum fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for EntityTypeEnum");
    }
}
