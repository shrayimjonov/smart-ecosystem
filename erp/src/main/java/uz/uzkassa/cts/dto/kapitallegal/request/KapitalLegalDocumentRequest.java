package uz.uzkassa.cts.dto.kapitallegal.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDirectionType;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentType;

import java.io.Serializable;

@Getter
@Setter
public class KapitalLegalDocumentRequest implements Serializable {
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
     * Сумма платежа
     */
    private String amount;
    /**
     * МФО банка, в котором создается транзакция
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
    private String purpose;
    /**
     * Назначение платежа
     */
    @JsonProperty("purp_code")
    private String purpCode;
    /**
     *
     */
    private KapitalLegalDocumentState state;
    /**
     * Уникальный номер документа, присваиваемый клиентской стороной при создании платежного поручения.
     * Чтобы исключить вероятность того, что уникальные номера у разных платежных поручений
     * совпадут - должно остоять из текущей даты, номера документа и логина
     * в формате: YYYYMMDDH24MISSFFFNUMLOGIN, где YYYY-год, MM-месяц, DD-день, H24-часы в 24-х часовом формате,
     * MI-минуты, SS-секунды, FFF-миллисекунды, NUM-номер документа, LOGIN-логин клиента.
     */
    private String uniq;
}
