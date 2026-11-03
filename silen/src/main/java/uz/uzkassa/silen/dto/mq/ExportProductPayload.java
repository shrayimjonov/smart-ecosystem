package uz.uzkassa.silen.dto.mq;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.filter.ProductFilter;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportProductPayload {
    ProductFilter productFilter;
    String userId;

}
