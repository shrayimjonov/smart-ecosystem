package uz.uzkassa.smartpossupply.dto.integration.agreement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpossupply.enums.PricingModel;
import uz.uzkassa.smartpossupply.integration.filter.BillingPageFilter;

import java.util.List;

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
    private Boolean enableForSingle;

    @Override
    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (serviceType != null) {
            map.put("serviceType", List.of(serviceType));
        }
        if (isReccurring != null) {
            map.put("isReccurring", List.of(String.valueOf(isReccurring)));
        }
        if (pricingModel != null) {
            map.put("pricingModel", List.of(pricingModel.name()));
        }
        if (branchInstance != null) {
            map.put("branchInstance", List.of(String.valueOf(branchInstance)));
        }
        if (companyInn != null) {
            map.put("companyInn", List.of(companyInn));
        }
        if (grouped != null) {
            map.put("grouped", List.of(String.valueOf(grouped)));
        }
        if (displayInCabinet != null) {
            map.put("displayInCabinet", List.of(String.valueOf(displayInCabinet)));
        }
        if (enableForSingle != null) {
            map.put("enableForSingle", List.of(String.valueOf(enableForSingle)));
        }
        return map;
    }
}
