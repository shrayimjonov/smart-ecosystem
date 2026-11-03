package uz.uzkassa.smartpos.integration.supply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.integration.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO {

    Long id;

    Long customerId;
    String customerTin;

    Long supplierId;
    String supplierTin;
    String supplierName;

    LocalDateTime orderDate;
    String orderNumber;

    Long contractId;

    Long branchId;
    String branchName;

    Long warehouseId;

    String description;

    OrderStatus status;

    Long deliveryBranchId;

    AddressDTO deliveryAddress;

    SaleType saleType;

    PaymentType paymentType;

    List<OrderProductDTO> products = new ArrayList<>();

    AppType appType;

    DeliveryType deliveryType;

    LocalDateTime deliveryDate;


    @JsonProperty("totalPrice")
    public BigDecimal getTotalPrice() {
        return products.stream().map(OrderProductDTO::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
