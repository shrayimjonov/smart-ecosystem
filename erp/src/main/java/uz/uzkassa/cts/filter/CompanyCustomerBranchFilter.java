package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyCustomerBranchFilter extends BaseFilter {

    String ofdStatus;

    Boolean hasAct;

    Boolean hasInvoicePayment;

    String fromDate;

    String toDate;

    String tin;

}
