package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.integration.enums.DeliveryType;
import uz.uzkassa.smartpos.integration.enums.OrderStatus;
import uz.uzkassa.smartpos.integration.enums.PaymentType;
import uz.uzkassa.smartpos.integration.enums.SaleType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierOrderDetailDTO {
    Long id;

    CompanyBaseDTO customer;

    CompanyBaseDTO supplier;

    LocalDateTime orderDate;

    OrderStatus status;

    CommonDTO branch;

    CommonDTO warehouse;

    String orderNumber;

    ContractLookUpDTO contract;

    String description;

    List<SupplierOrderProductDetailDTO> products = new ArrayList<>();

    CompanyBaseDTO company;

    CommonDTO deliveryBranch;

    SaleType saleType;

    PaymentType paymentType;

    AddressDetailDTO deliveryAddress;

    LocalDateTime deliveryDate;


    DeliveryType deliveryType;
}
