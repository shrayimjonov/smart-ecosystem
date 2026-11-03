package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReservationStatus {
    RESERVED("Забронирован", "Bron qilindi", "Забронирован", false),
    EXPIRED("Истек", "Muddati o'tdi", "Истек", true),
    CANCELED("Отменен", "Bekor qilindi", "Отменен", true),
    CONFIRMED("Подтвержден", "Tasdiqlandi", "Подтвержден", true);

    private final String code;
    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;
    private final boolean increase;

    ReservationStatus(String nameRu, String nameUz, String nameCyrillic, boolean increase) {
        this.code = name();
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.nameCyrillic = nameCyrillic;
        this.increase = increase;
    }
}
