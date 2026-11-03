package uz.uzkassa.cts.dto.kapitallegal.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalCanPay;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;

import java.io.Serializable;

@Getter
@Setter
public class KapitalBankAccount implements Serializable {

    /**
     * Счет
     */
    private String account;
    /**
     * ID счета
     */
    @JsonProperty("aid")
    private Long accountId;
    /**
     * МФО
     */
    private String branch;
    /**
     * Признак, показывающий, можно ли проводить оплату со счета через API и Mobile24. 0 - нельзя, 1 - можно
     */
    @JsonProperty("canpay")
    private KapitalLegalCanPay canPay;
    /**
     * Обороты по кредиту по проведенным операциям за текущий операционный день на момент запроса
     */
    @JsonProperty("ct")
    private Long credit;
    /**
     * Обороты по дебету по проведенным операциям за текущий операционный день на момент запроса
     */
    @JsonProperty("dt")
    private Long debit;
    /**
     * Дата последней транзакции
     */
    @JsonProperty("l_date")
    private String lastTransactionDate;
    /**
     * Название счета
     */
    @JsonProperty("name")
    private String accountName;
    /**
     * Дата открытия счета
     */
    @JsonProperty("o_date")
    private String openDate;
    /**
     *
     */
    @JsonProperty("notif_in")
    private Long notif_in;
    /**
     *
     */
    @JsonProperty("notif_out")
    private Long notif_out;
    /**
     * Сальдо входящее - остаток на начало дня
     */
    @JsonProperty("s_in")
    private String balanceBeginOfDay;
    /**
     * Сальдо исходящее - остаток на конец дня (текущий остаток)
     */
    @JsonProperty("s_out")
    private String balanceCurrentOrEndOfDay;
    /**
     * Состояние счета
     */
    private KapitalLegalDocumentState state;
    /**
     * Код валюты
     */
    @JsonProperty("val")
    private String currencyCode;


}
