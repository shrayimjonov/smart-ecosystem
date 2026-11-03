package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.ActivityType;

import java.util.List;
import java.util.Optional;

public interface ActivityTypeRepository extends BaseRepository<ActivityType, Long> {

    List<ActivityType> findAllByParentIdAndDeletedIsFalse(Long parentId);

    List<ActivityType> findAllByParentIdIsNullAndDeletedIsFalse();

    Optional<ActivityType> findFirstByCodeAndParent_Code(Long code, Long parentCode);
}
