package uz.uzkassa.smartpossupply.dto.integration.quote;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.BillingContractStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class QuoteResponseDto implements Serializable {

    private String id;
    private LocalDateTime createdDate;
    private String fileUri;
    private String fileName;
    private String contentType;
    private Integer totalProductCount;
    private BigDecimal totalPriceWithVat;
    private BigDecimal totalPrice;
    private BigDecimal totalVatPrice;
    private String documentNumber;
    private BillingContractStatus status;
    private String customerName;
    private String customerId;
    private String tin;
    private String customerOKED;
    private String customerAddress;
    private String publicOfferNumber;
    private String publicOfferId;
    private LocalDateTime publicOfferCreateDate;
}
