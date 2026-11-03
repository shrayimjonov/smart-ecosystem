package uz.uzkassa.smartpossupply.dto.markorder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.BufferStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkOrderProductListDTO implements Serializable {

    Long id;

    ProductCommonDTO product;

    CommonDTO order;

    Integer received;

    Integer remaining;

    Integer qty;

    BufferStatus status;

    LocalDateTime orderDate;
}
