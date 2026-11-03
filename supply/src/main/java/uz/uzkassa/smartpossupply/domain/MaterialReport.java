package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "material_report")
public class MaterialReport extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 5674333234L;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @Column(name = "product_name", length = 2000)
    String productName;

    @Column(name = "product_barcode")
    String productBarcode;

    @Column(name = "product_serial_number")
    String productSerialNumber;

    @Column(name = "branch_id")
    Long branchId;

    @Column(name = "branch_name")
    String branchName;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @Column(name = "warehouse_name")
    String warehouseName;

    @Column(name = "qty")
    BigDecimal qty;

    @Column(name = "price")
    BigDecimal price;

    @Column(name = "stock_in_qty")
    BigDecimal stockInQty;

    @Column(name = "stock_in_price")
    BigDecimal stockInPrice;

    @Column(name = "stock_out_qty")
    BigDecimal stockOutQty;

    @Column(name = "stock_out_price")
    BigDecimal stockOutPrice;

    @Column(name = "created_date")
    LocalDate createdDate;

    @Column(name = "stock_in_pallet_package_qty")
    Integer stockInPalletPackageQty;

    @Column(name = "stock_out_pallet_package_qty")
    Integer stockOutPalletPackageQty;

    @Column(name = "stock_in_block_package_qty")
    Integer stockInBlockPackageQty;

    @Column(name = "stock_out_block_package_qty")
    Integer stockOutBlockPackageQty;

    @Column(name = "stock_in_box_package_qty")
    Integer stockInBoxPackageQty;

    @Column(name = "stock_out_box_package_qty")
    Integer stockOutBoxPackageQty;

    @Column(name = "clickhouse_sync", columnDefinition = "boolean default false")
    boolean clickhouseSync = false;
}
