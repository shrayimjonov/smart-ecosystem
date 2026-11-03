package uz.uzkassa.cts.dto.billing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.billing.BillingDocumentType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingInvoicePayloadDTO implements Serializable {
    Long customerId;
    Integer totalProductCount;
    BigDecimal totalPriceWithVat;
    String appType;
    List<BillingInvoiceDetailDTO> details;
    Long customerParentId;
    String noteFromExternalSystem;
    Long agreementId;
    String tin;
    BillingDocumentType documentType;
    LocalDate contractDate;
    String contractNumber;
//    @JsonIgnore
    Long billingInvoiceId;

    @Override
    public String toString() {
        return "ActOfPaymentScheduleDTO{" +
                "customerId=" + customerId +
                ", totalProductCount=" + totalProductCount +
                ", totalPriceWithVat=" + totalPriceWithVat +
                ", appType='" + appType + '\'' +
                ", details=" + details +
                ", customerParentId=" + customerParentId +
                ", noteFromExternalSystem='" + noteFromExternalSystem + '\'' +
                ", agreementId=" + agreementId +
                ", tin='" + tin + '\'' +
                '}';
    }
}
