package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.interfaces.TemplateAttributeConstants;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;
import java.util.List;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TemplateType {
    ACTIVATION(TemplateAttributeConstants.ACTIVATION.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faollashtirish";
                case uz_cyrillic -> "Фаоллаштириш";
                default -> "Активация";
            };
        }
    },
    CHANGE_PASSWORD(TemplateAttributeConstants.CHANGE_PASSWORD.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Parolni o'zgartirish";
                case uz_cyrillic -> "Паролни ўзгартириш";
                default -> "Изменение пароля";
            };
        }
    },
    CHANGE_USERNAME(TemplateAttributeConstants.CHANGE_USERNAME.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Loginni o'zgartirish";
                case uz_cyrillic -> "Логинни ўзгартириш";
                default -> "Изменить логин";
            };
        }
    },
    BIRTHDAY(TemplateAttributeConstants.BIRTHDAY.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tug'ilgan kun";
                case uz_cyrillic -> "Туғилган кун";
                default -> "День рождения";
            };
        }
    },
    CUSTOMER_ACTIVATION_CTS(TemplateAttributeConstants.CUSTOMER_ACTIVATION.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CTS mijozlarni faollashtirish";
                case uz_cyrillic -> "CTS мижозларни фаоллаштириш";
                default -> "Активация клиентов CTS";
            };
        }
    },
    CUSTOMER_ACTIVATION_SMARTPOS(TemplateAttributeConstants.CUSTOMER_ACTIVATION.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Smartpos mijozlarni faollashtirish";
                case uz_cyrillic -> "Smartpos мижозларни фаоллаштириш";
                default -> "Активация клиентов Smartpos";
            };
        }
    },
    LEAD(TemplateAttributeConstants.LEAD.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Lead";
                case uz_cyrillic -> "Lead";
                default -> "Лид";
            };
        }
    },
    VACANCY_APPLICATION_CREATED(TemplateAttributeConstants.VACANCY_APPLICATION.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi ariza yaratish";
                case uz_cyrillic -> "Янги ариза яратиш";
                default -> "Создать новая заявка";
            };
        }
    },
    VACANCY_APPLICATION_REJECTED(TemplateAttributeConstants.VACANCY_APPLICATION.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ariza rad etildi";
                case uz_cyrillic -> "Ариза рад этилди";
                default -> "Заявка отклонена";
            };
        }
    },
    VACANCY_APPLICATION_ACCEPTED(TemplateAttributeConstants.VACANCY_APPLICATION.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ariza qabul qilindi";
                case uz_cyrillic -> "Ариза кабул килинди";
                default -> "Заявка принята";
            };
        }
    },
    APPROVAL_PROCESS_TEMPLATE_FOR_CREATOR(TemplateAttributeConstants.APPROVAL_PROCESS_TEMPLATE_FOR_CREATOR.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yaratuvchi uchun tasdiqlash jarayoni shabloni";
                case uz_cyrillic -> "Яратувчи учун тасдиқлаш жараёни шаблони";
                default -> "Шаблон процесса утверждения для создателя";
            };
        }
    },
    APPROVAL_PROCESS_TEMPLATE_FOR_APPROVER(TemplateAttributeConstants.APPROVAL_PROCESS_TEMPLATE_FOR_APPROVER.ATTRIBUTES) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tasdiqlovchi uchun tasdiqlash jarayoni shabloni";
                case uz_cyrillic -> "Тасдиқловчи учун тасдиқлаш жараёни шаблони";
                default -> "Шаблон процесса утверждения для утверждающего";
            };
        }
    };

    private final String name;
    private final String code;
    private final List<CommonCodeDTO> attributes;

    TemplateType(List<CommonCodeDTO> attributes) {
        this.code = this.name();
        this.name = this.getName();
        this.attributes = attributes;
    }

    public abstract String getName(Language language);

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
