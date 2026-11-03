package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.05.2023 16:46
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockClickhouseDTO extends AuditClickhouseDTO {

    Long id;

    String stockNumber;

    Long contractId;
    String contractNumber;
    String contractDate;

    String transferDate;

    Long warehouseId;
    String warehouseName;

    Long customerId;
    String customerTin;
    String customerName;

    Long supplierId;
    String supplierTin;
    String supplierName;

    Long companyId;
    String companyTin;
    String companyName;

    Long branchId;
    String branchName;

    Long facturaId;
    String facturaNicId;
    String facturaDate;
    String facturaNo;

    Long partyId;
    String partyNumber;

    Long aicId;

    Long orderId;
    String orderNumber;
    String orderDate;

    String transferType;
    String status;

    Long oldStockId;
    String oldStockNumber;
    String oldStockTransferDate;

    Long stockInId;
    String stockInNumber;
    String stockInTransferDate;

    Long stockTransferId;
    String stockTransferNumber;
    String stockTransferDate;

    Long priceListId;
    String priceListNumber;

    BigDecimal totalQty;
    BigDecimal totalPrice;
    BigDecimal totalPriceWithoutVat;
    BigDecimal totalVatPrice;
    String description;
}
