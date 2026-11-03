package uz.uzkassa.smartpossupply.integration.apay.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class APayAddTransactionResponseDto implements Serializable {

    private String id;

    private String status;

    private LocalDateTime time;
    private String localId;
    private String maskedPhoneNumber;

    public APayAddTransactionResponseDto() {}
}
