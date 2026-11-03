package uz.uzkassa.silen.dto.waybill;

import lombok.Getter;

@Getter
public enum WaybillTransportType {

    Bus("Avtobus", 1),
    Light("Yengil", 2),
    Carriage("Tirkama", 3),
    Motorcycle("Motonakloyot", 4),
    Electronics("Elektronakliyot", 5),
    Load("Yuk", 6),
    Minibus("Mikroavtobus", 7),
    Special("Maxsus", 8),
    Other("Boshqa", 9),
    Cargo("Yuk yo’lovchi tashuvchi", 10),
    Trailer("Yarim tirkama", 11);

    final Integer code;
    final String text;

    WaybillTransportType(String text, Integer code) {
        this.code = code;
        this.text = text;
    }

    public static WaybillTransportType fromCode(Integer code) {
        if (code != null) {
            for (WaybillTransportType item : WaybillTransportType.values()) {
                if (code.equals(item.getCode())) {
                    return item;
                }
            }
        }
        return null;
    }
}
