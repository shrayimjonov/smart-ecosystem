package uz.uzkassa.smartpossupply.dto.stockout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActLinkDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptLinkDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockOutListDTO {

    Long id;

    String stockNumber;

    LocalDateTime transferDate;

    CommonDTO expeditor;

    CommonDTO planRoute;

    CompanyBaseDTO customer;

    CommonDTO branch;

    CommonDTO vehicle;

    CommonDTO warehouse;

    CommonDTO order;

    LocalDateTime orderDate;

    Set<FacturaLinkDTO> facturas;

    CommonDTO oldStock;

    BigDecimal totalPrice;

    BigDecimal totalForeignPrice;
    BigDecimal exchangeRate;
    CurrencyType currencyType;

    BigDecimal totalDiscountPrice;

    BigDecimal totalQty;

    StockStatus status;

    LocalDateTime lastModifiedDate;

    AcceptanceTransferActLinkDTO acceptanceTransferAct;

    String receipt;

    List<ReceiptLinkDTO> receipts = new ArrayList<>();

    PaymentType paymentType;
}
