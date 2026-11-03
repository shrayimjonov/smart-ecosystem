package uz.uzkassa.smartpos.integration.billing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.dto.filter.IntegrationBaseFilter;
import uz.uzkassa.smartpos.integration.enums.AppType;
import uz.uzkassa.smartpos.integration.enums.BillingPromotionStatus;
import uz.uzkassa.smartpos.integration.enums.BillingPromotionType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BillingPromotionFilter extends IntegrationBaseFilter {

    private AppType appType;

    private BillingPromotionType promotionType;

    private LocalDate from;

    private LocalDate to;

    private BillingPromotionStatus status;

    public MultiValueMap<String, String> getMultiValueMap() {

        var map = super.getMultiValueMap();

        if (appType != null) {
            map.put("appType", List.of(appType.getCode()));
        }
        if (promotionType != null) {
            map.put("promotionType", List.of(promotionType.getCode()));
        }
        if (status != null) {
            map.put("status", List.of(status.getCode()));
        }
        if (from != null) {
            map.put("from", List.of(String.valueOf(from)));
        }
        if (to != null) {
            map.put("to", List.of(String.valueOf(to)));
        }
        return map;
    }
}