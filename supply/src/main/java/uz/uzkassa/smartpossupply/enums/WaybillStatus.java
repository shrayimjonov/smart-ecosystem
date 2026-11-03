package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.dto.base.EnumDTO;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ConsignorSent = 110, //Joʻnatilgan
 * ConsignorCanceled = 120, //Bekor qilingan
 * <p>
 * ResponsiblePersonRejected = 130, //Rad qilingan(YYB)
 * ResponsiblePersonAccepted = 140, //Qabul qilingan
 * ResponsiblePersonTillReturned = 150, //Yuk qaytarilgan
 * ResponsiblePersonGiven = 160, //Yuk oluvchiga topshirilgan
 * <p>
 * ConsigneeRejected = 170, //Rad qilingan
 * ConsigneeAccepted = 180, //Tasdiqlangan
 * <p>
 * ResponsiblePersonReturned = 190, //Yuk qaytarilgan
 * <p>
 * ConsignorReturnAccepted = 200, //Qaytarib olingan
 * <p>
 * TaxCommitteeRevoked = 999 //Bekor qilingan (SQ)
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum WaybillStatus {
    DRAFT("Сохранено", 0) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Saqlanganlar";
                case cyrillic -> "Сақланганлар";
                default -> "Сохраненные";
            };
        }
    },
    ConsignorSent("Отправлено", 110) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuborilganlar";
                case cyrillic -> "Юборилганлар";
                default -> "Отправленные";
            };
        }
    },
    ConsignorCanceled("Отменено", 120) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bekor qilinganlar";
                case cyrillic -> "Бекор қилинганлар";
                default -> "Отмененные";
            };
        }
    },

    ResponsiblePersonRejected("Отклонено агентом", 130) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agent tomonidan rad etilganlar";
                case cyrillic -> "Агент томонидан рад этилганлар";
                default -> "Отклоненные агентом";
            };
        }
    },
    ResponsiblePersonAccepted("Подписано агентом", 140) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agent tomonidan imzolangan";
                case cyrillic -> "Агент томонидан имзоланган";
                default -> "Подписанные агентом";
            };

        }
    },
    ResponsiblePersonTillReturned("Груз возвращается", 150) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuk qaytarilmoqda";
                case cyrillic -> "Юк қайтарилмоқда";
                default -> "Груз возвращается";
            };
        }
    },
    ResponsiblePersonGiven("Передано", 160) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Topshirilganlar";
                case cyrillic -> "Топширилганлар";
                default -> "Переданные";
            };
        }
    },

    ConsigneeRejected("Отклонено", 170) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Rad etilganlar";
                case cyrillic -> "Рад этилганлар";
                default -> "Отклоненные";
            };
        }
    },
    ConsigneeAccepted("Подтверждено", 180) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tasdiqlanganlar";
                case cyrillic -> "Тасдиқланганлар";
                default -> "Подтвержденные";
            };
        }
    },

    ResponsiblePersonReturned("Возвращено агентом", 190) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agent tomonidan qaytarilganlar";
                case cyrillic -> "Агент томонидан қайтарилганлар";
                default -> "Возвращенные агентом";
            };
        }
    },
    TaxCommitteeRevoked("Налоговый комитет отменил", 999) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Soliq qoʻmitasi rad etdi";
                case cyrillic -> "Солиқ қўмитаси рад этди";
                default -> "Налоговый комитет отменил";
            };

        }
    },

    ConsignorReturnAccepted("Возвращено", 200) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qaytarilganlar";
                case cyrillic -> "Қайтарилганлар";
                default -> "Возврашенные";
            };
        }
    },
    Revoked("Недействительный", 998) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Haqiqiy emas";
                case cyrillic -> "Ҳақиқий эмас";
                default -> "Недействительный";
            };
        }
    };

    private final String name;
    private final String shortText;
    private final Integer code;

    WaybillStatus(String shortText, Integer code) {
        this.shortText = shortText;
        this.code = code;
        this.name = getName();
    }

    public static WaybillStatus fromString(String code) {
        if (code != null) {
            for (WaybillStatus item : WaybillStatus.values()) {
                if (code.equalsIgnoreCase(item.getName()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static WaybillStatus fromCode(Integer code) {
        if (code != null) {
            for (WaybillStatus item : WaybillStatus.values()) {
                if (code.equals(item.getCode())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(WaybillStatus.values().length);
        for (WaybillStatus item : WaybillStatus.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto() {
        return new EnumDTO(getCode(), name(), getName());
    }

    public EnumDTO toListDto() {
        return new EnumDTO(getCode(), name(), getName());
    }

    public static Integer getSignType(WaybillStatus status) {
       return switch (status) {
            case ConsignorSent -> 1;
            case ConsignorCanceled -> 2;
            case ResponsiblePersonRejected -> 3;
            case ResponsiblePersonAccepted -> 4;
            case ResponsiblePersonTillReturned -> 5;
            case ResponsiblePersonGiven -> 6;
            case ConsigneeRejected -> 7;
            case ConsigneeAccepted -> 8;
            case ResponsiblePersonReturned -> 9;
            case ConsignorReturnAccepted -> 10;
            default -> 0;
        };
    }

    @JsonCreator
    public static WaybillStatus forValue(String value) {
        return fromString(value);
    }

    @Override
    public String toString() {
        return this.name();
    }


    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
