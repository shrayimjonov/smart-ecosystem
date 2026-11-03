package uz.uzkassa.cts.dto.integration.billing;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.billing.BillingAppType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class BillingCategoryTreeDto implements Serializable {

    private String id;
    private String code;
    private String nameRu;
    private String nameUzCyrillic;
    private String nameUzLatin;
    private Boolean selectable;
    private BillingAppType appType;
    private List<BillingCategoryTreeDto> children;
    private String parentId;
}
