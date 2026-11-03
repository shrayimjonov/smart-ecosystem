package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.EnumDTO;

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
@AllArgsConstructor
public enum WaybillStatus {
    DRAFT("Сохраненные", "Сохранено", 0),
    ConsignorSent("Отправленные", "Отправлено", 110),
    ConsignorCanceled("Отмененные", "Отменено", 120),

    ResponsiblePersonRejected("Отклоненные агентом", "Отклонено агентом", 130),
    ResponsiblePersonAccepted("Подписанные агентом", "Подписано агентом", 140),
    ResponsiblePersonTillReturned("Груз возвращается", "Груз возвращается", 150),
    ResponsiblePersonGiven("Переданные", "Передано", 160),

    ConsigneeRejected("Отклоненные", "Отклонено", 170),
    ConsigneeAccepted("Подтвержденные", "Подтверждено", 180),

    ResponsiblePersonReturned("Возвращенные агентом", "Возвращено агентом", 190),
    TaxCommitteeRevoked("Налоговый комитет отменил", "Налоговый комитет отменил", 999),

    ConsignorReturnAccepted("Возврашенные", "Возвращено", 200);

    private final String ruText;
    private final String shortText;
    private final Integer code;

    public static WaybillStatus fromString(String code) {
        if (code != null) {
            for (WaybillStatus item : WaybillStatus.values()) {
                if (code.equalsIgnoreCase(item.getRuText()) || code.equals(item.name())) {
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
        return new EnumDTO(getCode(), name(), getRuText());
    }

    public EnumDTO toListDto() {
        return new EnumDTO(getCode(), name(), getShortText());
    }

    @JsonCreator
    public static WaybillStatus forValue(String value) {
        return fromString(value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
