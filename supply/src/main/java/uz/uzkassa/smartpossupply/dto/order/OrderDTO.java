package uz.uzkassa.smartpossupply.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.MarketplaceAddressDTO;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO extends GrandTotalDTO {

    Long id;

    Long offlineOrderId;

    Long customerId;
    String customerTin;

    Long supplierId;
    String supplierTin;

    LocalDateTime orderDate;
    String orderNumber;
    String marketCode;

    Long contractId;

    @NotNull
    Long branchId;

    Long warehouseId;

    Long vehicleId;

    String description;

    OrderStatus status;

    Long deliveryBranchId;

    MarketplaceAddressDTO deliveryAddress;

    SaleType saleType;

    PaymentType paymentType;

    @NotEmpty
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

    Boolean fromMobile;

    @JsonIgnore
    public void addProduct(OrderProductDTO product) {
        products.add(product);
    }
}
