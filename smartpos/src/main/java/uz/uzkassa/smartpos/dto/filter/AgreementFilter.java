package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
public class AgreementFilter extends BaseFilter implements Serializable {
    Long customerId;
    Long xizmatId;
    Long ctoId;
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
