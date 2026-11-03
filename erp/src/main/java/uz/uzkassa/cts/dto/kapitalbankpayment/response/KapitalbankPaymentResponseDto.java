package uz.uzkassa.cts.dto.kapitalbankpayment.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.KapitalbankPaymentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class KapitalbankPaymentResponseDto implements Serializable {

    private Long id;
    private String tin;
    private String deviceSerialNumber;
    private String companyName;
    private String accountNumber;
    private String bankCode;
    private String bankName;
    private String pan;
    private String expiry;
    private BigDecimal amount;
    private Integer transactionId;
    private String maskedPhoneNumber;
    private String errorMessage;
    private String status;
    private LocalDateTime timestamp;

    private String payData;
    private String payErrorMessage;
    private String payStatus;
    private LocalDateTime payTimestamp;
    private KapitalbankPaymentStatus kapitalbankPaymentStatus;
}
