package uz.uzkassa.horeca.filters.billing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AgreementFilter extends BillingBaseFilter implements Serializable {

    Long customerId;

    Long xizmatId;

    Long ctoId; // this is id of the company(in cto)

    String inn;

    String serviceType;

    String status;

    List<String> tins;

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
        if (tins != null) {
            map.put("tins", tins);
        }
        return map;
    }
}
