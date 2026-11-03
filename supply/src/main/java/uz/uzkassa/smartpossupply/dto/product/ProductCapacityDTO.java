package uz.uzkassa.smartpossupply.dto.product;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCapacityDTO extends CommonDTO {

    String capacity;

    public ProductCapacityDTO(Long id, String name, String capacity) {
        super(id, name);
        this.capacity = capacity;
    }
}
