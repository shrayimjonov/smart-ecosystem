package uz.uzkassa.smartpos.integration.meningUyim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeningUyimAccount implements Serializable {
    @JsonProperty("subscriber_code")
    private String subscriberCode;
    @JsonProperty("subscriber_name")
    private String subscriberName;
    @JsonProperty("subscriber_adress")
    private String subscriberAdress;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("current_balance")
    private BigDecimal currentBalance;

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = Optional.ofNullable(currentBalance)
            .map(BigDecimal::negate)
            .orElse(null);
    }
}
