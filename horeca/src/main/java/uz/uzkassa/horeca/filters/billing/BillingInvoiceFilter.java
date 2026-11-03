package uz.uzkassa.horeca.filters.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.horeca.enums.billing.AppType;
import uz.uzkassa.horeca.enums.billing.BillingXfileStatus;
import uz.uzkassa.horeca.enums.billing.SortTypeEnum;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BillingInvoiceFilter extends BillingBaseFilter {
    BillingContractStatus status;
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
        if (getSearch() != null) {
            map.put("search", List.of(getSearch()));
        }

        if (getFrom() != null) {
            map.put("from", List.of(String.valueOf(getFrom())));
        }

        if (getTo() != null) {
            map.put("to", List.of(String.valueOf(getTo())));
        }
        if (getCompanyId() != null) {
            map.put("companyId", List.of(String.valueOf(getCompanyId())));
        }
        if (getCtoId() != null) {
            map.put("ctoId", List.of(String.valueOf(getCtoId())));
        }
        if (getBranchId() != null) {
            map.put("branchId", List.of(String.valueOf(getBranchId())));
        }
        if (getUserId() != null) {
            map.put("userId", List.of(String.valueOf(getUserId())));
        }
        if (getTerminalId() != null) {
            map.put("terminalId", List.of(String.valueOf(getTerminalId())));
        }
        if (getCityId() != null) {
            map.put("cityId", List.of(String.valueOf(getCityId())));
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
