package uz.uzkassa.smartpos.integration.supply.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.integration.enums.DeliveryType;
import uz.uzkassa.smartpos.integration.enums.OrderStatus;
import uz.uzkassa.smartpos.integration.enums.PaymentType;
import uz.uzkassa.smartpos.integration.enums.SaleType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreateDTO {

    Long id;
    @NotNull
    Long customerId;
    @NotNull
    Long supplierId;
    @NotNull
    String supplierTin;
    @NotNull
    String supplierName;

    String orderNumber;

    Long contractId;

    Long branchId;
    String branchName;

    Long warehouseId;

    String description;

    OrderStatus status = OrderStatus.NEW;

    Long deliveryBranchId;

    AddressDTO deliveryAddress;

    SaleType saleType;

    PaymentType paymentType;
    @NotEmpty
    List<CreateOrderProductDTO> products = new ArrayList<>();
    @NotNull
    DeliveryType deliveryType;

}
