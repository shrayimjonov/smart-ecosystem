package uz.uzkassa.silen.integration.billing.dto.customerpublicoffer;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.integration.billing.enums.BillingDocumentType;

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
