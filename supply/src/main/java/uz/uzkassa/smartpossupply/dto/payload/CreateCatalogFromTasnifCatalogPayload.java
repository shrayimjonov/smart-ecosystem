package uz.uzkassa.smartpossupply.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCatalogFromTasnifCatalogPayload implements Serializable {

    Long companyId;

    String tin;

    Long branchId;

    Long creatorId;

    Long unitId;

    String productGroup;

    public CreateCatalogFromTasnifCatalogPayload(Long companyId, String tin, Long branchId, Long creatorId, Long unitId, String productGroup) {
        this.companyId = companyId;
        this.tin = tin;
        this.branchId = branchId;
        this.creatorId = creatorId;
        this.unitId = unitId;
        this.productGroup = productGroup;
    }
}
