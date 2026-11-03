package uz.uzkassa.cts.filter.billing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.cts.enums.billing.PricingModel;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XizmatFilter extends IntegrationBaseFilter {
    String serviceType;
    Boolean isReccurring;
    PricingModel pricingModel;
    boolean withActiveAgreementCounts = false;
    boolean all = false;
    Integer branchInstance = 1;
    String companyInn;
    Boolean grouped;

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
        map.put("all", List.of(String.valueOf(all)));
        if (branchInstance != null) {
            map.put("branchInstance", List.of(String.valueOf(branchInstance)));
        }
        if (companyInn != null) {
            map.put("companyInn", List.of(companyInn));
        }
        if (grouped != null) {
            map.put("grouped", List.of(String.valueOf(grouped)));
        }
        return map;
    }
}
