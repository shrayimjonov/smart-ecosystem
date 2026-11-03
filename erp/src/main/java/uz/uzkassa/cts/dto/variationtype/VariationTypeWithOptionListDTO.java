package uz.uzkassa.cts.dto.variationtype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.WidgetType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariationTypeWithOptionListDTO extends CommonCodeDTO implements Serializable {

    WidgetType type;

    List<CommonDTO> values = new ArrayList<>();

    @JsonIgnore
    public void addValue(CommonDTO value) {
        values.add(value);
    }
}
