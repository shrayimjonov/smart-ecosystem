package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.enums.HallItemStatus;

import java.util.Collection;
import java.util.List;

public interface HallItemRepository extends BaseRepository<HallItem, Long>, HallItemRepositoryCustom {
    List<HallItem> findAllByHallIdAndDeletedIsFalseOrderByIdAsc(Long hallId);

    List<HallItem> findAllByHallIdAndDeletedIsFalseAndStatusOrderByIdAsc(Long hallId, HallItemStatus status);

    boolean existsByHallIdAndNameAndDeletedIsFalse(Long hallId, String name);

    boolean existsByHallIdAndNameAndDeletedIsFalseAndIdNot(Long hallId, String name,Long id);

    List<HallItem> findAllByIdIn(Collection<Long> hallIds);
}
