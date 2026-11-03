package uz.uzkassa.cts.dto.rentDeviceAct;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RentDeviceActOfReturnDTO implements Serializable {
    private Long id;
    private String number;
    private Long customerId;
    private String customerName;
    private String customerTin;
    private Integer totalProductCount;
    private BigDecimal totalPriceWithVat;
    private BigDecimal priceFor1pc;
    private BigDecimal priceFor1pcWithVat;
    private BigDecimal vat1pc;
    private ContractStatus status;
    private ActDocFlowStatus docFlowStatus;
    private String docFlowReason;
    private LocalDateTime createdDate;
    private LocalDateTime acceptDate;
    private String fileUri;
    private String fileName;

    private String serialNumber;
    private String modelName;

    public String getNumber() {
        return PrefixEnum.ADRR + "/" + this.getId();
    }
}
