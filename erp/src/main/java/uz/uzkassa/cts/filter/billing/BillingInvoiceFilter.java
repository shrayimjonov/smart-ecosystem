package uz.uzkassa.cts.filter.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.cts.enums.billing.BillingAppType;
import uz.uzkassa.cts.enums.billing.BillingContractStatus;
import uz.uzkassa.cts.enums.billing.BillingXfileStatus;
import uz.uzkassa.cts.enums.SortType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BillingInvoiceFilter extends IntegrationBaseFilter {
    private LocalDate from;
    private LocalDate to;
    BillingContractStatus status;
    private Long docNumber;
    private BillingAppType appType;
    private Long xizmatId;
    private BillingXfileStatus xfileStatus;

    private String orderBy = "createdDate";

    SortType sortOrder = SortType.desc;

    @JsonIgnore
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortType.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (from != null) {
            map.put("from", List.of(String.valueOf(from)));
        }
        if (to != null) {
            map.put("to", List.of(String.valueOf(to)));
        }
        if (status != null) {
            map.put("status", List.of(status.name()));
        }
        if (docNumber != null) {
            map.put("docNumber", List.of(String.valueOf(docNumber)));
        }
        if (appType != null) {
            map.put("appType", List.of(appType.name()));
        }
        if (xizmatId != null) {
            map.put("xizmatId", List.of(String.valueOf(xizmatId)));
        }
        if (xfileStatus != null) {
            map.put("xfileStatus", List.of(xfileStatus.name()));
        }
        return map;
    }
}
