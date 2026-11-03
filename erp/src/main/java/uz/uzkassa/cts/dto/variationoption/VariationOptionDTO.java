package uz.uzkassa.cts.dto.variationoption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.LocalizedDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariationOptionDTO extends LocalizedDTO implements Serializable {

    Long variationTypeId;
}
