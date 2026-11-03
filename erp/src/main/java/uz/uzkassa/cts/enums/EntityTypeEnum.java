package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum EntityTypeEnum implements Serializable {
    CHECKLIST {
        @Override
        public String getName(Language language) {
            return "CHECKLIST";
        }
    },
    DOCUMENT {
        @Override
        public String getName(Language language) {
            return "DOCUMENT";
        }
    },
    NOTE {
        @Override
        public String getName(Language language) {
            return "NOTE";
        }
    },
    STOCK_IN {
        @Override
        public String getName(Language language) {
            return "STOCK_IN";
        }
    },
    STOCK_OUT {
        @Override
        public String getName(Language language) {
            return "STOCK_OUT";
        }
    },
    STOCK_RETURN {
        @Override
        public String getName(Language language) {
            return "STOCK_RETURN";
        }
    },
    STOCK_TRANSFER {
        @Override
        public String getName(Language language) {
            return "STOCK_TRANSFER";
        }
    },
    STOCK_ADJUSTMENT {
        @Override
        public String getName(Language language) {
            return "STOCK_ADJUSTMENT";
        }
    },
    TASK {
        @Override
        public String getName(Language language) {
            return "TASK";
        }
    },
    CASES {
        @Override
        public String getName(Language language) {
            return "CASES";
        }
    },
    PROJECT {
        @Override
        public String getName(Language language) {
            return "PROJECT";
        }
    },
    EMPLOYEE {
        @Override
        public String getName(Language language) {
            return "EMPLOYEE";
        }
    },
    CUSTOMER {
        @Override
        public String getName(Language language) {
            return "CUSTOMER";
        }
    },
    OFD_APPLICATION {
        @Override
        public String getName(Language language) {
            return "OFD_APPLICATION";
        }
    },
    COMPANY {
        @Override
        public String getName(Language language) {
            return "COMPANY";
        }
    },
    BRANCH {
        @Override
        public String getName(Language language) {
            return "BRANCH";
        }
    },
    DEPARTMENT {
        @Override
        public String getName(Language language) {
            return "DEPARTMENT";
        }
    },
    MAILING_LIST {
        @Override
        public String getName(Language language) {
            return "MAILING_LIST";
        }
    },
    SCHEDULE_MESSAGE {
        @Override
        public String getName(Language language) {
            return "SCHEDULE_MESSAGE";
        }
    },
    DEVICE {
        @Override
        public String getName(Language language) {
            return "DEVICE";
        }
    },
    DEVICE_MODEL {
        @Override
        public String getName(Language language) {
            return "DEVICE_MODEL";
        }
    },
    DEVICE_SUSPEND_HISTORY {
        @Override
        public String getName(Language language) {
            return "DEVICE_SUSPEND_HISTORY";
        }
    },
    USER {
        @Override
        public String getName(Language language) {
            return "USER";
        }
    },
    ROLE {
        @Override
        public String getName(Language language) {
            return "ROLE";
        }
    },
    POSITION {
        @Override
        public String getName(Language language) {
            return "POSITION";
        }
    },
    REGION {
        @Override
        public String getName(Language language) {
            return "REGION";
        }
    },
    DISTRICT {
        @Override
        public String getName(Language language) {
            return "DISTRICT";
        }
    },
    COMPANY_DEVICE {
        @Override
        public String getName(Language language) {
            return "COMPANY_DEVICE";
        }
    },
    OLD_COMPANY_DEVICE {
        @Override
        public String getName(Language language) {
            return "OLD_COMPANY_DEVICE";
        }
    },
    FISCAL_MODULE {
        @Override
        public String getName(Language language) {
            return "FISCAL_MODULE";
        }
    },
    COMPANY_FISCAL_MODULE {
        @Override
        public String getName(Language language) {
            return "COMPANY_FISCAL_MODULE";
        }
    },
    BRANCH_FISCAL_MODULE {
        @Override
        public String getName(Language language) {
            return "BRANCH_FISCAL_MODULE";
        }
    },
    CUSTOMER_FISCAL_MODULE {
        @Override
        public String getName(Language language) {
            return "CUSTOMER_FISCAL_MODULE";
        }
    },
    CUSTOMER_BRANCH_FISCAL_MODULE {
        @Override
        public String getName(Language language) {
            return "CUSTOMER_BRANCH_FISCAL_MODULE";
        }
    },
    BRANCH_DEVICE {
        @Override
        public String getName(Language language) {
            return "BRANCH_DEVICE";
        }
    },
    CUSTOMER_DEVICE {
        @Override
        public String getName(Language language) {
            return "CUSTOMER_DEVICE";
        }
    },
    CUSTOMER_BRANCH_DEVICE {
        @Override
        public String getName(Language language) {
            return "CUSTOMER_BRANCH_DEVICE";
        }
    },
    CUSTOMER_BRANCH {
        @Override
        public String getName(Language language) {
            return "CUSTOMER_BRANCH";
        }
    },
    CANDIDATE {
        @Override
        public String getName(Language language) {
            return "CANDIDATE";
        }
    },
    FISCALIZATION {
        @Override
        public String getName(Language language) {
            return "FISCALIZATION";
        }
    },
    INVOICE {
        @Override
        public String getName(Language language) {
            return "INVOICE";
        }
    },
    ORDER_SPECIFICATION {
        @Override
        public String getName(Language language) {
            return "ORDER_SPECIFICATION";
        }
    },
    SERVICE_SPECIFICATION {
        @Override
        public String getName(Language language) {
            return "SERVICE_SPECIFICATION";
        }
    },
    POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return "POWER_ATTORNEY";
        }
    },
    CONTRACT {
        @Override
        public String getName(Language language) {
            return "CONTRACT";
        }
    },
    SERVICE_INVOICE {
        @Override
        public String getName(Language language) {
            return "SERVICE_INVOICE";
        }
    },
    ONE_TIME_ORDER {
        @Override
        public String getName(Language language) {
            return "ONE_TIME_ORDER";
        }
    },
    ONE_TIME_ORDER_SERVICE {
        @Override
        public String getName(Language language) {
            return "ONE_TIME_ORDER_SERVICE";
        }
    },
    BANK_CLIENT {
        @Override
        public String getName(Language language) {
            return "BANK_CLIENT";
        }
    },
    BANK {
        @Override
        public String getName(Language language) {
            return "BANK";
        }
    },
    REFERENCE {
        @Override
        public String getName(Language language) {
            return "REFERENCE";
        }
    },
    VAT {
        @Override
        public String getName(Language language) {
            return "VAT";
        }
    },
    BONUS {
        @Override
        public String getName(Language language) {
            return "BONUS";
        }
    },
    ACTIVITY_TYPE {
        @Override
        public String getName(Language language) {
            return "ACTIVITY_TYPE";
        }
    },
    ATTENDANCE {
        @Override
        public String getName(Language language) {
            return "ATTENDANCE";
        }
    },
    BENEFIT_REQUEST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Imtiyozlar uchun so'rovlar";
                case uz_cyrillic -> "Имтиёзлар учун сўровлар";
                default -> "Запросы на пособие";
            };
        }
    },
    EQUIPMENT_REQUEST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Uskunalar uchun so'rovlar";
                case uz_cyrillic -> "Ускуналар учун сўровлар";
                default -> "Запросы на оборудование";
            };
        }
    },
    EVENT {
        @Override
        public String getName(Language language) {
            return "EVENT";
        }
    },
    EVENT_REQUEST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tadbir so'rovlari";
                case uz_cyrillic -> "Тадбир сўровлари";
                default -> "Запросы на событие";
            };
        }
    },
    EXPORT_REPORT {
        @Override
        public String getName(Language language) {
            return "EXPORT_REPORT";
        }
    },
    IMPORT_REPORT {
        @Override
        public String getName(Language language) {
            return "IMPORT_REPORT";
        }
    },
    NEIGHBORHOOD {
        @Override
        public String getName(Language language) {
            return "NEIGHBORHOOD";
        }
    },
    CONTRACT_FOR_LEASING {
        @Override
        public String getName(Language language) {
            return "CONTRACT_FOR_LEASING";
        }
    },
    WARRANTY_FOR_LEASING {
        @Override
        public String getName(Language language) {
            return "WARRANTY_FOR_LEASING";
        }
    },
    ACT_OF_TRANSMISSION {
        @Override
        public String getName(Language language) {
            return "ACT_OF_TRANSMISSION";
        }
    },
    ACT_OF_TRANSMISSION_INVOICE {
        @Override
        public String getName(Language language) {
            return "ACT_OF_TRANSMISSION_INVOICE";
        }
    },
    ACT_PAYMENT_SCHEDULE {
        @Override
        public String getName(Language language) {
            return "ACT_PAYMENT_SCHEDULE";
        }
    },
    ACT_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return "ACT_POWER_ATTORNEY";
        }
    },
    LEASING_INVOICE {
        @Override
        public String getName(Language language) {
            return "LEASING_INVOICE";
        }
    },
    TRADE_IN_CONTRACT {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_CONTRACT";
        }
    },
    TRADE_IN_ACT {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_ACT";
        }
    },
    TRADE_IN_INVOICE {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_INVOICE";
        }
    },
    TRADE_IN_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_POWER_ATTORNEY";
        }
    },
    TRADE_IN_COUNTER_CONTRACT {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_COUNTER_CONTRACT";
        }
    },
    TRADE_IN_COUNTER_ACT {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_COUNTER_ACT";
        }
    },
    TRADE_IN_COUNTER_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_COUNTER_POWER_ATTORNEY";
        }
    },
    TRADE_IN_COUNTER_CONTRACT_NOTE {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_COUNTER_CONTRACT_NOTE";
        }
    },
    TRADE_IN_COUNTER_CONTRACT_FILE {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_COUNTER_CONTRACT_FILE";
        }
    },
    SALE_CONTRACT {
        @Override
        public String getName(Language language) {
            return "SALE_CONTRACT";
        }
    },
    SALE_ACT {
        @Override
        public String getName(Language language) {
            return "SALE_ACT";
        }
    },
    SALE_INVOICE {
        @Override
        public String getName(Language language) {
            return "SALE_INVOICE";
        }
    },
    SALE_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return "SALE_POWER_ATTORNEY";
        }
    },
    RENT_ACT {
        @Override
        public String getName(Language language) {
            return "RENT_ACT";
        }
    },
    RENT_ACT_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return "RENT_ACT_POWER_ATTORNEY";
        }
    },
    RENT_ACT_OF_RETURN {
        @Override
        public String getName(Language language) {
            return "RENT_ACT_OF_RETURN";
        }
    },
    RENT_ACT_OF_RETURN_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return "RENT_ACT_OF_RETURN_POWER_ATTORNEY";
        }
    },
    RENT_ACT_IMAGE {
        @Override
        public String getName(Language language) {
            return "RENT_ACT_IMAGE";
        }
    },
    RENT_ACT_OF_RETURN_IMAGE {
        @Override
        public String getName(Language language) {
            return "RENT_ACT_OF_RETURN_IMAGE";
        }
    },
    CASE_FILE {
        @Override
        public String getName(Language language) {
            return "CASE_FILE";
        }
    },
    HUMO_CONNECT {
        @Override
        public String getName(Language language) {
            return "HUMO_CONNECT";
        }
    },
    CONTRACT_REPORT {
        @Override
        public String getName(Language language) {
            return "CONTRACT_REPORT";
        }
    },
    FM_REPORT {
        @Override
        public String getName(Language language) {
            return "FM_REPORT";
        }
    },
    EMPLOYEE_REPORT {
        @Override
        public String getName(Language language) {
            return "EMPLOYEE_REPORT";
        }
    },
    TRADE_IN_LETTER_OF_GUARANTEE {
        @Override
        public String getName(Language language) {
            return "TRADE_IN_LETTER_OF_GUARANTEE";
        }
    },
    CUSTOMER_BRANCH_APPLICATION {
        @Override
        public String getName(Language language) {
            return "CUSTOMER_BRANCH_APPLICATION";
        }
    },
    PAYMENT_CONTRACT {
        @Override
        public String getName(Language language) {
            return "PAYMENT_CONTRACT";
        }
    },
    PAYMENT_ACT {
        @Override
        public String getName(Language language) {
            return "PAYMENT_ACT";
        }
    },
    PAYMENT_ACT_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return "PAYMENT_ACT_POWER_ATTORNEY";
        }
    },
    PAYMENT_COMPANY_CONTRACT {
        @Override
        public String getName(Language language) {
            return "PAYMENT_COMPANY_CONTRACT";
        }
    },
    SERVICE_ORDER_INVOICE {
        @Override
        public String getName(Language language) {
            return "SERVICE_ORDER_INVOICE";
        }
    },
    SERVICE_ORDER_SPECIFICATION {
        @Override
        public String getName(Language language) {
            return "SERVICE_ORDER_SPECIFICATION";
        }
    },
    ONE_TIME_ORDER_INVOICE {
        @Override
        public String getName(Language language) {
            return "ONE_TIME_ORDER_INVOICE";
        }
    },
    CUSTOMER_BRANCH_SCANNER {
        @Override
        public String getName(Language language) {
            return "CUSTOMER_BRANCH_SCANNER";
        }
    },
    RENT_DEVICE_ACT {
        @Override
        public String getName(Language language) {
            return "";
        }
    },
    RENT_DEVICE_ACT_OF_RETURN {
        @Override
        public String getName(Language language) {
            return "RENT_DEVICE_ACT_OF_RETURN";
        }
    },
    RENT_DEVICE_ACT_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return "RENT_DEVICE_ACT_POWER_ATTORNEY";
        }
    },
    VACANCY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Vakansiya";
                case uz_cyrillic -> "Вакансия";
                default -> "Вакансия";
            };
        }
    },
    VACANCY_APPLICATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ishga arizalar";
                case uz_cyrillic -> "Ишга аризалар";
                default -> "Заявки на вакансию";
            };
        }
    },
    VACANCY_APPLICATION_TEMPLATE {
        @Override
        public String getName(Language language) {
            return "VACANCY_APPLICATION_TEMPLATE";
        }
    },
    FEEDBACK {
        @Override
        public String getName(Language language) {
            return "FEEDBACK";
        }
    },
    CUSTOMER_REQUEST {
        @Override
        public String getName(Language language) {
            return "CUSTOMER_REQUEST";
        }
    },
    LEAD {
        @Override
        public String getName(Language language) {
            return "LEAD";
        }
    },
    GROUPS {
        @Override
        public String getName(Language language) {
            return "GROUPS";
        }
    },
    DEPENDENT {
        @Override
        public String getName(Language language) {
            return "DEPENDENT";
        }
    },
    INTEGRATION {
        @Override
        public String getName(Language language) {
            return "INTEGRATION";
        }
    },
    WAREHOUSE {
        @Override
        public String getName(Language language) {
            return "WAREHOUSE";
        }
    },
    PRODUCT {
        @Override
        public String getName(Language language) {
            return "PRODUCT";
        }
    },
    PRODUCT_PHOTO {
        @Override
        public String getName(Language language) {
            return "PRODUCT_PHOTO";
        }
    },
    BRAND {
        @Override
        public String getName(Language language) {
            return "BRAND";
        }
    },
    CUSTOMER_CHECK_BANK_INFO {
        @Override
        public String getName(Language language) {
            return "CUSTOMER_CHECK_BANK_INFO";
        }
    },
    REPAIR_TICKET {
        @Override
        public String getName(Language language) {
            return "REPAIR_TICKET";
        }
    },
    HOLIDAY {
        @Override
        public String getName(Language language) {
            return "HOLIDAY";
        }
    },
    FISCAL_MODULE_REPORT {
        @Override
        public String getName(Language language) {
            return "FISCAL_MODULE_REPORT";
        }
    },
    LEAVE_REQUEST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ta'til so‘rovi";
                case uz_cyrillic -> "Таътил сўрови";
                default -> "Заявки на отпуск";
            };
        }
    },
    PERSONAL_IDENTITY_INFORMATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Shaxsiy hujjatlar";
                case uz_cyrillic -> "Шахсий ҳужжатлар";
                default -> "Личные документы";
            };
        }
    },
    BILL {
        @Override
        public String getName(Language language) {
            return "BILL";
        }
    };

    private final String code;
    private final String name;

    EntityTypeEnum() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
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
