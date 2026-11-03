package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityTypeReportFilter extends BaseReportFilter {

    Long activityTypeId;

    Long parentActivityTypeId;

}
