package uz.uzkassa.cts.dto.integration.billing;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.enums.billing.BillingContractStatus;
import uz.uzkassa.cts.enums.billing.BillingXfileStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class InvoiceResponseDto implements Serializable {
    private String id;
    private String fileUri;
    private String fileName;
    private String contentType;
    @Enumerated(EnumType.STRING)
    private BillingContractStatus status;
    private Integer totalProductCount;
    private BigDecimal totalPriceWithVat;
    private BigDecimal totalPrice;
    private BigDecimal totalVatPrice;
    private Integer year;
    private String customerAddress;
    private String customerPublicOfferId;
    private String customerPublicOfferNumber;
    private LocalDateTime customerPublicOfferDate;
    private Long customerId;
    private String customerName;
    private String customerTin;
    private String xFileId;
    private BillingXfileStatus xFileStatus;
    private String documentNumber;
    private LocalDateTime invoiceDate;
}
