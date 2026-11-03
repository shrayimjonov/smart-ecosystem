package uz.uzkassa.cts.enums.kapitalbank;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum KapitalLegalDayStatus {

    OPERATION_DAY_OPEN(0, "OPERATION_DAY_OPEN",
            "отчет не окончательный, т.е. операционный день не закрыт и могут быть ещё поступления",
            "ҳисобот якуний емас, яъни. иш куни ёпиқ емас ва яна тушумлар бўлиши мумкин",
            "hisobot yakuniy emas, ya'ni. ish kuni yopiq emas va yana tushumlar bo'lishi mumkin"),
    FINAL_RESULT(1, "FINAL_RESULT", "окончательный результат",
            "якуний натижа", "yakuniy natija");

    private final Integer id;
    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    KapitalLegalDayStatus(Integer id, String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.id = id;
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }
}
