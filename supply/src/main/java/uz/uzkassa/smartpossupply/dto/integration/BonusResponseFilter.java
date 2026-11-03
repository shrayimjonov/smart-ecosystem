package uz.uzkassa.smartpossupply.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.BillingContractStatus;
import uz.uzkassa.smartpossupply.enums.SortTypeEnum;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BonusResponseFilter extends IntegrationBaseFilter {
    private LocalDate from;
    private LocalDate to;
    private AppType appType;
    private BillingContractStatus invoiceStatus;
    private String orderBy = "createdDate";
    private List<String> managerId;

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    @JsonIgnore

    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortTypeEnum.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();
        if (orderBy != null) {
            map.put("orderBy", List.of(orderBy));
        }
        if (sortOrder != null) {
            map.put("sortOrder", List.of(sortOrder.name()));
        }
        if (from != null) {
            map.put("from", List.of(from.toString()));
        }
        if (to != null) {
            map.put("to", List.of(to.toString()));
        }
        if (managerId != null) {
            map.put("managerId", managerId);
        }
        if (appType != null) {
            map.put("appType", List.of(appType.toString()));
        }
        if (invoiceStatus != null) {
            map.put("invoiceStatus", List.of(invoiceStatus.toString()));
        }
        return map;
    }
}
