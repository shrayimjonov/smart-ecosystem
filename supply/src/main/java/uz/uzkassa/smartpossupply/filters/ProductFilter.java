package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ProductFilter extends BaseFilter {

    @JsonProperty("favourite")
    Boolean favourite;

    Boolean validateProductType;

    String productType;

    String environmentType;

    String sortOrderType;

    Long companyCommittentId;

    @JsonProperty("hasMark")
    Boolean hasMark;

    String vat;

    Long categoryId;

    boolean hideZero;

    Boolean hasCategory;

    String reportType;

    Long entityId;

    LocalDateTime lastSyncDate;

    Set<Long> branchIds;
}
