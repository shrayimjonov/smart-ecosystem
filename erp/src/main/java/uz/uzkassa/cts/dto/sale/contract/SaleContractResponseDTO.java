package uz.uzkassa.cts.dto.sale.contract;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SaleContractResponseDTO implements Serializable {

    private Long id;
    private LocalDateTime createdDate;
    private ContractStatus status;
    private String contractNumber;

    private Long customerId;
    private String customerName;
    private String customerTin;

    private String fileUri;
    private String fileName;
    private String contentType;

    private Long creatorId;
    private String creatorName;

    private Long saleActId;

    private Long saleInvoiceId;
    private LocalDateTime invoiceDate;
    private String invoiceNumber;

    private BigDecimal amount;
    private String accessoryNames;
    private Integer sumQty;

    public String getContractNumber() {
        return getId() != null ? PrefixEnum.CAS + "/" + getId() : null;
    }

    public String getInvoiceNumber() {
        return getSaleInvoiceId() != null ? PrefixEnum.IAS + "/" + getSaleInvoiceId() : null;
    }
}
