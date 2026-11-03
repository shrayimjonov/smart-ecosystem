package uz.uzkassa.smartpos.integration.supply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.integration.apay.AddressFullDTO;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SupplierBranchWithAddressDTO {
    private Long id;
    private String name;
    private AddressFullDTO address;

    public SupplierBranchWithAddressDTO(Branch branch) {
        this.id = branch.getId();
        this.name = branch.getName();
        this.address = branch.getAddressDTO();
    }
}
