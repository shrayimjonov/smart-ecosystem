package uz.uzkassa.cts.filter;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OneTimeOrderInvoiceFilter extends BaseFilter {

    ContractStatus contractStatus;
    Long serviceId;
    XfileStatus xfileStatus;
}
