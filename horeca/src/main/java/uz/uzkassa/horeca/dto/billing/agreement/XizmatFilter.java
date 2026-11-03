package uz.uzkassa.horeca.dto.billing.agreement;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.enums.billing.PricingModel;
import uz.uzkassa.horeca.filters.billing.BillingPageFilter;

@Getter
@Setter
public class XizmatFilter extends BillingPageFilter {

    private String serviceType;
    private Boolean isReccurring;
    private PricingModel pricingModel;
    private boolean withActiveAgreementCounts = false;
    private boolean all = false;
    private Integer branchInstance = 1;
    private Boolean grouped;
    private String companyInn;
    private Boolean displayInCabinet;
}
