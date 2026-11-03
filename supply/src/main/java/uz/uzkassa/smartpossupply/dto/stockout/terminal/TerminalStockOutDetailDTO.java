package uz.uzkassa.smartpossupply.dto.stockout.terminal;

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
import uz.uzkassa.smartpossupply.dto.customer.TerminalCustomerListDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderBaseDTO;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptLinkDTO;
import uz.uzkassa.smartpossupply.dto.stockout.TerminalStockOutProductDetailDTO;
import uz.uzkassa.smartpossupply.enums.DeliveryType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.10.2024.2021 11:35
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalStockOutDetailDTO extends GrandTotalDTO {

    Long id;

    String stockNumber;

    StockStatus status;

    ContractLookUpDTO contract;

    LocalDateTime transferDate;

    CompanyBaseDTO supplier;

    TerminalCustomerListDTO customer;

    CommonDTO warehouse;

    CommonDTO branch;

    FacturaLinkDTO factura;

    Set<FacturaLinkDTO> facturas = new HashSet<>();

    OrderBaseDTO order;

    String description;

    CommonDTO deliveryBranch;

    List<TerminalStockOutProductDetailDTO> products = new ArrayList<>();

    DeliveryType deliveryType;

    PaymentType paymentType;

    AddressDetailDTO deliveryAddress;

    LocalDateTime lastModifiedDate;

    ReceiptLinkDTO receipt;

    boolean hasReceipt;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    @JsonIgnore
    public void addProduct(TerminalStockOutProductDetailDTO product) {
        products.add(product);
    }
}
