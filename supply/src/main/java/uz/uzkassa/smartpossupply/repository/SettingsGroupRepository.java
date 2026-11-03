package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.settings.SettingsGroup;

import java.util.List;

@Repository
public interface SettingsGroupRepository extends BaseRepository<SettingsGroup, Long> {

    List<SettingsGroup> findAllByOrderByOrderNumberAscIdAsc();

    @Modifying
    @Query("update SettingsGroup s set s.orderNumber = s.orderNumber + 1 where s.orderNumber >= :orderNumber")
    void shiftOrderNumbersUp(@Param("orderNumber") Integer orderNumber);

    @Modifying
    @Query("update SettingsGroup s set s.orderNumber = s.orderNumber - 1 " +
            "where s.id != :excludeId and s.orderNumber > :oldOrderNumber and s.orderNumber <= :newOrderNumber")
    void shiftOrderNumbersDownInRange(@Param("excludeId") Long excludeId,
                                      @Param("oldOrderNumber") Integer oldOrderNumber,
                                      @Param("newOrderNumber") Integer newOrderNumber);

    @Modifying
    @Query("update SettingsGroup s set s.orderNumber = s.orderNumber + 1 " +
            "where s.id != :excludeId and s.orderNumber >= :newOrderNumber and s.orderNumber < :oldOrderNumber")
    void shiftOrderNumbersUpInRange(@Param("excludeId") Long excludeId,
                                    @Param("newOrderNumber") Integer newOrderNumber,
                                    @Param("oldOrderNumber") Integer oldOrderNumber);

}
