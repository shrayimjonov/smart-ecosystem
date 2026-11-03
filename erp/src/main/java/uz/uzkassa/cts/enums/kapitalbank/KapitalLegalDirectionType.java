package uz.uzkassa.cts.enums.kapitalbank;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum KapitalLegalDirectionType {
    TRANSFER_BETWEEN_OWN_ACCOUNTS(0, "TRANSFER_BETWEEN_OWN_ACCOUNTS",
            "переброска между своими счетами",
            "ўз ҳисоб варақлари ўртасида пул ўтказиш",
            "o`z hisob varaqlari o'rtasida pul o'tkazish"),
    OUTCOME(1, "OUTCOME", "исходящий платеж",
            "чиқувчи тўлов", "chiquvchi to'lov"),
    INCOME(2, "INCOME", "входящий платеж",
            "кирувчи тўлов", "kiruvchi to'lov");

    private final Integer id;
    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    KapitalLegalDirectionType(Integer id, String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.id = id;
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }
}
