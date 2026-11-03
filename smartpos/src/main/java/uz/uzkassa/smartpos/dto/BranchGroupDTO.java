package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.BranchGroup;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BranchGroupDTO implements Serializable {
    protected Long id;
    @NotNull
    @Size(min = 1, max = 50)
    private String name;
    private List<SelectItem> branches;

    public BranchGroupDTO(BranchGroup branchGroup) {
        this.id = branchGroup.getId();
        this.name = branchGroup.getName();
        if (CollectionUtils.isNotEmpty(branchGroup.getBranches())) {
            this.branches = branchGroup.getBranches()
                .stream()
                .map(Branch::toSelectItem)
                .toList();
        }
    }
}
