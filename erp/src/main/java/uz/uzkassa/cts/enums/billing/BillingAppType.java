package uz.uzkassa.cts.enums.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.Language;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BillingAppType implements Serializable {
    SMARTPOS(CompanyType.CDTI.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SMARTPOS";
                case uz_cyrillic -> "SMARTPOS";
                default -> "SMARTPOS";
            };
        }
    },
    SILEN(CompanyType.CDTI.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SILEN";
                case uz_cyrillic -> "SILEN";
                default -> "SILEN";
            };
        }
    },
    SUPPLIER(CompanyType.CDTI.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SUPPLIER";
                case uz_cyrillic -> "SUPPLIER";
                default -> "SUPPLIER";
            };
        }
    },
    ADMIN(CompanyType.CDTI.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "ADMIN";
                case uz_cyrillic -> "ADMIN";
                default -> "ADMIN";
            };
        }
    },
    SPI(CompanyType.CDTI.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SPI";
                case uz_cyrillic -> "SPI";
                default -> "SPI";
            };
        }
    },
    TOP_UP_CDTI(CompanyType.CDTI.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Пополнение CDTI";
                case uz_cyrillic -> "Пополнение CDTI";
                default -> "Пополнение CDTI";
            };
        }
    },
    STS_APP(CompanyType.STS.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "STS_APP";
                case uz_cyrillic -> "STS_APP";
                default -> "STS_APP";
            };
        }
    },
    TOP_UP_STS(CompanyType.STS.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Пополнение STS";
                case uz_cyrillic -> "Пополнение STS";
                default -> "Пополнение STS";
            };
        }
    },
    GREEN_APP(CompanyType.GREEN.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "GREEN_APP";
                case uz_cyrillic -> "GREEN_APP";
                default -> "GREEN_APP";
            };
        }
    },
    TOP_UP_GREEN(CompanyType.GREEN.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Пополнение GREEN";
                case uz_cyrillic -> "Пополнение GREEN";
                default -> "Пополнение GREEN";
            };
        }
    },
    PRIME_APP(CompanyType.PRIME.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "PRIME_APP";
                case uz_cyrillic -> "PRIME_APP";
                default -> "PRIME_APP";
            };
        }
    },
    TOP_UP_PRIME(CompanyType.PRIME.name()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Пополнение PRIME";
                case uz_cyrillic -> "Пополнение PRIME";
                default -> "Пополнение PRIME";
            };
        }
    };

    private final String code;
    private final String name;
    private final String className;

    BillingAppType(String className) {
        this.code = this.name();
        this.name = this.getName();
        this.className = className;
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static BillingAppType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown AppType enum value: " + code));
    }

    @JsonCreator
    public static BillingAppType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for AppType");
    }
}
