package uz.uzkassa.cts.dto.actoftransmissioninvoice.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.actoftransmission.ActOfTransmissionShortDTO;
import uz.uzkassa.cts.dto.contract.ContractShortDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.dto.xfile.XfileShortDTO;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created date time: 03.03.2025 11:45
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActOfTransmissionInvoiceListAdminDTO implements Serializable {

    Long id;

    ContractStatus status;

    String invoiceNumber;

    LocalDateTime invoiceDate;

    Integer totalProductCount;

    BigDecimal priceFor1pc;

    BigDecimal totalPrice;

    BigDecimal totalVatPrice;

    BigDecimal totalSellPrice;

    ActOfTransmissionShortDTO act;

    ContractShortDTO contract;

    CustomerShortDTO customer;

    XfileStatus xfileStatus;
}
