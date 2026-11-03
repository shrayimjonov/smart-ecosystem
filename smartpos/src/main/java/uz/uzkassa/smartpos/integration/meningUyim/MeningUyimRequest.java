package uz.uzkassa.smartpos.integration.meningUyim;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeningUyimRequest implements Serializable {
    @JsonProperty("subscriber_code") //Лицевой счет абонента
    private String subscriberCode;
    @JsonProperty("amount") //Сумма платежа
    private BigDecimal amount;
    @JsonProperty("payment_id") //ID биллинговая система
    private Integer paymentId;
    @JsonProperty("transaction_id") //Идентификатор транзакции в нашей системе
    private String transactionId;
}
