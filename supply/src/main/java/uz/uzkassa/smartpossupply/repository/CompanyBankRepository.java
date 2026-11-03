package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyBank;

import java.util.Optional;

@Repository
public interface CompanyBankRepository extends BaseRepository<CompanyBank, Long> {
    Optional<CompanyBank> findByCompanyIdAndBankId(Long companyId, Long bankId);

    @Modifying
    @Query(value = "update CompanyBank set main=:main where companyId=:companyId and bankId=:bankId and id not in(:id)")
    void update(@Param("main") boolean main, @Param("companyId") Long companyId, @Param("bankId") Long bankId, @Param("id") Long id);

    @Query("SELECT cb FROM CompanyBank cb WHERE cb.id = :id ")
    @EntityGraph(attributePaths = {"company", "bank"})
    Optional<CompanyBank> findByIdToSync(@Param("id") Long id);

    @EntityGraph(attributePaths = {"company", "bank"})
    Optional<CompanyBank> findFirstByCompanyIdAndMainIsTrue(Long companyId);

}
