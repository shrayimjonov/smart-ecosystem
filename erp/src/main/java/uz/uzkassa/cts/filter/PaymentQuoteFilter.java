package uz.uzkassa.cts.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaymentQuoteFilter extends BaseFilter {

    private List<Long> creatorIds;
    private String paymentStatus;
    private String paymentType;
}
