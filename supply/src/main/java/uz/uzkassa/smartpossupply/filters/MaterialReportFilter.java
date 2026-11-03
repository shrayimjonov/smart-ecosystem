package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Created time: 08.05.2025 15:50
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialReportFilter extends BaseFilter {

    boolean allBranches;

    List<Long> branchIds;

    List<Long> warehouseIds;

    @JsonIgnore
    public String getSearchValueClickhouse() {
        return " ilike '%".concat(this.search.toLowerCase().replaceAll("'", "\\\\'")).concat("%'");
    }
}
