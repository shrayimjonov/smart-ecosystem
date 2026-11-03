package uz.uzkassa.cts.dto.variationtype;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.enums.WidgetType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariationTypeLookUpDTO extends CommonCodeDTO implements Serializable {

    WidgetType type;

    public VariationTypeLookUpDTO(Long id, String name, String code, WidgetType type) {
        super(id, name, code);
        this.type = type;
    }
}
