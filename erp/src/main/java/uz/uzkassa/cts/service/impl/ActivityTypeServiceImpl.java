package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.ActivityType;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.filter.ActivityTypeFilter;
import uz.uzkassa.cts.repository.ActivityTypeRepository;
import uz.uzkassa.cts.service.ActivityTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class ActivityTypeServiceImpl extends BaseServiceImpl<ActivityType, Long> implements ActivityTypeService {

    private final ActivityTypeRepository activityTypeRepository;

    public ActivityTypeServiceImpl(ActivityTypeRepository activityTypeRepository) {
        super(activityTypeRepository);
        this.activityTypeRepository = activityTypeRepository;
    }

    @Override
    public List<CommonDTO> lookUp(ActivityTypeFilter filter) {
        if (filter.getOnlyParents() != null && filter.getOnlyParents()) {
            return activityTypeRepository.findAllByParentIdNull().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
        } else if (filter.getParentId() != null) {
            return activityTypeRepository.findAllByParentId(filter.getParentId()).stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
        } else {
            throw validationService.badRequestExceptionThrow(ACTIVITY_TYPE_PARENT_VALIDATION).get();
        }
    }
}
