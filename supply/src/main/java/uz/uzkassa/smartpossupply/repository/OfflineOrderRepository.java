package uz.uzkassa.smartpossupply.repository;

import feign.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.OfflineOrder;

import java.util.List;


@Repository
public interface OfflineOrderRepository extends BaseRepository<OfflineOrder, Long> {

    @Query(value = """
        SELECT DISTINCT ON (mobile_id) *
        FROM offline_order
        WHERE mobile_id IN (:mobileIds)
        ORDER BY mobile_id, created_date DESC
        """,
            nativeQuery = true)
    List<OfflineOrder> findLastByMobileIds(@Param("mobileIds") List<String> mobileIds);

}
