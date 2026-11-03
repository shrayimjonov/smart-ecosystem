package uz.uzkassa.cts.dto.onetimeorderinvoice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class OneTimeOrderInvoice1cResponse implements Serializable {
    @JsonProperty("ДатаТранзакции")
    private String transactionDate;
    @JsonProperty("НомерТранзакции")
    private Long id;
    @JsonProperty("ПлательщикИНН")
    private String customerTin;
    @JsonProperty("СчетПлательщика")
    private String customerAccountNumber;
    @JsonProperty("ПлательщикНаименование")
    private String customerName;
    @JsonProperty("Сумма")
    private BigDecimal amount;
    @JsonProperty("Тип оплаты")
    private String paymentType;
}
