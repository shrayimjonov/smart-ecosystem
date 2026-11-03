package uz.uzkassa.smartpos.integration.supply.dto;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.dto.base.ClickhouseCommonDTO;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceReferenceDetailDTO implements Serializable {
    String id;
    String name;
    String code;
    ClickhouseCommonDTO parent;
}
