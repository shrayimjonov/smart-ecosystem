package uz.uzkassa.cts.dto.apay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class APayAddTransactionResponseDto implements Serializable {
    private String id;
    @JsonProperty("status")
    private APayStatus status;
    private LocalDateTime time;
    private Integer localId;
    private String maskedPhoneNumber;
    private String qrCode;
}
