package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyDeviceListFilter extends BaseFilter {

    String deviceStatus;

    List<String> paymentStatuses;

    List<String> paymentTypes;

    List<Long> branchIds;

    List<Long> regionIds;

    LocalDate cbdFrom;

    LocalDate cbdTo;
}
