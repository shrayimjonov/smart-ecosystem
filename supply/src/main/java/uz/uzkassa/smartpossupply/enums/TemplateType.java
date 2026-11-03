package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Getter
public enum TemplateType implements Serializable {

    COMPLETE_STOCK_IN(List.of(TemplateTypeAlias.TRANSACTION_NUMBER, TemplateTypeAlias.CREATOR)) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kirim yakunlandi";
                case cyrillic -> "Кирим якунланди";
                default -> "Завершение прихода";
            };
        }
    },
    COMPLETE_STOCK_OUT(List.of(TemplateTypeAlias.TRANSACTION_NUMBER, TemplateTypeAlias.CREATOR)) {
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chiqim yakunlandi";
                case cyrillic -> "Чиқим якунланди";
                default -> "Завершение отгрузки";
            };
        }
    },
    COMPLETE_STOCK_TRANSFER(List.of(TemplateTypeAlias.TRANSACTION_NUMBER, TemplateTypeAlias.CREATOR)) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O‘tkazma bajarildi";
                case cyrillic -> "Ўтказма бажарилди";
                default -> "Выполнена передача";
            };
        }
    },
    OUT_OF_STOCK(List.of(TemplateTypeAlias.PRODUCT_NAME, TemplateTypeAlias.BARCODE, TemplateTypeAlias.BRANCH_NAME, TemplateTypeAlias.WAREHOUSE_NAME)) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovar tugadi";
                case cyrillic -> "Товар тугади";
                default -> "Товар закончился на складе";
            };
        }
    },
    ORDER_FROM_AGENT(List.of(TemplateTypeAlias.AGENT, TemplateTypeAlias.ORDER_NUMBER, TemplateTypeAlias.CLIENT_NAME)) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agent buyurtmasi";
                case cyrillic -> "Агент буюртмаси";
                default -> "Заказ от агента";
            };
        }
    },
    ORDER_FROM_MARKETPLACE(List.of(TemplateTypeAlias.ORDER_NUMBER, TemplateTypeAlias.CLIENT_NAME, TemplateTypeAlias.CLIENT_TIN)) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Marketplace buyurtmasi";
                case cyrillic -> "Маркетплейс буюртмаси";
                default -> "Заказ от маркетплейса";
            };
        }
    },
    ORDER_DELIVERED(List.of(TemplateTypeAlias.TRANSACTION_NUMBER)) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yetkazib berildi";
                case cyrillic -> "Етказиб берилди";
                default -> "Доставка выполнена";
            };
        }
    },
    SUBSCRIPTION_PAYMENT_REMINDER(List.of(TemplateTypeAlias.SUBSCRIPTION_SERVICES)) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xizmat muddati tugash arafasida";
                case cyrillic -> "Хизмат муддати тугаш арафасида";
                default -> "Срок действия услуги истекает";
            };
        }
    };

    private final String name;
    private final String code;
    private final List<TemplateTypeAlias> types;

    TemplateType(List<TemplateTypeAlias> types) {
        this.types = types;
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static TemplateType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown TemplateType enum value: " + code));
    }

    @JsonCreator
    public static TemplateType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for TemplateType");
    }

}
