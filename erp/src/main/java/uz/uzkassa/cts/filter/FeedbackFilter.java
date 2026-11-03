package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CustomerBranchStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FeedbackFilter extends BaseFilter {

    Long typeId;
    Integer rate;
    CustomerBranchStatus customerBranchStatus;
}
