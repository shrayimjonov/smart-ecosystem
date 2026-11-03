package uz.uzkassa.cts.dto.payment;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class PaymentActDetailDTO implements Serializable {

    private Integer totalProductCount;
    private BigDecimal totalPriceWithVat;
    private BigDecimal priceFor1pc;
    private BigDecimal priceFor1pcWithVat;
    private BigDecimal vat1pc;
    private APayStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private ContractStatus status = ContractStatus.CREATED;
    @Enumerated(EnumType.STRING)
    private ActDocFlowStatus docFlowStatus;
    private LocalDateTime acceptDate;
    private String docFlowReason;

    private String fileUri;
    private String fileName;

    private String powerAttorneyFileUri;
    private String powerAttorneyFileName;

    private Long deviceId;
    private String deviceSerialNumber;
    private String deviceModelName;

    private Long id;
    private String number;
    private LocalDateTime createdDate;
    private String creatorName;
    private String modifierName;

    public String getNumber() {
        return getId() != null ? PrefixEnum.AP + "/" + getId() : null;
    }
}
