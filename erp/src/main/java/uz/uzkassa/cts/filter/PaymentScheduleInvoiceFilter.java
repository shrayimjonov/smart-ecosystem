package uz.uzkassa.cts.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.XfileStatus;

@Getter
@Setter
public class PaymentScheduleInvoiceFilter extends BaseFilter {
    XfileStatus xfileStatus;
}
