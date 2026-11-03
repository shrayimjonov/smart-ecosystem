package uz.uzkassa.cts.filter.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import uz.uzkassa.cts.enums.SortType;

import java.io.Serializable;

@Getter
@Setter
public class BillingCategoryFilter extends PageFilter implements Serializable {

    private String orderBy = "code";

    SortType sortOrder = SortType.desc;

    private Boolean active;

    private Boolean parentIsNull;

    @JsonIgnore
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortType.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }
}
