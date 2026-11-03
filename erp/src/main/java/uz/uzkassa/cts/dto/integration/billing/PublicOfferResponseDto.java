package uz.uzkassa.cts.dto.integration.billing;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.billing.BillingAppType;
import uz.uzkassa.cts.enums.billing.BillingDocumentType;

import java.io.Serializable;

@Getter
@Setter
public class PublicOfferResponseDto  implements Serializable {

    private String id;
    private String content;
    private Boolean active;
    private BillingAppType appType;
    private BillingDocumentType documentType;
}
