package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum Role implements Serializable {
    ROLE_SUPER_ADMIN(PermissionType.ADMIN) {
        @Override
        public String getName(LanguageEnum languageEnum) {
            return switch (languageEnum) {
                case uz, en -> "Super Admin";
                case cyrillic -> "Супер Админ";
                default -> "Супер администратор";
            };
        }
    },

    ROLE_ADMIN(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum languageEnum) {
            return switch (languageEnum) {
                case uz, en -> "Admin";
                case cyrillic -> "Админ";
                default -> "Администратор";
            };
        }
    }, ROLE_SUPERVISOR(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Nazoratchi";
                case cyrillic -> "Назоратчи";
                default -> "Руководитель";
            };
        }
    },
    ROLE_MODERATOR(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz, en -> "Moderator";
                case cyrillic -> "Модератор";
                default -> "Модератор";
            };
        }
    },
    ROLE_OPERATOR(PermissionType.ADMIN) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz, en -> "Operator";
                case cyrillic -> "Оператор";
                default -> "Оператор";
            };
        }
    },
    ROLE_SUPPORT(PermissionType.ADMIN) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Texnik mutaxassis";
                case cyrillic -> "Техник мутахассис";
                case en -> "Support";
                default -> "Технический специалист";
            };
        }
    },
    ROLE_OWNER(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Biznes egasi";
                case cyrillic -> "Бизнес эгаси";
                case en -> "Business owner";
                default -> "Владелец бизнеса";
            };
        }
    },
    ROLE_DIRECTOR(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Direktor";
                case cyrillic -> "Директор";
                case en -> "Director";
                default -> "Директор";
            };
        }
    },
    ROLE_ACCOUNTANT(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hisobchi";
                case cyrillic -> "Ҳисобчи";
                case en -> "Accountant";
                default -> "Бухгалтер";
            };
        }
    },
    ROLE_BRANCH_ADMIN(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Filial menejeri";
                case cyrillic -> "Филиал менежери";
                case en -> "Branch admin";
                default -> "Руководитель филиала";
            };
        }
    },

    ROLE_WAREHOUSE_MANAGER(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sklad menejeri";
                case cyrillic -> "Склад менежери";
                case en -> "Warehouse manager";
                default -> "Заведующий складом";
            };
        }
    },

    ROLE_CASHIER(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum languageEnum) {
            return switch (languageEnum) {
                case uz -> "Kassir";
                case cyrillic -> "Кассир";
                case en -> "Cashier";
                default -> "Кассир";
            };
        }
    },
    ROLE_WAITER(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum languageEnum) {
            return switch (languageEnum) {
                case uz -> "Ofitsant";
                case cyrillic -> "Официант";
                case en -> "Waiter";
                default -> "Официант";
            };
        }
    },
    ROLE_CHEF(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum languageEnum) {
            return switch (languageEnum) {
                case uz -> "Oshpaz";
                case cyrillic -> "Ошпаз";
                case en -> "Chef";
                default -> "Повар";
            };
        }
    },
    ROLE_BARMEN(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum languageEnum) {
            return switch (languageEnum) {
                case uz -> "Bar xodimi";
                case cyrillic -> "Бар ходими";
                case en -> "Barmen";
                default -> "Бармен";
            };
        }
    },
    ROLE_CLIENT(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mijoz";
                case cyrillic -> "Мижоз";
                case en -> "Client";
                default -> "Клиент";
            };
        }
    },
    ROLE_HOSTESS(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mezbon";
                case cyrillic -> "Мезбон";
                case en -> "Hostess";
                default -> "Хостес";
            };
        }
    },
    ROLE_MANAGER(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Menejer";
                case cyrillic -> "Менежер";
                case en -> "Manager";
                default -> "Менеджер";
            };
        }
    },
    ROLE_USER(PermissionType.CABINET) {
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
    ROLE_CTS_INTEGRATION(PermissionType.ADMIN) {
        @Override
        public String getName(LanguageEnum language) {
            return "Cts Integration";
        }
    },
    ROLE_APAY_INTEGRATION(PermissionType.ADMIN) {
        @Override
        public String getName(LanguageEnum language) {
            return "Apay Integration";
        }
    },
    ROLE_ANONYMOUS(PermissionType.CABINET) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Anonim";
                default -> "Аноним";
            };
        }
    };

    private final String name;
    private final String code;
    private final PermissionType type;

    Role() {
        this.name = getName();
        this.code = name();
        this.type = null;
    }

    Role(PermissionType type) {
        this.name = getName();
        this.type = type;
        this.code = name();
    }

    public static List<CommonCodeDTO> getOrganizationCodesAsCommonDTO() {
        return List.of(
                new CommonCodeDTO(null, Role.ROLE_ADMIN.getName(), Role.ROLE_ADMIN.getCode()),
                new CommonCodeDTO(null, Role.ROLE_MODERATOR.getName(), Role.ROLE_MODERATOR.getCode()),
                new CommonCodeDTO(null, Role.ROLE_OPERATOR.getName(), Role.ROLE_OPERATOR.getCode()),
                new CommonCodeDTO(null, Role.ROLE_SUPPORT.getName(), Role.ROLE_SUPPORT.getCode())
        );
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static Role getByCode(String code) {
        return Arrays.stream(Role.values()).filter(role -> role.getCode().equals(code)).findFirst().orElse(null);
    }

    public static List<Role> getAdminRoles() {
        return List.of(
                Role.ROLE_SUPER_ADMIN,
                Role.ROLE_ADMIN,
                Role.ROLE_SUPERVISOR,
                Role.ROLE_MODERATOR,
                Role.ROLE_SUPPORT,
                Role.ROLE_OPERATOR,
                Role.ROLE_CTS_INTEGRATION
        );
    }

    public static List<Role> getAllowableAdminRoles() {
        return List.of(
                Role.ROLE_ADMIN,
                Role.ROLE_SUPERVISOR,
                Role.ROLE_MODERATOR,
                Role.ROLE_SUPPORT,
                Role.ROLE_OPERATOR
        );
    }

    public static List<String> getAdminRoleCodes() {
        return getAdminRoles().stream().map(Role::getCode).collect(Collectors.toList());

    }

    public static List<Role> getCabinetRoles() {
        return List.of(
                Role.ROLE_OWNER,
                Role.ROLE_DIRECTOR,
                Role.ROLE_ACCOUNTANT,
                Role.ROLE_BRANCH_ADMIN,
                Role.ROLE_WAREHOUSE_MANAGER,
                Role.ROLE_CASHIER,
                Role.ROLE_WAITER,
                Role.ROLE_CHEF,
                Role.ROLE_BARMEN,
                Role.ROLE_HOSTESS,
                Role.ROLE_MANAGER
        );
    }

    public static List<String> getCabinetRoleCodes() {
        return getCabinetRoles().stream().map(Role::getCode).collect(Collectors.toList());
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
}
