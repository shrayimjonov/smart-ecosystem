package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.MarketplaceApplication;
import uz.uzkassa.smartpossupply.enums.MarketplaceApplicationStatus;

import java.util.Optional;

@Repository
public interface MarketplaceApplicationRepository extends BaseRepository<MarketplaceApplication, Long>, MarketplaceApplicationRepositoryCustom {

    Optional<MarketplaceApplication> findFirstByProductId(Long productId);

    @EntityGraph(attributePaths = {"unit", "product", "country"})
    @Query("select ma from MarketplaceApplication ma where ma.id=:id")
    Optional<MarketplaceApplication> getDetail(@Param("id") Long id);

    @Modifying
    @Query("update MarketplaceApplication ma set ma.status=:status where ma.id=:id")
    void updateStatus(@Param("id") Long id, @Param("status") MarketplaceApplicationStatus status);

}
