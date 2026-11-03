package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created time: 07.05.2025 14:44
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class MaterialReportClickhouseDTO implements Serializable {

    Long productId;
    String productName;
    String productBarcode;
    String productSerialNumber;

    Long branchId;
    String branchName;
    Long warehouseId;
    String warehouseName;

    BigDecimal qty;
    BigDecimal price;
    BigDecimal stockInQty;
    BigDecimal stockInPrice;
    BigDecimal stockOutQty;
    BigDecimal stockOutPrice;

    Integer stockInPalletPackageQty;
    Integer stockOutPalletPackageQty;

    Integer stockInBlockPackageQty;
    Integer stockOutBlockPackageQty;

    Integer stockInBoxPackageQty;
    Integer stockOutBoxPackageQty;

    LocalDate date;
}
