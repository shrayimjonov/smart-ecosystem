package uz.uzkassa.cts.enums.kapitalbank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum KapitalLegalDocumentType {
    @JsonProperty("01")
    PAYMENT_ORDER("01", "PAYMENT_ORDER", "Платежное поручение",
            "Тўлов топшириқномаси", "To'lov topshiriqnomasi"),
    @JsonProperty("02")
    PAYMENT_CLAIM("02", "PAYMENT_CLAIM", "Платежное требованиe",
            "Тўлов талабномаси", "To`lov talabnomasi"),
    @JsonProperty("03")
    ACCOUNT_CASH_WARRANT("03", "ACCOUNT_CASH_WARRANT", "Расходный кассовый ордер",
        "Харажат касса ордери", "Xarajat kassa orderi"),
    @JsonProperty("04")
    LIMITED_CHEQUE("04", "LIMITED_CHEQUE", "Лимитированные чеки",
        "Чекланган чеклар", "Cheklangan cheklar"),
    @JsonProperty("05")
    LETTER_OF_CREDIT_APPLICATION("05", "LETTER_OF_CREDIT_APPLICATION", "Заявление на аккредитив",
            "Aккредитивга ариза", "Akkreditivga ariza"),
    @JsonProperty("06")
    MEMORIAL_ORDER("06", "MEMORIAL_ORDER", "Мемориальный ордер",
            "Мемориал ордер", "Memorial order"),
    @JsonProperty("08")
    CORRECTIONAL_MEMORIAL_ORDER("08", "CORRECTIONAL_MEMORIAL_ORDER", "Исправительный мемориальный ор",
            "ANOR тизими орқали 24/7 тўлов", "ANOR tizimi orqali 24/7 to'lov"),
    @JsonProperty("09")
    ELECTRON_CREDIT_CARD("09", "ELECTRON_CREDIT_CARD", "Электронная кредитная карточка",
        "Электрон кредит карталар", "Elektron kredit kartalar"),
    @JsonProperty("11")
    COLLECTION_ORDER("11", "COLLECTION_ORDER", "Инкассовое поручение",
            "Инкасса топшириқномаси", "Inkassa topshiriqnomasi"),
    @JsonProperty("12")
    PAYMENT_REQUEST_ORDER("12", "PAYMENT_REQUEST_ORDER", "Платежное требование поручение",
        "Тулов талабномаси", "To'lov talabnomasi"),
    @JsonProperty("13")
    INCOMING_PAYMENT_ORDER("13", "INCOMING_PAYMENT_ORDER", "Входящее платежное поручение",
        "Кирувчи тўлов топшириқномаси", "Kiruvchi to'lov topshiriqnomasi"),
    @JsonProperty("14")
    COMMERCIAL_CHECKS("14", "COMMERCIAL_CHECKS", "Расчетные чеки коммерческих ба",
            "Тижорат банкларининг чеклари", "Tijorat banklarining cheklari"),
    @JsonProperty("16")
    PAYMENT_ORDER_SWIFT("16", "PAYMENT_ORDER_SWIFT", "ПП валюта SWIFT",
        "ТТ валюта SWIFT", "TT valyuta SWIFT"),
    @JsonProperty("21")
    PAYMENT_24_7_VIA_ANOR("21", "PAYMENT_24_7_VIA_ANOR", "Платеж 24/7 через систему ANOR",
            "ANOR тизими орқали 24/7 тўлов", "ANOR tizimi orqali 24/7 to'lov"),
    @JsonProperty("22")
    PAYMENT_CLAIM_CURRENCY("22", "PAYMENT_CLAIM_CURRENCY", "ПТ валюта",
        "Тўлов талабномаси валюта", "To`lov talabnomasi valyuta"),
    @JsonProperty("31")
    ANNOUNCEMENT_CASH_DEPOSIT("31", "ANNOUNCEMENT_CASH_DEPOSIT", "Объявл. о взносе наличн.",
        "Нақд депозит ҳақида эслатма", "Naqd depozit haqida eslatma"),
    @JsonProperty("32")
    RECEIPT_CASH_ORDER("32", "RECEIPT_CASH_ORDER", "Приходный кассовый ордер",
        "Кирим касса ордери", "kirim kassa orderi"),
    @JsonProperty("33")
    STATEMENT_CASH_CONTRIBUTION("33", "STATEMENT_CASH_CONTRIBUTION", "Заявл. на взнос нал.инвал",
        "Заявл. на взнос нал.инвал", "Заявл. на взнос нал.инвал"),
    @JsonProperty("34")
    LE_APPL_FOR_DISABILITY_CASH_CONTRIBUTION("34", "LE_APPL_FOR_DISABILITY_CASH_CONTRIBUTION", "ЮЛ Заявление.на взнос.нал.инвал",
        "ЮЛ Заявление.на взнос.нал.инвал", "ЮЛ Заявление.на взнос.нал.инвал"),
    @JsonProperty("35")
    SIMPLE_PAYMENT_ORDER("35", "SIMPLE_PAYMENT_ORDER", "простое платежное поручение",
            "Оддий тўлов топшириҚномаси", "Oddiy to`lov topshiriqnomasi"),
    @JsonProperty("36")
    CASH_RECEIPT_ORDER_SWIFT("36", "CASH_RECEIPT_ORDER_SWIFT", "Приходный кас ордер-SWIFT",
        "Приходный кас ордер-SWIFT", "Приходный кас ордер-SWIFT"),

    @JsonProperty("61")
    INCOMING_DEBIT("61", "INCOMING_DEBIT", "Входящий дебетовый",
        "Входящий дебетовый", "Входящий дебетовый"),
    @JsonProperty("62")
    MO_CURRENCY("62", "MO_CURRENCY", "МО валюта",
        "МО валюта", "МО валюта"),
    @JsonProperty("64")
    MO_OUTGOING("64", "MO_OUTGOING", "МО исходящий",
        "МО исходящий", "МО исходящий"),
    @JsonProperty("65")
    INCOMING_MEMORDER("65", "INCOMING_MEMORDER", "Входящий мемордер",
        "Входящий мемордер", "Входящий мемордер"),
    @JsonProperty("68")
    OFF_BALANCE_SHEET_MO_SUMS("68", "OFF_BALANCE_SHEET_MO_SUMS", "Внебалансовый МО сумы",
        "Внебалансовый МО сумы", "Внебалансовый МО сумы"),
    @JsonProperty("69")
    OFF_BALANCE_SHEET_MO_CURRENCY("69", "OFF_BALANCE_SHEET_MO_CURRENCY", " Внебалансовый МО валюта",
        " Внебалансовый МО валюта", " Внебалансовый МО валюта"),
    @JsonProperty("71")
    CASH_RECEIPT("71", "CASH_RECEIPT", "Кассовый чек",
        "Кассовый чек", "Кассовый чек"),
    @JsonProperty("72")
    ACCOUNT_CASH_WARRANT_72("72", "ACCOUNT_CASH_WARRANT_72", "Расходный кассовый ордер",
        "Расходный кассовый ордер", "Расходный кассовый ордер"),
    @JsonProperty("73")
    APPL_FOR_ISSUE_OF_FOREIGN_CURRENCY("73", "APPL_FOR_ISSUE_OF_FOREIGN_CURRENCY", "Заявл. на выдачу инвалюты",
        "Заявл. на выдачу инвалюты", "Заявл. на выдачу инвалюты"),
    @JsonProperty("74")
    LE_APPL_FOR_ISSUE_OF_FOREIGN_CURRENCY("74", "LE_APPL_FOR_ISSUE_OF_FOREIGN_CURRENCY", "ЮЛ Заявл. на выдач инвал.",
        "ЮЛ Заявл. на выдач инвал.", "ЮЛ Заявл. на выдач инвал."),
    @JsonProperty("75")
    EXPENDITURE_CASH_ORDER_DEP("75", "EXPENDITURE_CASH_ORDER_DEP", "Расходный кас ордер - деп",
        "Расходный кас ордер - деп", "Расходный кас ордер - деп"),
    @JsonProperty("82")
    CORRECTIONAL_MO_CURRENCY("82", "CORRECTIONAL_MO_CURRENCY", "Исправительный МО валюта",
        "Исправительный МО валюта", "Исправительный МО валюта"),
    @JsonProperty("91")
    RECEIPT_ORDER_FOR_NO("91", "RECEIPT_ORDER_FOR_NO", "Приходный ордер по НО",
        "Приходный ордер по НО", "Приходный ордер по НО"),
    @JsonProperty("92")
    EXPENDITURE_ORDER_FOR_NO("92", "EXPENDITURE_ORDER_FOR_NO", "Расходный ордер по НО",
        "Расходный ордер по НО", "Расходный ордер по НО"),
    @JsonProperty("93")
    WRITE_OFF_ORDER("93", "WRITE_OFF_ORDER", " Ордер списания бланков",
        " Ордер списания бланков", " Ордер списания бланков"),
    @JsonProperty("97")
    CREDIT_CARD_RECHARGE("97", "CREDIT_CARD_RECHARGE", "пополнение пластиковой карты",
            "кредит картани тўлдириш", "kredit kartani to'ldirish"),
    @JsonProperty("98")
    PAYMENT_TO_THE_TREASURY("98", "PAYMENT_TO_THE_TREASURY", "платеж в казначейство",
            "хазинага тўлов", "xazinaga to'lov"),
    @JsonProperty("99")
    PAYMENT_TO_THE_BUDGET("99", "PAYMENT_TO_THE_BUDGET", "платеж в бюджет",
            "бюджетга тўлов", "byudjetga to'lov"),


    @JsonProperty("111")
    COLLECTION_ORDER_111("111", "COLLECTION_ORDER_111", "Инкассовое поручение",
                                 "Инкассовое поручение", "Инкассовое поручение"),
    @JsonProperty("211")
    PAYMENT_ORDER_VIA_REMOTE_SYSTEM("211", "PAYMENT_ORDER_VIA_REMOTE_SYSTEM", "Пл.поруч.через сист.дист.",
                                 "Пл.поруч.через сист.дист.", "Пл.поруч.через сист.дист."),

    @JsonProperty("1006")
    MO_SUMS_DEBIT_FROM_CARDS("1006", "MO_SUMS_DEBIT_FROM_CARDS", "МО сумы (списание с карт)",
                                 "МО сумы (списание с карт)", "МО сумы (списание с карт)"),
    @JsonProperty("9004")
    REFERENCE_09004_OP("9004", "REFERENCE_09004_OP", "Справка 09004 ОП",
                                 "Справка 09004 ОП", "Справка 09004 ОП"),
    @JsonProperty("9005")
    REFERENCE_09005_OP("9005", "REFERENCE_09005_OP", "Справка 09005 ОП",
        "Справка 09005 ОП", "Справка 09005 ОП"),
    @JsonProperty("90020")
    REFERENCE_090020_OP("90020", "REFERENCE_090020_OP", "Справка 09002 ОП",
        "Справка 09002 ОП", "Справка 09002 ОП"),
    @JsonProperty("90021")
    REFERENCE_090021_OP("90021", "REFERENCE_090021_OP", "Справка 090021 ОП",
        "Справка 090021 ОП", "Справка 090021 ОП"),
    @JsonProperty("90070")
    REFERENCE_090070_OP("90070", "REFERENCE_090070_OP", "Справка 090070 ОП",
        "Справка 090070 ОП", "Справка 090070 ОП"),
    @JsonProperty("90071")
    REFERENCE_090071_OP("90071", "REFERENCE_090071_OP", "Справка 090071 ОП",
        "Справка 090071 ОП", "Справка 090071 ОП"),
    @JsonProperty("90120")
    REFERENCE_090120_OP("90120", "REFERENCE_090120_OP", "Справка 090120 ОП",
        "Справка 090120 ОП", "Справка 090120 ОП"),
    @JsonProperty("90121")
    REFERENCE_090121_OP("90121", "REFERENCE_090121_OP", "Справка 090121 ОП",
        "Справка 090121 ОП", "Справка 090121 ОП"),
    @JsonProperty("90130")
    REFERENCE_090130_OP("90130", "REFERENCE_090130_OP", "Справка 090130 ОП",
        "Справка 090130 ОП", "Справка 090130 ОП"),
    @JsonProperty("90131")
    REFERENCE_090131_OP("90131", "REFERENCE_090131_OP", "Справка 090131 ОП",
        "Справка 090131 ОП", "Справка 090131 ОП"),
    @JsonProperty("90150")
    REFERENCE_090150_OP("90150", "REFERENCE_090150_OP", "Справка 090150 ОП",
        "Справка 090150 ОП", "Справка 090150 ОП"),
    @JsonProperty("90151")
    REFERENCE_090151_OP("90151", "REFERENCE_090151_OP", "Справка 090151 ОП",
        "Справка 090151 ОП", "Справка 090151 ОП");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;
    private final String number;


    KapitalLegalDocumentType(String number, String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
        this.number = number;
    }


    @JsonValue
    public static KapitalLegalDocumentType get(String code) {
        for (KapitalLegalDocumentType documentType : KapitalLegalDocumentType.values()) {
            if (code.equals(documentType.getCode())) {
                return documentType;
            }
        }
        return null;
    }
}
