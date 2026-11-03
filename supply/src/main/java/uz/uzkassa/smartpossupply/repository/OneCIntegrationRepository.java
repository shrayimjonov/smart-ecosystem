package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.OneCIntegration;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 25.01.2024
 */
@Repository
public interface OneCIntegrationRepository extends BaseRepository<OneCIntegration, Long> {

    //    @Cacheable(cacheNames = ONC_INTEGRATION_API_CACHE)
    Optional<OneCIntegration> findByCompanyId(Long companyId);
}
