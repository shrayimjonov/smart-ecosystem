package uz.uzkassa.cts.dto.onetimeorderinvoice.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.contract.ContractShortDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.dto.xfile.XfileShortDTO;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created date time: 03.03.2025 11:36
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OneTimeOrderInvoiceListAdminDTO implements Serializable {

    Long id;

    ContractStatus status;

    String invoiceNumber;

    LocalDateTime invoiceDate;

    Integer totalProductCount;

    BigDecimal totalPrice;

    BigDecimal totalVatPrice;

    BigDecimal totalSellPrice;

    ContractShortDTO contract;

    CustomerShortDTO customer;

    XfileShortDTO xfile;
}
