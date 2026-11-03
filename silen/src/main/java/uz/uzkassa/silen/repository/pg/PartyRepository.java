package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Party;
import uz.uzkassa.silen.enumeration.AggregationStatus;

import java.util.List;

/**
 * Spring Data repository for the OrderProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartyRepository extends JpaRepository<Party, String>, PartyRepositoryCustom {
    boolean existsByOrderProductIdAndJobStatus(String orderProductId, AggregationStatus jobStatus);

    @Query("select id from Party where orderProductId=:orderProductId")
    List<String> getPartyIdsByOrderProductId(@Param("orderProductId") String orderProductId);
}
