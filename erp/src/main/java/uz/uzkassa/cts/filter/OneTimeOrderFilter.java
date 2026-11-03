package uz.uzkassa.cts.filter;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ContractStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OneTimeOrderFilter extends BaseFilter {
    List<ContractStatus> statuses;
    LocalDate modifiedFrom;
    LocalDate modifiedTo;
    List<Long> serviceIds;
}
