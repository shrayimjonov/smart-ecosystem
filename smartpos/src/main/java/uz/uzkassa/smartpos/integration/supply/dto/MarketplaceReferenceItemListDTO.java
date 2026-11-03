package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceReferenceItemListDTO {
    String id;
    String name;
    String code;
    String parentId;
    Long count;
}
