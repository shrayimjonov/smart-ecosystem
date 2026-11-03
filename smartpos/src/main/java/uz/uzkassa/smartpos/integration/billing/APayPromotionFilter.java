package uz.uzkassa.smartpos.integration.billing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.dto.filter.IntegrationBaseFilter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class APayPromotionFilter extends IntegrationBaseFilter {
    private LocalDate from;
    private LocalDate to;
    private String appType;
    private Boolean active;
    private String orderBy = "createdDate";

    public MultiValueMap<String, String> getMultiValueMap() {

        var map = super.getMultiValueMap();

        if (appType != null) {
            map.put("appType", List.of(appType));
        }
        if (active != null) {
            map.put("active", List.of(String.valueOf(active)));
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