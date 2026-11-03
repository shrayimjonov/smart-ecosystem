package uz.uzkassa.smartpossupply.dto.order.mobile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.OrderType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.SaleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 12:15
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileOrderDTO extends GrandTotalDTO {

    Long id;

    Long branchId;

    Long customerId;

    LocalDateTime orderDate;

    Long contractId;

    OrderStatus status;

    Long deliveryBranchId;

    Long warehouseId;

    SaleType saleType;

    PaymentType paymentType;

    BigDecimal advancePaymentAmount;

    BigDecimal advancePaymentPercent;

    List<MobileOrderProductDTO> products = new ArrayList<>();

    OrderType orderType;

    Long planRouteCustomerBranchId;

    String description;

    Long priceListId;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    LocalDateTime deliveryDate;

    Double longitude;

    Double latitude;
    
    @JsonIgnore
    public void addProduct(MobileOrderProductDTO product) {
        products.add(product);
    }
}
