package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDateDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.OrderSource;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.OrderType;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderListDTO implements Serializable {

    Long id;

    CompanyBaseDTO customer;

    CommonCodeDTO customerBranch;

    LocalDateTime orderDate;

    OrderStatus status;

    String orderNumber;

    OrderType orderType;

    CommonDTO creator;

    CommonDTO branch;

    CommonDTO warehouse;

    CommonDTO vehicle;

    CommonDateDTO stock;

    Set<CommonDateDTO> stocks;

    ContractLookUpDTO contract;

    BigDecimal totalPrice;

    BigDecimal totalForeignPrice;
    BigDecimal exchangeRate;
    CurrencyType currencyType;

    BigDecimal totalDiscountPrice;

    BigDecimal totalQty;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    AddressDetailDTO deliveryAddress;

    CommonDTO deliveryBranch;

    LocalDateTime deliveryDate;

    OrderSource source;

    CommonDTO stockTransfer;

    CommonDTO planRoute;

    LocalDateTime createdDate;

    LocalDateTime updatedDate;

    BigDecimal sku;

    Integer totalQtyInPackage;

    CommonDTO expeditor;

    LocalDateTime lastModifiedDate;

    boolean fromHippo;

    PaymentType paymentType;

    CommonDTO trip;
}
