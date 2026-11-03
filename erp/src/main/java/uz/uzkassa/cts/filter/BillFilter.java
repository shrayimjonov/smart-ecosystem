package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.apay.APayStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BillFilter extends BaseFilter {
    String billId;
    APayStatus aPayStatus;
    DeviceType deviceType;
}
