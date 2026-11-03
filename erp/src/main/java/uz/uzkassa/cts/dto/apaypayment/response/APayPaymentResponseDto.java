package uz.uzkassa.cts.dto.apaypayment.response;

import lombok.Data;
import uz.uzkassa.cts.enums.apay.APayPaymentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class APayPaymentResponseDto implements Serializable {
    private Long id;
    private String tin;
    private String deviceSerialNumber;
    private String companyName;
    private String accountNumber;
    private String bankCode;
    private String bankName;
    private String pan;
    private BigDecimal amount;
    private String billId;
    private APayPaymentStatus aPayPaymentStatus;
    private String payErrorMessage;
    private String checkErrorMessage;
    private String addErrorMessage;
    private LocalDateTime createdDate;
    private LocalDateTime confirmedDate;
}
