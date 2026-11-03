package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.ActivityType;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.ActivityTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ActivityTypeService extends BaseService {

    ActivityTypeRepository activityTypeRepository;

    public List<CommonDTO> lookUp(BaseFilter filter) {
        if (filter.getParentId() == null) {
            return activityTypeRepository
                .findAllByParentIdIsNullAndDeletedIsFalse()
                .stream()
                .map(ActivityType::toCommonDTO)
                .collect(Collectors.toList());
        }
        return activityTypeRepository
            .findAllByParentIdAndDeletedIsFalse(filter.getParentId())
            .stream()
            .map(ActivityType::toCommonDTO)
            .collect(Collectors.toList());
    }
}
