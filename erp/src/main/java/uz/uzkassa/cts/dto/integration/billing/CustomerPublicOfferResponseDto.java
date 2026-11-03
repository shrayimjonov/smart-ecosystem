package uz.uzkassa.cts.dto.integration.billing;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.billing.BillingDocumentType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerPublicOfferResponseDto implements Serializable {
    private String id;
    private String publicOfferId;
    private String publicOfferContent;
    private String customerId;
    private String customerTin;
    private LocalDateTime createdDate;
    private String fileUri;
    private BillingDocumentType documentType;
    private String contractNumber;
    private LocalDate contractDate;
}
