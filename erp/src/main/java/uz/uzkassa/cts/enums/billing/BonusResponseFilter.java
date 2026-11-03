package uz.uzkassa.cts.enums.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import uz.uzkassa.cts.enums.SortType;
import uz.uzkassa.cts.filter.billing.IntegrationBaseFilter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BonusResponseFilter extends IntegrationBaseFilter {
    LocalDate from;
    LocalDate to;
    BillingAppType appType;
    BillingContractStatus invoiceStatus;
    String orderBy = "createdDate";
    //    String managerId;
    List<String> managerId;

    SortType sortOrder = SortType.desc;

    @JsonIgnore
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortType.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }
}
