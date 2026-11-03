package uz.uzkassa.smartpos.integration.apay.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.integration.enums.APayStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class APayAddTransactionResponseDto implements Serializable {
    private String id;
    private APayStatus status;
    private LocalDateTime time;
    private String localId;
    private String maskedPhoneNumber;

}
