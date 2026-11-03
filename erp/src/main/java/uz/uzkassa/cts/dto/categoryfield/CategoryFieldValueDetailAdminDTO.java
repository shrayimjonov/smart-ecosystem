package uz.uzkassa.cts.dto.categoryfield;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.variationtype.VariationTypeWithOptionListDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryFieldValueDetailAdminDTO implements Serializable {

    CommonDTO category;

    List<VariationTypeWithOptionListDTO> fields = new ArrayList<>();

    @JsonIgnore
    public void addField(VariationTypeWithOptionListDTO fieldDTO) {
        fields.add(fieldDTO);
    }
}
