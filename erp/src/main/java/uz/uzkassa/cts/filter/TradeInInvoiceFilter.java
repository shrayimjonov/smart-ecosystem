package uz.uzkassa.cts.filter;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TradeInInvoiceFilter extends BaseFilter {

    XfileStatus xfileStatus;

    public TradeInInvoiceFilter(LocalDate from, LocalDate to, ContractStatus status) {
        this.from = from;
        this.to = to;
        this.status = status.getCode();
    }
}
