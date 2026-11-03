package uz.uzkassa.smartpos.dto.customerreceipts;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CurrentBranchesAndTerminalsDTO {

    private Long branchCount;

    private Long terminalCount;

    public CurrentBranchesAndTerminalsDTO(Long branchCount,
                                          Long terminalCount) {
        this.branchCount = branchCount;
        this.terminalCount = terminalCount;
    }
}
