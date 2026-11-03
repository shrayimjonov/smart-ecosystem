package uz.uzkassa.cts.dto.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractReportListDTO implements Serializable {

    Long id;
    String serialNumber;
    String tin;
    String name;
    String region;
    String contractNumber;
    BigInteger amount;
    String comment;
    String deviceReplacement;
    String reason;
    LocalDateTime date;
    CheckStatus checkStatus;
    ActDocFlowStatus documentStatus;
    ContractReportPaymentType realisation;
    ContractReportInvoiceStatus invoiceStatus;
    ContractReportInvoiceSaleStatus saleStatus;
    String lastModifier;
    LocalDateTime lastModifiedDate;
}
