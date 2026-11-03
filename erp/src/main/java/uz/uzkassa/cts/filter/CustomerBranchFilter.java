package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchFilter extends BaseFilter {

    Boolean confirmed;

    Boolean hasAct;

    Boolean hasFiscal;

    Boolean hasInvoicePayment;

    String ofdStatus;

    Long parentActivityTypeId;

    Long activityTypeId;

    List<Long> districtIds;
}
