package uz.uzkassa.cts.dto.onetimeorder.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OneTimeOrderResponseDto implements Serializable {
    private Long id;
    private Long contractId;
    private String contractName;
    private Long customerId;
    private String customerName;
    private String tin;
    private Long serviceId;
    private String serviceName;
    private String fileUri;
    private String fileName;
    private String contentType;
    private Integer totalProductCount;
    private BigDecimal totalSellPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalVatPrice;
    private String totalVatPriceInWord;
    private String totalSellPriceInWord;
    private String totalPriceInWord;
    private ContractStatus status;
    private LocalDateTime createdDate;
    private String number;
    private Long creatorId;
    private String creatorFullName;
    private Long lastModifierId;
    private String lastModifierFullName;
    private String serialNumberKKM;
}
