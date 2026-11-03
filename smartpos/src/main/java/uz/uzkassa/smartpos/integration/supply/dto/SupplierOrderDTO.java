package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.integration.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierOrderDTO {
    Long id;

    Long customerId;
    String customerTin;

    String supplierTin;

    LocalDateTime orderDate;
    String orderNumber;

    Long contractId;

    Long branchId;

    Long warehouseId;

    String description;

    OrderStatus status;

    Long deliveryBranchId;

    AddressDTO deliveryAddress;

    SaleType saleType;

    PaymentType paymentType;

    List<SupplierOrderProductDTO> products = new ArrayList<>();

    AppType appType = AppType.SMARTPOS;

    DeliveryType deliveryType;

    BigDecimal totalQty;

    BigDecimal totalPrice;

    BigDecimal totalPriceWithoutVat;

    BigDecimal totalVatPrice;

    public SupplierOrderDTO(OrderCreateDTO dto, Company customer) {
        this.id = dto.getId();
        this.customerTin = customer.getInn();
        this.supplierTin = dto.getSupplierTin();
        this.orderDate = LocalDateTime.now();
        this.orderNumber = dto.getOrderNumber();
        this.contractId = dto.getContractId();
        this.branchId = dto.getBranchId();
        this.warehouseId = dto.getWarehouseId();
        this.description = dto.getDescription();
        this.status = Optional.ofNullable(dto.getStatus()).orElse(OrderStatus.NEW);
        this.deliveryBranchId = dto.getDeliveryBranchId();
        this.deliveryAddress = dto.getDeliveryAddress();
        this.saleType = dto.getSaleType();
        this.paymentType = dto.getPaymentType();
        this.products = dto.getProducts().stream().map(SupplierOrderProductDTO::new).collect(Collectors.toList());
        this.appType = AppType.SMARTPOS;
        this.deliveryType = dto.getDeliveryType();
        this.totalPrice = products.stream().map(SupplierOrderProductDTO::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalPriceWithoutVat = products.stream()
            .map(SupplierOrderProductDTO::getTotalPriceWithoutVat).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalVatPrice = products.stream()
                .map(productDto -> Optional.ofNullable(productDto.getTotalVatPrice()).orElse(BigDecimal.ZERO))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
