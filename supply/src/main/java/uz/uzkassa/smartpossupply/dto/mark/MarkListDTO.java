package uz.uzkassa.smartpossupply.dto.mark;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.MarkStatus;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkListDTO implements Serializable {

    Long id;

    String code;

    String printCode;

    ProductCommonDTO product;

    MarkStatus turonStatus;

    AggregationStatus aggregationStatus;

    boolean used;
}
