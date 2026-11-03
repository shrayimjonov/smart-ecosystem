package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceCategoryTreeDTO implements Serializable {
    String id;
    String name;
    String parentId;
    Long productCount;
    SingleCatalogPhotoDTO photo;
    Integer position;
    boolean favorite;
    List<String> catalogCodes;
    List<MarketplaceCategoryTreeDTO> children = new ArrayList<>();
}

