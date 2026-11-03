package uz.uzkassa.smartpossupply.dto.integration.billinginvoice;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.BillingContractStatus;
import uz.uzkassa.smartpossupply.enums.BillingXfileStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
