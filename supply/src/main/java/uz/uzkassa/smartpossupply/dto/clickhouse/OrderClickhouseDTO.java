package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderClickhouseDTO extends AuditClickhouseDTO {

    Long id;
    String orderNumber;
    String orderDate;

    Long companyId;
    String companyName;
    String companyTin;

    Long contractId;
    String contractNumber;
    String contractDate;

    Long customerId;
    String customerTin;
    String customerName;

    Long supplierId;
    String supplierTin;
    String supplierName;

    Long branchId;
    String branchName;

    Long warehouseId;
    String warehouseName;

    String status;
    String saleType;

    BigDecimal advancePaymentAmount;
    String paymentType;

    String description;

    Long deliveryBranchId;
    String deliveryBranchName;

    BigDecimal totalQty;
    BigDecimal totalPrice;
    BigDecimal totalVatPrice;

    Long copyOrderId;
    String copyOrderNumber;

    String orderType;

    Long priceListId;
    String priceListNumber;

}
