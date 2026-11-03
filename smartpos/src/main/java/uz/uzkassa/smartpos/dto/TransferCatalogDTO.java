package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class TransferCatalogDTO implements Serializable {
    @NotNull
    private Long fromBranchId;
    @NotNull
    @NotEmpty
    private Set<Long> toBranchIds;

    public TransferCatalogDTO(Long fromBranchId, Long toBranchId) {
        this.fromBranchId = fromBranchId;
        this.toBranchIds = Set.of(toBranchId);
    }
}
