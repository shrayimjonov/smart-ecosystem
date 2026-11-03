package uz.uzkassa.smartpossupply.dto.order.mobile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.enums.OrderRejectReason;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.SaleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 11:42
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileOrderDetailDTO extends GrandTotalDTO {

    Long id;

    String orderNumber;

    LocalDateTime orderDate;

    CompanyBaseDTO customer;

    OrderStatus status;

    CommonDTO branch;

    CommonDTO warehouse;

    ContractLookUpDTO contract;

    String description;

    CommonCodeDTO priceList;

    CommonDTO deliveryBranch;

    SaleType saleType;

    PaymentType paymentType;

    BigDecimal advancePaymentAmount;

    BigDecimal advancePaymentPercent;

    AddressDetailDTO deliveryAddress;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    CommonDTO expeditor;

    boolean withPlanRoute;

    LocalDateTime deliveryDate;

    LocalDateTime lastModifiedDate;

    List<MobileOrderProductDetailDTO> products = new ArrayList<>();

    OrderRejectReason rejectReason;

    @JsonIgnore
    public void addProduct(MobileOrderProductDetailDTO product) {
        products.add(product);
    }
}
