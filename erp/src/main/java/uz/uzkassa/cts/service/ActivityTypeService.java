package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.ActivityType;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.filter.ActivityTypeFilter;

import java.util.List;

public interface ActivityTypeService extends BaseService<ActivityType, Long> {

    List<CommonDTO> lookUp(ActivityTypeFilter filter);
}
