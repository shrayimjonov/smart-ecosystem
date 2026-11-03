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
public enum Prefix implements Serializable {
    STOCK_IN {
        @Override
        public String getName(LanguageEnum language) {
            return "ПР";
        }
    },
    STOCK_OUT {
        @Override
        public String getName(LanguageEnum language) {
            return "ОТ";
        }
    },
    ORDER {
        @Override
        public String getName(LanguageEnum language) {
            return "ЗА";
        }
    },
    MARK_ORDER {
        @Override
        public String getName(LanguageEnum language) {
            return "Заказ-КМ";
        }
    },
    AGGREGATION_ORDER {
        @Override
        public String getName(LanguageEnum language) {
            return "Заказ-КA";
        }
    },
    PARTY {
        @Override
        public String getName(LanguageEnum language) {
            return "Партия";
        }
    },
    INVOICE {
        @Override
        public String getName(LanguageEnum language) {
            return "СФ";
        }
    },
    STOCK_TRANSFER {
        @Override
        public String getName(LanguageEnum language) {
            return "ПЕР";
        }
    },
    STOCK_ADJUSTMENT {
        @Override
        public String getName(LanguageEnum language) {
            return "КОР";
        }
    },
    PLAN_ROUTE {
        @Override
        public String getName(LanguageEnum language) {
            return "MП";
        }
    },
    PLAN_ROUTE_TEMPLATE {
        @Override
        public String getName(LanguageEnum language) {
            return "ШМ";
        }
    },
    TRANSACTION {
        @Override
        public String getName(LanguageEnum language) {
            return "ТР";
        }
    },
    PRICE_LIST {
        @Override
        public String getName(LanguageEnum language) {
            return "ПЛ";
        }
    },
    AIC {
        @Override
        public String getName(LanguageEnum language) {
            return "АИК";
        }
    },
    STOCK_RETURN {
        @Override
        public String getName(LanguageEnum language) {
            return "BT";
        }
    },
    CASH_FLOW {
        @Override
        public String getName(LanguageEnum language) {
            return "КО";
        }
    },
    FINANCIAL_OPERATION {
        @Override
        public String getName(LanguageEnum language) {
            return "ФО";
        }
    },
    WEB_KASSA {
        @Override
        public String getName(LanguageEnum language) {
            return "ВК";
        }
    },
    ACCEPTANCE_TRANSFER_ACT {
        @Override
        public String getName(LanguageEnum language) {
            return "SHIP";
        }
    };

    private final String name;
    private final String code;

    Prefix() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static Prefix forValue(Map<String, String> value) {
        return Prefix.valueOf(value.get("code"));
    }
}
