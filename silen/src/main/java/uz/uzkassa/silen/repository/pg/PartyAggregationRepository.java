package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.uzkassa.silen.domain.PartyAggregation;

public interface PartyAggregationRepository extends JpaRepository<PartyAggregation, String>, PartyAggregationRepositoryCustom {

    @Query("select max(intNumber) from PartyAggregation where organizationId=?1")
    Integer getMaxIntNumber(String organizationId);

    boolean existsPartyAggregationByOrganizationIdAndNumberIgnoreCase(String organizationId, String number);
}
