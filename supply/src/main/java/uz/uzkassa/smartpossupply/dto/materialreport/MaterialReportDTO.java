package uz.uzkassa.smartpossupply.dto.materialreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created time: 05.05.2025 11:59
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialReportDTO implements Serializable {

    Long branchId;

    String branchName;

    BigDecimal totalInitialQty;

    BigDecimal totalInitialPrice;

    BigDecimal totalLastQty;

    BigDecimal totalLastPrice;

    BigDecimal totalStockInQty;

    BigDecimal totalStockInPrice;

    BigDecimal totalStockOutQty;

    BigDecimal totalStockOutPrice;

    List<MaterialReportItemDTO> items;
}
