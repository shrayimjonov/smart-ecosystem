package uz.uzkassa.smartpos.integration.cto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ActOfTransmissionSmartposDTO implements Serializable {
    private Long id;
    private String number;
    private String model;
    private String serialNumber;
    private String customerBranch;
    private LocalDateTime createdDate;
    private LocalDateTime acceptDate;
    private LocalDate nextPaymentDate;
    private BigDecimal price;
    private String fileUri;
    private String fileName;
}
