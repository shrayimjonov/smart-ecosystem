package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyDeliverySettings;
import uz.uzkassa.smartpossupply.enums.DeliveryType;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.09.2023
 */
@Repository
public interface CompanyDeliverySettingsRepository extends BaseRepository<CompanyDeliverySettings, Long> {

    Optional<CompanyDeliverySettings> findFirstByCompanyId(Long companyId);

    Optional<CompanyDeliverySettings> findFirstByCompanyIdAndType(Long companyId, DeliveryType type);
}
