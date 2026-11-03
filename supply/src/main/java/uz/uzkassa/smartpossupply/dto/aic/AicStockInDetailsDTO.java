package uz.uzkassa.smartpossupply.dto.aic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.product.ProductFullDetailDTO;
import uz.uzkassa.smartpossupply.enums.AicStatus;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicStockInDetailsDTO implements Serializable {

    Long id;

    String aicReportId;

    String aicDocumentId;

    ProductFullDetailDTO product;

    ProductFullDetailDTO childProduct;

    AicStatus status;

    int qty;

    Set<String> aggregations = new LinkedHashSet<>();

    Set<String> blocks = new LinkedHashSet<>();

    Set<String> marks = new LinkedHashSet<>();
}
