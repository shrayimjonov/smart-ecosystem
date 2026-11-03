package uz.uzkassa.smartpos.dto.integration;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.AppType;

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
    private AppType appType;
    private List<BillingCategoryTreeDto> children;
    private String parentId;
}
