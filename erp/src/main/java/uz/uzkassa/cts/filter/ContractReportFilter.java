package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.*;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ContractReportFilter extends BaseFilter {

    CheckStatus checkStatus;
    ActDocFlowStatus documentStatus;
    ContractReportPaymentType realisation;
    ContractReportInvoiceStatus invoiceStatus;
    ContractReportInvoiceSaleStatus saleStatus;
    boolean replacement;
    Long lastModifierId;
    LocalDate fromLastModifier;
    LocalDate toLastModifier;
}
