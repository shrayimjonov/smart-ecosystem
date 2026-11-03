package uz.uzkassa.cts.dto.report;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractReportInvoiceSaleStatus;
import uz.uzkassa.cts.enums.ContractReportInvoiceStatus;
import uz.uzkassa.cts.enums.ContractReportPaymentType;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ContractReportDTO implements Serializable {

    Long id;
    String serialNumber;
    String tin;
    String name;
    String contractNumber;
    BigInteger amount;
    String comment;
    String deviceReplacement;
    String reason;
    LocalDateTime date;
    Long regionId;
    ActDocFlowStatus documentStatus;
    ContractReportPaymentType realisation;
    ContractReportInvoiceStatus invoiceStatus;
    @NotNull
    ContractReportInvoiceSaleStatus saleStatus;
}
