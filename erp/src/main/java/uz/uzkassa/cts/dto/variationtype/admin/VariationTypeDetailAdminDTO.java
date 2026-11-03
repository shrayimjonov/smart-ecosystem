package uz.uzkassa.cts.dto.variationtype.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.LocalizedDTO;
import uz.uzkassa.cts.enums.WidgetType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariationTypeDetailAdminDTO extends LocalizedDTO implements Serializable {

    WidgetType widgetType;
}
