package uz.uzkassa.cts.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ActDocFlowStatus;

@Getter
@Setter
public class PaymentActFilter extends BaseFilter {

    private ActDocFlowStatus actDocFlowStatus;
}
