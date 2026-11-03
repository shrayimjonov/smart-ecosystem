package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.dto.base.ClickhouseCommonDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketPlaceCategoryDetailDTO implements Serializable {
    String id;
    String name;
    ClickhouseCommonDTO parent;
    List<ClickhouseCommonDTO> children = new ArrayList<>();
    MarketplacePhotoDTO photoDTO;
}
