package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.integration.enums.ValueType;
import uz.uzkassa.smartpos.integration.enums.WidgetType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceReferenceListDTO {
    String id;
    String name;
    String code;
    String parentId;
    WidgetType widgetType;
    ValueType valueType;
    Long count;
    List<MarketplaceReferenceItemListDTO> children = new ArrayList<>();

}