package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum Role implements Serializable {
    ROLE_SUPER_ADMIN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Super Admin";
                case cyrillic -> "Супер Админ";
                default -> "Супер администратор";
            };
        }
    },
    ROLE_ADMIN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Admin";
                case cyrillic -> "Админ";
                default -> "Администратор";
            };
        }
    },
    ROLE_SUPERVISOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Supervisor";
                case cyrillic -> "Супервайзер";
                default -> "Супервайзер";
            };
        }
    },
    ROLE_MODERATOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Moderator";
                default -> "Модератор";
            };
        }
    },
    ROLE_OPERATOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Operator";
                default -> "Оператор";
            };
        }
    },
    ROLE_SUPPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Manager";
                default -> "Менеджер";
            };
        }
    },
    ROLE_BUSINESS_OWNER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Biznes egasi";
                case cyrillic -> "Бизнес эгаси";
                default -> "Владелец бизнеса";
            };
        }
    },
    ROLE_DIRECTOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Direktor";
                default -> "Директор";
            };
        }
    },
    ROLE_ACCOUNTANT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hisobchi";
                case cyrillic -> "Ҳисобчи";
                default -> "Бухгалтер";
            };
        }
    },
    ROLE_MAIN_ACCOUNTANT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bosh hisobchi";
                case cyrillic -> "Бош ҳисобчи";
                default -> "Главный бухгалтер";
            };
        }
    },
    ROLE_BRANCH_ADMIN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Filial menejeri";
                case cyrillic -> "Филиал менежери";
                default -> "Руководитель филиала";
            };
        }
    },
    ROLE_WAREHOUSE_MANAGER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sklad menejeri";
                case cyrillic -> "Склад менежери";
                default -> "Заведующий складом";
            };
        }
    },
    ROLE_WAREHOUSE_EMPLOYEE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sklad xodimi";
                case cyrillic -> "Склад ходими";
                default -> "Сотрудник склада";
            };
        }
    },
    ROLE_AGENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Savdo agenti";
                case cyrillic -> "Савдо агенти";
                default -> "Торговый агент";
            };
        }
    },
    ROLE_NETWORK_AGENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Savdo tarmog'i agenti";
                case cyrillic -> "Савдо тармоғи агенти";
                default -> "Агент торговой сети";
            };
        }
    },
    ROLE_CONFIDANT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mas’ul shaxs";
                case cyrillic -> "Масъул шахс";
                default -> "Доверенное лицо";
            };
        }
    },
    ROLE_COURIER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yetkazib beruvchi";
                case cyrillic -> "Етказиб берувчи";
                default -> "Доставщик";
            };
        }
    },
    ROLE_EXPEDITOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yukni yetkazuvchi mas’ul shaxs";
                case cyrillic -> "Юкни етказувчи масъул шахс";
                default -> "Экспедитор";
            };
        }
    },
    ROLE_USER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Foydalanuvchi";
                case cyrillic -> "Фойдаланувчи";
                default -> "Пользователь";
            };
        }
    },
    ROLE_ANONYMOUS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Anonim";
                default -> "Аноним";
            };
        }
    },
    ROLE_1C_INTEGRATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Integratsiya";
                default -> "Интеграция";
            };
        }
    },
    ROLE_BILLING {
        @Override
        public String getName(LanguageEnum language) {
            return "Billing";
        }
    },
    ROLE_APAY {
        @Override
        public String getName(LanguageEnum language) {
            return "Apay";
        }
    },
    ROLE_CASHIER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kassir";
                case cyrillic -> "Кассир";
                default -> "Kaccиp";
            };
        }
    },
    ROLE_DRIVER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xaydovchi";
                case cyrillic -> "Хайдовчи";
                default -> "Водитель";
            };
        }
    },
    ROLE_HIPPO {
        @Override
        public String getName(LanguageEnum language) {
            return "Hippo";
        }
    },
    ROLE_MARKETPLACE {
        @Override
        public String getName(LanguageEnum language) {
            return "Marketplace";
        }
    },
    ROLE_SWAGGER {
        @Override
        public String getName(LanguageEnum language) {
            return "Swagger";
        }
    },
    ONLINE_KKM {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "KKM xizmat ko‘rsatish";
                case cyrillic -> "ККМ хизмат кўрсатиш";
                default -> "Обслуживание ККМ";
            };
        }
    },
    ROLE_MARKETPLACE_BUSINESS_OWNER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Marketpleys biznes egasi";
                case cyrillic -> "Маркетплейс бизнес эгаси";
                default -> "Владелец бизнеса в маркетплейсе";
            };
        }
    },
    ROLE_VIEWER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ko‘rish huquqiga ega foydalanuvchi";
                case cyrillic -> "Кўриш рухсатига эга фойдаланувчи";
                default -> "Пользователь с правами просмотра";
            };
        }
    };

    private final String name;
    private final String code;

    Role() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static Role getByCode(String code) {
        for (Role role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    @JsonCreator
    public static Role forValue(Map<String, String> value) {
        return Role.valueOf(value.get("code"));
    }

    public static List<CommonCodeDTO> getOrganizationRolesAsCommonDTO() {
        return getAdminRoles().stream().map(role -> new CommonCodeDTO(null, role.getName(), role.getCode())).toList();
    }

    public static List<String> getAdminRoleCodes() {
        return getAdminRoles().stream().map(Role::getCode).toList();
    }

    public static List<Role> getAdminRoles() {
        return Arrays.asList(
                Role.ROLE_SUPER_ADMIN,
                Role.ROLE_ADMIN,
                Role.ROLE_SUPERVISOR,
                Role.ROLE_MODERATOR,
                Role.ROLE_OPERATOR,
                Role.ROLE_SUPPORT,
                Role.ROLE_APAY
        );
    }

    public static EnumSet<Role> getBusinessOwnerRole() {
        return EnumSet.of(
                Role.ROLE_BUSINESS_OWNER,
                Role.ROLE_MARKETPLACE_BUSINESS_OWNER);
    }

    public static List<String> getCabinetRoleCodes() {
        return getCabinetRoles().stream().map(Role::getCode).toList();
    }

    public static List<Role> getCabinetRoles() {
        return Arrays.asList(
                Role.ROLE_SUPERVISOR,
                Role.ROLE_BUSINESS_OWNER,
                Role.ROLE_DIRECTOR,
                Role.ROLE_MAIN_ACCOUNTANT,
                Role.ROLE_ACCOUNTANT,
                Role.ROLE_BRANCH_ADMIN,
                Role.ROLE_WAREHOUSE_MANAGER,
                Role.ROLE_WAREHOUSE_EMPLOYEE,
                Role.ROLE_AGENT,
                Role.ROLE_NETWORK_AGENT,
                Role.ROLE_CONFIDANT,
                Role.ROLE_COURIER,
                Role.ROLE_DRIVER,
                Role.ROLE_EXPEDITOR,
                Role.ROLE_CASHIER,
                Role.ROLE_MARKETPLACE_BUSINESS_OWNER,
                Role.ROLE_VIEWER
        );
    }

    public static EnumSet<Role> getBillingRoles() {
        return EnumSet.of(
                Role.ROLE_BILLING,
                Role.ROLE_ADMIN,
                Role.ROLE_SUPER_ADMIN,
                Role.ROLE_BUSINESS_OWNER,
                Role.ROLE_SUPERVISOR,
                Role.ROLE_SUPPORT,
                Role.ROLE_MAIN_ACCOUNTANT
        );
    }

    public static EnumSet<Role> getBranchRequiredRoles() {
        return EnumSet.of(
                Role.ROLE_BRANCH_ADMIN
        );
    }

    public static EnumSet<Role> getWarehouseRequiredRoles() {
        return EnumSet.of(
                Role.ROLE_WAREHOUSE_EMPLOYEE,
                Role.ROLE_WAREHOUSE_MANAGER
        );
    }

    public static List<String> getBillingRoleCodes() {
        return getBillingRoles().stream().map(Role::getCode).toList();
    }

    public static List<Role> getKPIRoles() {
        return List.of(
                ROLE_AGENT
        );
    }
}
