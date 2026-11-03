package uz.uzkassa.smartpossupply.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.MarketplaceAddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerWithLimitDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleLookupDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.DeliveryType;
import uz.uzkassa.smartpossupply.enums.OrderRejectReason;
import uz.uzkassa.smartpossupply.enums.OrderSource;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.OrderType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.SaleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailDTO extends GrandTotalDTO {

    Long id;

    CustomerWithLimitDTO customer;

    CompanyBaseDTO supplier;

    LocalDateTime orderDate;

    String marketCode;

    OrderStatus status;

    CommonDTO branch;

    CommonDTO warehouse;

    String orderNumber;

    ContractLookUpDTO contract;

    String description;

    CommonDTO stock;

    CommonDTO stockIn;

    List<OrderProductDetailDTO> products = new ArrayList<>();

    CommonDTO copyOrder;

    CommonDTO deliveryBranch;

    SaleType saleType;

    PaymentType paymentType;

    BigDecimal advancePaymentAmount;

    BigDecimal advancePaymentPercent;

    OrderType orderType;

    CommonDTO priceList;

    boolean from1C;

    boolean fromHippo;

    AppType appType;

    MarketplaceAddressDetailDTO deliveryAddress;

    DeliveryType deliveryType;

    LocalDateTime deliveryDate;

    OrderSource source;

    CommonDTO stockTransfer;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    CommonDTO expeditor;

    LocalDateTime createdDate;

    LocalDateTime updatedDate;

    BigDecimal sku;

    Integer totalQtyInPackage;

    VehicleLookupDTO vehicle;

    LocalDateTime lastModifiedDate;

    OrderRejectReason rejectReason;

    CommonDTO trip;

    @JsonIgnore
    public void addProduct(OrderProductDetailDTO product) {
        products.add(product);
    }
}
