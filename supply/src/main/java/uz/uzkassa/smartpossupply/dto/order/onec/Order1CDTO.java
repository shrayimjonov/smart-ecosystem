package uz.uzkassa.smartpossupply.dto.order.onec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class Order1CDTO implements Serializable {

    String orderNumber;

    LocalDateTime orderDate;

    String customerCode;

    String deliveryBranchCode;

    String description;

    Long branchId;

    Long warehouseId;

    String customerTin;

    OrderContract1CDTO contract;

    BigDecimal advancePaymentPercent;

    String specificationNumber;

    LocalDate specificationDate;

    String source;

    PaymentType paymentType;

    List<OrderProduct1CDTO> products = new ArrayList<>();

    @JsonProperty("createStockIn")
    boolean createStockIn = true;

    public void addProduct(OrderProduct1CDTO orderProduct1CDTO) {
        products.add(orderProduct1CDTO);
    }
}
