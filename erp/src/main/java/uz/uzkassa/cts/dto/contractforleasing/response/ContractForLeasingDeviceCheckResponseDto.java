package uz.uzkassa.cts.dto.contractforleasing.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContractForLeasingDeviceCheckResponseDto {
    public Long id;
    public String number;
    public LocalDate createDate;
    public LocalDate endDate;
    public int existDevices;
    public int deviceCanBeAdded;
    public int deviceTotal;
    CommonCodeDTO deviceModel;
    private BigDecimal firstPaymentForLeasing;
}
