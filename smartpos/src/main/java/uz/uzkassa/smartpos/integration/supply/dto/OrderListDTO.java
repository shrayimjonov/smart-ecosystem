package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Order;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.integration.enums.DeliveryType;
import uz.uzkassa.smartpos.integration.enums.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderListDTO implements Serializable {

    Long id;

    CompanyBaseDTO customer;
    CompanyBaseDTO supplier;

    LocalDateTime orderDate;

    OrderStatus status;

    String orderNumber;

    DeliveryType deliveryType;

    BigDecimal totalPrice;

    public OrderListDTO(Order order) {
        this.id = order.getId();
        this.customer = order.getCustomer().toCompanyBaseDTO();
        this.supplier = new CompanyBaseDTO(order.getSupplierId(), order.getSupplierName(), order.getSupplierTin());
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.orderNumber = order.getOrderNumber();
        this.deliveryType = order.getDeliveryType();
        this.totalPrice = order.getTotalPrice();
    }
}
