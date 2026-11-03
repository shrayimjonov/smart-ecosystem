package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.base.LocalizedEntity;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.ActivityTypeRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ActivityTypeService extends BaseService {

    ActivityTypeRepository activityTypeRepository;

    public List<CommonDTO> getList(BaseFilter filter) {
        if (filter.getParentId() == null) {
            return activityTypeRepository.findAllByParentIdIsNullAndDeletedIsFalse().stream().map(LocalizedEntity::toCommonDTO).toList();
        }
        return activityTypeRepository.findAllByParentIdAndDeletedIsFalse(filter.getParentId()).stream().map(LocalizedEntity::toCommonDTO).toList();
    }
}
