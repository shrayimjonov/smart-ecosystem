package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.party.Party;

@Repository
public interface PartyRepository extends BaseRepository<Party, Long>, PartyRepositoryCustom {
    @Query("SELECT coalesce(max(ag.incrementNumber), 0) FROM Party ag where ag.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
