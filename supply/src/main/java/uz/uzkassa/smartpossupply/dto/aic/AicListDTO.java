package uz.uzkassa.smartpossupply.dto.aic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.AicStatus;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicListDTO {

    Long id;

    String aic;

    ProductCommonDTO product;

    ProductType productType;

    int  qty;

    LocalDateTime createdAt;

    AicStatus status;

    CommonDTO creator;
}
