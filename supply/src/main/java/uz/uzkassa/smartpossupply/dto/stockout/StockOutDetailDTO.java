package uz.uzkassa.smartpossupply.dto.stockout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerWithLimitDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderBaseDTO;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptLinkDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleLookupDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.DeliveryType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockOutDetailDTO extends GrandTotalDTO {

    Long id;

    String stockNumber;

    StockStatus status;

    CommonDTO expeditor;

    CommonDTO planRoute;

    CommonDTO stockIn;

    ContractLookUpDTO contract;

    LocalDateTime transferDate;

    CompanyBaseDTO supplier;

    CustomerWithLimitDTO customer;

    CommonDTO warehouse;

    CommonDTO branch;

    VehicleLookupDTO vehicle;

    FacturaLinkDTO factura;

    Set<FacturaLinkDTO> facturas = new HashSet<>();

    OrderBaseDTO order;

    CommonDTO priceList;

    String description;

    CommonDTO oldStock;

    CommonDTO deliveryBranch;

    List<StockOutProductDetailDTO> products = new ArrayList<>();

    AppType appType;

    DeliveryType deliveryType;

    PaymentType paymentType;

    AddressDetailDTO deliveryAddress;

    boolean allowWithLimit;

    LocalDateTime lastModifiedDate;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    List<String> receiptUIDs = new ArrayList<>();

    List<ReceiptLinkDTO> receipts = new ArrayList<>();

    boolean hasReceipt;

    CommonDTO cashier;

    @JsonIgnore
    public void addProduct(StockOutProductDetailDTO product) {
        products.add(product);
    }
}
