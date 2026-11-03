package uz.uzkassa.smartpos.integration.meningUyim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeningUyimPayment implements Serializable {
    private Integer id; //ID биллинговая система
    @JsonProperty("payment_status") //Статус платежа
    private Integer paymentStatus;
    private String description;
}
