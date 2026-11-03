package uz.uzkassa.smartpos.dto.integration.agreement;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.dto.filter.IntegrationBaseFilter;
import uz.uzkassa.smartpos.dto.integration.enums.PricingModel;

import java.util.List;

@Getter
@Setter
public class XizmatFilter extends IntegrationBaseFilter {
    private String serviceType;
    private Boolean isReccurring;
    private PricingModel pricingModel;
    private boolean withActiveAgreementCounts = false;
    private boolean all = false;
    private Boolean disabled = false;
    @NotNull
    private Integer branchInstance = 1;
    @NotNull
    @NotEmpty
    private String companyInn;
    private Boolean grouped;
    private Boolean displayInCabinet;

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
        if (disabled != null) {
            map.put("disabled", List.of(String.valueOf(disabled)));
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
        return map;
    }
}
