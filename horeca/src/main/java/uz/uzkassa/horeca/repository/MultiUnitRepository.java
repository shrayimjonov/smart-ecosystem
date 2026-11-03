package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.unit.MultiUnit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface MultiUnitRepository extends BaseRepository<MultiUnit, Long> {

    @Modifying
    void deleteAllByMenuItemId(Long menuItemId);

    List<MultiUnit> findAllByMenuItemIdAndDeletedIsFalse(Long menuItemId);

    @Modifying
    @Query("update MultiUnit  set deleted=true  where id in (:ids)")
    void deleteAllByIdIn(@Param("ids") Set<Long> ids);
}
