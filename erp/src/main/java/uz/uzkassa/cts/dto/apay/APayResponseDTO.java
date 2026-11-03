package uz.uzkassa.cts.dto.apay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class APayResponseDTO implements Serializable {
    private String fiscalSign;
    @JsonProperty("status")
    private APayStatus status;
    private String qrCodeURL;
    private String terminalId;
    private LocalDateTime transactionDate;
}
