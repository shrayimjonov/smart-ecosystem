package uz.uzkassa.cts.dto.paymentCompanyContract;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentCompanyContractDetailDTO {

    private Long id;
    private LocalDateTime createdDate;

    private ContractStatus status;

    private Long companyId;
    private String companyTin;
    private String companyName;

    private BigDecimal totalPriceWithVat;
    private BigDecimal totalVatPrice;
    private BigDecimal totalPrice;
    private BigDecimal priceFor1pc;
    private BigDecimal priceFor1pcWithVat;
    private String fileUri;
    private String fileName;

    private Long creatorId;
    private String creatorName;
    private String contractNumber;

    private Long deviceModelId;
    private String deviceModelName;

    public String getContractNumber() {
        return getId() != null ? PrefixEnum.CCP + "/" + getId() : null;
    }
}
