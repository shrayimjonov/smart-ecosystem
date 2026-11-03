package uz.uzkassa.smartpossupply.dto.offlineorder;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.MarketplaceAddressDTO;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.DeliveryType;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.OrderType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.SaleType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderUpdateDTO extends GrandTotalDTO {

    Long id;

    Long orderId;

    String mobileId;

    Long deliveryBranchId;

    Long customerId;

    Long supplierId;

    LocalDateTime orderDate;
    String orderNumber;
    String marketCode;

    Long contractId;

    Long branchId;

    Long warehouseId;

    Long vehicleId;

    String description;

    OrderStatus status;

    MarketplaceAddressDTO deliveryAddress;

    SaleType saleType;

    PaymentType paymentType;

    List<OrderProductDTO> products = new ArrayList<>();

    BigDecimal advancePaymentAmount;

    BigDecimal advancePaymentPercent;

    String specificationNumber;

    LocalDate specificationDate;

    Long priceListId;

    boolean from1C;

    boolean fromHippo;

    OrderType orderType;

    AppType appType;

    DeliveryType deliveryType;

    LocalDateTime deliveryDate;

    boolean createStockIn;

    String source;

    Long expeditorId;

    Long agentId;

    Long planRouteCustomerBranchId;

    Long planRouteId;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;
}
