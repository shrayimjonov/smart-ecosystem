package uz.uzkassa.cts.filter.billing;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgreementFilter extends BillingBaseFilter {
    Long customerId;

    Long xizmatId;

    Long ctoId;// this is id of the company(in cto)

    @NotBlank
    String inn;

    String serviceType;

    String status;

    Boolean recurring;

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (customerId != null) {
            map.put("customerId", List.of(String.valueOf(customerId)));
        }
        if (xizmatId != null) {
            map.put("xizmatId", List.of(String.valueOf(xizmatId)));
        }
        if (ctoId != null) {
            map.put("ctoId", List.of(String.valueOf(ctoId)));
        }
        if (inn != null) {
            map.put("inn", List.of(inn));
        }
        if (serviceType != null) {
            map.put("serviceType", List.of(serviceType));
        }
        if (status != null) {
            map.put("status", List.of(status));
        }
        if (recurring != null) {
            map.put("recurring", List.of(String.valueOf(recurring)));
        }
        return map;
    }
}
