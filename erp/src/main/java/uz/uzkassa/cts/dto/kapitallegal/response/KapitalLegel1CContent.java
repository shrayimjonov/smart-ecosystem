package uz.uzkassa.cts.dto.kapitallegal.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDirectionType;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentType;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KapitalLegel1CContent {

    /**
     * Счет получателя
     */
    @JsonProperty("acc_ct")
    private String accountCredit;

    /**
     * Счет отправителя
     */
    @JsonProperty("acc_dt")
    private String accountDebit;

    /**
     * Сумма операции
     */
    private String amount;

    /**
     * МФО
     */
    private String branch;

    /**
     * Дата документа
     */
    @JsonProperty("ddate")
    private String documentDate;

    /**
     * 0 - переброска между своими счетами,
     * 1 - исходящий платеж, 2 - входящий платеж
     */
    @JsonProperty("dir")
    private KapitalLegalDirectionType directionType;

    /**
     * Код типадокумента .
     * "01"-Платежное поручение
     * "02"-Платежное требованиe
     * "05"-Заявление на аккредитив
     * "06"-Мемориальный ордер
     * "08"-Исправительный мемориальный ор
     * "11"-Инкассовое поручение
     * "14"-Расчетные чеки коммерческих б
     * "21"-Платеж 24/7 через систему ANOR,
     * "35"-простое платежное поручение,
     * "97"-пополнение пластиковой карты,
     * "98"-платеж в казначейство,
     * "99"-платеж в бюджет
     */
    @JsonProperty("dtype")
    private KapitalLegalDocumentType documentType;

    /**
     * Код ошибки, возникшей при действии с документом в АБС
     */
    @JsonProperty("err")
    private String absError;

    /**
     * Описание ошибки АБС
     */
    @JsonProperty("err_msg")
    private String absErrorMsgDescription;

    /**
     * ID документа в АБС
     */
    @JsonProperty("general_id")
    private String absId;

    /**
     * ИНН получателя
     */
    @JsonProperty("inn_ct")
    private String tinCredit;
    /**
     * ИНН отправителя
     */
    @JsonProperty("inn_dt")
    private String tinDebit;
    //
    /**
     * МФО получателя
     */
    @JsonProperty("mfo_ct")
    private String mfoCredit;

    /**
     * МФО отправителя
     */
    @JsonProperty("mfo_dt")
    private String mfoDebit;

    /**
     * Наименование получателя
     */
    @JsonProperty("name_ct")
    private String nameCredit;

    /**
     * Наименование отправителя
     */
    @JsonProperty("name_dt")
    private String nameDebit;

    /**
     * Номер документа
     */
    @JsonProperty("num")
    private String documentNumber;

    /**
     * Код назначения платежа
     */
    @JsonProperty("purp_code")
    private String purpCode;

    /**
     * Назначение платежа
     */
    private String purpose;
    /**
     * Cостояние платежа
     */
    private KapitalLegalDocumentState state;

    /**
     * Уникальный номер документа, присваиваемый клиентской стороной при создании платежного поручения
     */
    private String uniq;

    /**
     * Дата и время проведения платежа
     */
    private String time;
}
