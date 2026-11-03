package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.dto.cases.caseproperty.*;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum CaseType implements Serializable {

    DIRECTOR_FULLNAME_CHANGE(DirectorProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Direktor o'zgarishi";
                case uz_cyrillic -> "Директор ўзгариши";
                default -> "Изменение директора";
            };
        }
    },
    PHONE_NUMBER_CHANGE(PhoneNumberProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Telefon raqami o'zgarishi";
                case uz_cyrillic -> "Телефон рақами ўзгариши";
                default -> "Смена номера телефона";
            };
        }
    },
    DEREGISTER_KKM(DeregisterDeviceProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ro'yxatdan chiqarish";
                case uz_cyrillic -> "Рўйхатдан чиқариш";
                default -> "Снятие с регистрации";
            };
        }
    },
    TEMPORARY_REPLACEMENT(DeviceTemporaryReplacementProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NKMni vaqtincha almashtirish";
                case uz_cyrillic -> "НКМни вақтинча алмаштириш";
                default -> "Временная замена ККМ";
            };
        }
    },
    REPLACEMENT_KKM(DeviceReplacementProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NKMni doimiy almashtirish";
                case uz_cyrillic -> "НКМни доимий алмаштириш";
                default -> "Постоянная замена ККМ";
            };
        }
    },
    REPLACEMENT_FM(FiscalModuleReplacementProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "FMni almashtirish";
                case uz_cyrillic -> "ФМни алмаштириш";
                default -> "Замена ФМ";
            };
        }
    },
    BRANCH_ADDRESS_CHANGE(CustomerBranchAddressProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TT manzilini o'zgartirish";
                case uz_cyrillic -> "ТТ манзилини ўзгартириш";
                default -> "Изменение адреса ТТ";
            };
        }
    },
    LEGAL_DATA_CHANGE(LegalDataProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yuridik ma'lumotlarni o'zgartirish";
                case uz_cyrillic -> "Юридик маълумотларни ўзгартириш";
                default -> "Изменение юридических данных";
            };
        }
    },
    BANK_DETAILS_CHANGE(BankDetailsProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bank ma'lumotlarini o'zgartirish";
                case uz_cyrillic -> "Банк маълумотларини ўзгартириш";
                default -> "Изменение банковских реквизитов";
            };
        }
    },
    BRANCH_DATA_CHANGE(BranchDataChangeProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TT ma'lumotlarini o'zgartirish";
                case uz_cyrillic -> "ТТ маълумотларини ўзгартириш";
                default -> "Изменение данных ТТ";
            };
        }
    },
    TARIFF_CHANGE(ChangeTariffProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tarifni o'zgartirish";
                case uz_cyrillic -> "Тарифни ўзгартириш";
                default -> "Изменение тарифа";
            };
        }
    },
    HUMO_BANK_DETAILS_CHANGE(HumoBankDetailsChangeProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HUMO ma'lumotlarini o'zgartirish";
                case uz_cyrillic -> "HUMO маълумотларини ўзгартириш";
                default -> "Изменение реквизитов торгового эквайринга";
            };
        }
    },
    HUMO_CONNECTION_CHANGE(HumoConnectionProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Savdo ekvayringni ulash";
                case uz_cyrillic -> "Савдо эквайрингни улаш";
                default -> "Подключение торгового эквайринга";
            };
        }
    },
    APP_TYPE_CHANGE(AppTypeChangeProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Billing turini o'zgartirish";
                case uz_cyrillic -> "Биллинг турини ўзгартириш";
                default -> "Изменение типа биллинга";
            };
        }
    },
    TARIFF_LIST_CHANGE(ChangeDeviceListTariffProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Devays tarifini o'zgartirish";
                case uz_cyrillic -> "Девайс тарифини ўзгартириш";
                default -> "Изменение тариф девайсов";
            };
        }
    },
    TEMPORARY_REPLACEMENT_REVERT(DeviceTemporaryReplacementRevertProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NKMni qayta almashtirish";
                case uz_cyrillic -> "НКМни қайта алмаштириш";
                default -> "Обратная замена ККМ";
            };
        }
    },
    ACT_OF_RECONCILIATION(ActOfReconciliationProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kelishuv akti";
                case uz_cyrillic -> "Келишув акти";
                default -> "Акт сверка";
            };
        }
    },
    CUSTOMER_BRANCH_SCANNER(CustomerBranchScannerProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Skaner";
                case uz_cyrillic -> "Сканер";
                default -> "Сканер";
            };
        }
    },
    HUMO_ACCOUNT_CLOSE(HumoAccountCloseProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HUMO hisobini yopish";
                case uz_cyrillic -> "HUMO ҳисобини ёпиш";
                default -> "Закрытие счета торгового эквайринга";
            };
        }
    },
    CHANGE_DEVICE_TYPE(ChangeDeviceTypeProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Devise turini o'zgartirish";
                case uz_cyrillic -> "Девайс турини ўзгартириш";
                default -> "Изменить тип устройства";
            };
        }
    },
    CHANGE_MANAGER(ChangeManagerProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Menejerni almashtirish";
                case uz_cyrillic -> "Менеджерни алмаштириш";
                default -> "Смена менеджера";
            };
        }
    },
    CHANGE_CTS(ChangeCtsProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TXKMni almashtirish";
                case uz_cyrillic -> "ТХКМни алмаштириш";
                default -> "Смена ЦТО";
            };
        }
    },
    DEACTIVATE_DEVICE(DeactivateDeviceProperty.class.getName()) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Devaysni deaktivatsiya qilish";
                case uz_cyrillic -> "Девайсни деактивация қилиш";
                default -> "Деактивация Девайса";
            };
        }
    };

    private final String code;
    private final String name;
    private final String className;

    CaseType(String className) {
        this.code = this.name();
        this.name = this.getName();
        this.className = className;
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CaseType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CaseTypeEnum enum value: " + code));
    }

    public static EnumSet<CaseType> lookUp(String search) {
        if (StringUtils.isEmpty(search)) {
            return EnumSet.allOf(CaseType.class);
        }
        Set<CaseType> caseTypeSet = Arrays.stream(values())
                .filter(caseType -> StringUtils.containsIgnoreCase(caseType.getName(), search))
                .collect(Collectors.toSet());
        return caseTypeSet.isEmpty() ? EnumSet.noneOf(CaseType.class) : EnumSet.copyOf(caseTypeSet);
    }

    @JsonCreator
    public static CaseType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CaseTypeEnum");
    }
}
