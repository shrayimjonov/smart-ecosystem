package uz.uzkassa.smartpos.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BranchFilter extends PrimaryFilter {
    private Long companyId;
    private List<Long> branchIds;
    private List<Long> branchGroupIds;
    private LocalDateTime from;
    private LocalDateTime to;
    private String searchKey;
    @Schema(hidden = true)
    private boolean forAdmin = false;

    @Override
    public String getSearch() {
        return searchKey;
    }

    @Override
    public String getSearchForQuery() {
        return "%" + searchKey.toLowerCase() + "%";
    }
}
