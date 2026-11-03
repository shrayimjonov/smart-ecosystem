package uz.uzkassa.smartpos.integration.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.dto.filter.PageFilter;
import uz.uzkassa.smartpos.dto.integration.quote.ContractStatus;
import uz.uzkassa.smartpos.enums.AppType;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.integration.enums.BillingXfileStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BillingInvoiceFilter extends PageFilter {
    private String search;
    private LocalDate from;
    private LocalDate to;
    private Long companyId;
    private String tin;
    private Long ctoId;
    protected Long branchId;
    private Long userId;
    private Long terminalId;
    private Long cityId;
    private ContractStatus status;
    private Long docNumber;
    private AppType appType;
    private Long xizmatId;
    private BillingXfileStatus xfileStatus;

    private String orderBy = "createdDate";

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    @JsonIgnore
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortTypeEnum.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (search != null) {
            map.put("search", List.of(search));
        }

        if (from != null) {
            map.put("from", List.of(String.valueOf(from)));
        }

        if (to != null) {
            map.put("to", List.of(String.valueOf(to)));
        }
        if (companyId != null) {
            map.put("companyId", List.of(String.valueOf(companyId)));
        }
        if (tin != null) {
            map.put("tin", List.of(tin));
        }
        if (ctoId != null) {
            map.put("ctoId", List.of(String.valueOf(ctoId)));
        }
        if (branchId != null) {
            map.put("branchId", List.of(String.valueOf(branchId)));
        }
        if (userId != null) {
            map.put("userId", List.of(String.valueOf(userId)));
        }
        if (terminalId != null) {
            map.put("terminalId", List.of(String.valueOf(terminalId)));
        }
        if (cityId != null) {
            map.put("cityId", List.of(String.valueOf(cityId)));
        }
        if (status != null) {
            map.put("status", List.of(status.name()));
        }
        if (docNumber != null) {
            map.put("docNumber", List.of(String.valueOf(docNumber)));
        }
        if (appType != null) {
            map.put("appType", List.of(String.valueOf(appType)));
        }
        if (xizmatId != null) {
            map.put("xizmatId", List.of(String.valueOf(xizmatId)));
        }
        if (xfileStatus != null) {
            map.put("xfileStatus", List.of(xfileStatus.name()));
        }
        if (orderBy != null) {
            map.put("orderBy", List.of(orderBy));
        }
        if (sortOrder != null) {
            map.put("sortOrder", List.of(sortOrder.name()));
        }
        return map;
    }
}
