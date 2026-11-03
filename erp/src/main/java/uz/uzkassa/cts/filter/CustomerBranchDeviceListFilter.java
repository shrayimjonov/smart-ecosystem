package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceSuspendStatus;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchDeviceListFilter extends BaseFilter {
    LocalDate startDateBegin;
    LocalDate startDateEnd;
    LocalDate dueDateBegin;
    LocalDate duetDateEnd;
    Long tariffId;
    DeviceSuspendStatus suspendStatus;
}
