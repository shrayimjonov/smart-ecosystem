package uz.uzkassa.cts.dto.servicepaymenthistory.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ServicePaymentHistoryListDto implements Serializable {
    private BigDecimal totalAmount;
    private List<ServicePaymentHistoryResponseDto> content = new ArrayList<>();
}
