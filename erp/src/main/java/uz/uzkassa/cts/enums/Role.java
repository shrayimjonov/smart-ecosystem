package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum Role implements Serializable {

    SUPER_ADMIN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Super Admin";
                default -> "Супер Админ";
            };
        }
    },
    ADMIN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Admin";
                default -> "Админ";
            };
        }
    },

    DIRECTOR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Direktor";
                case uz_cyrillic -> "Директор";
                default -> "Директор";
            };
        }
    },
    ACCOUNTANT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xisobchi";
                case uz_cyrillic -> "Хисобчи";
                default -> "Бухгалтер";
            };
        }
    },
    HR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HR Менеджер";
                case uz_cyrillic -> "HR Менеджер";
                default -> "HR Менеджер";
            };
        }
    },
    EMPLOYEE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xodim";
                case uz_cyrillic -> "Ходим";
                default -> "Сотрудник";
            };
        }
    },
    CALL_CENTER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Колл-центр";
                case uz_cyrillic -> "Колл-центр";
                default -> "Колл-центр";
            };
        }
    },
    WAREHOUSE_MANAGER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Зав.Склад";
                case uz_cyrillic -> "Зав.Склад";
                default -> "Зав.Склад";
            };
        }
    },
    CURATOR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kurator";
                case uz_cyrillic -> "Куратор";
                default -> "Куратор";
            };
        }
    },
    SUPERVISOR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Boshqaruvchi";
                case uz_cyrillic -> "Бошқарувчи";
                default -> "Руководитель";
            };
        }
    },
    MANAGER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Menejer";
                case uz_cyrillic -> "Менежер";
                default -> "Менеджер";
            };
        }
    },
    USER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Foydalanuvchi";
                case uz_cyrillic -> "Фойдаланувчи";
                default -> "Пользователь";
            };
        }
    },
    UZCARD {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Uzcard Connection";
                case uz_cyrillic -> "Uzcard Connection";
                default -> "Uzcard Connection";
            };
        }
    },
    CUSTOMER_ADMIN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Admin";
                case uz_cyrillic -> "Админ";
                default -> "Админ";
            };
        }
    },
    OPERATOR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Operator";
                case uz_cyrillic -> "Operator";
                default -> "Оператор";
            };
        }
    },
    PROMO_MANAGER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Promo Manager";
                case uz_cyrillic -> "Promo Manager";
                default -> "Promo Manager";
            };
        }
    },
    INTEGRATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Integratsiya";
                case uz_cyrillic -> "Интеграция";
                default -> "Интеграция";
            };
        }
    },
    SALES_MANAGER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sotuv menejeri";
                case uz_cyrillic -> "Сотув менежери";
                default -> "Менеджер по продажам";
            };
        }
    };

    private final String name;
    private final String code;
    private final String roleCode;

    Role() {
        this.name = getName();
        this.code = this.name();
        this.roleCode = "ROLE_".concat(this.code);
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static Role get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Role enum value: " + code));
    }

    @JsonCreator
    public static Role fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for Role");
    }

    public static Role getRoleCode(String roleCode) {
        return Stream.of(values()).filter(role -> role.getRoleCode().equals(roleCode)).findFirst().orElse(null);
    }

    public static List<String> getAdminRoles() {
        return List.of(Role.SUPER_ADMIN.getRoleCode(),
                Role.ADMIN.getRoleCode(),
                Role.DIRECTOR.getRoleCode(),
                Role.ACCOUNTANT.getRoleCode(),
                Role.HR.getRoleCode(),
                Role.EMPLOYEE.getRoleCode(),
                Role.CURATOR.getRoleCode(),
                Role.CALL_CENTER.getRoleCode(),
                Role.WAREHOUSE_MANAGER.getRoleCode(),
                Role.USER.getRoleCode(),
                Role.INTEGRATION.getRoleCode(),
                Role.UZCARD.getRoleCode(),
                Role.PROMO_MANAGER.getRoleCode()
        );
    }

    public static List<String> getCabinetRoleCodes() {
        List<String> roles = new ArrayList<>();
        roles.add(Role.ADMIN.getRoleCode());
        roles.add(Role.OPERATOR.getRoleCode());
        roles.add(Role.CURATOR.getRoleCode());
        roles.add(Role.SUPERVISOR.getRoleCode());
        roles.add(Role.MANAGER.getRoleCode());
        roles.add(Role.CALL_CENTER.getRoleCode());
        roles.add(Role.PROMO_MANAGER.getRoleCode());
        return roles;
    }

    public static List<String> getPromoManagerRoleCodes() {
        List<String> roles = new ArrayList<>();
        roles.add(Role.ADMIN.getRoleCode());
        roles.add(Role.PROMO_MANAGER.getRoleCode());
        return roles;
    }

    public static Set<String> getCompanyAndCabinetRoleCodes() {
        Set<String> result = new HashSet<>();
        result.addAll(getAdminRoles());
        result.addAll(getCabinetRoleCodes());
        result.addAll(getPromoManagerRoleCodes());

        return result;
    }
}
