package uz.uzkassa.cts.dto.orderspecification.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ServiceOrderSpecificationListDto implements Serializable {
    private Long id;
    private String tin;
    private String customerName;
    private Long customerId;
    private Long contractId;
    private String contractNumber;
    private LocalDateTime contractDate;
    private String specificationNumber;
    private LocalDateTime specificationDate;
    private BigDecimal totalPrice;
    private BigDecimal totalVat;
    private BigDecimal amount;
    private ContractStatus status;
    private String specFileUri;
    private String specFileName;
    private String specContentType;
}
