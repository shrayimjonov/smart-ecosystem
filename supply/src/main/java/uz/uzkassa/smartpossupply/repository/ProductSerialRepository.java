package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.05.2023
 */
@Repository
public interface ProductSerialRepository extends BaseRepository<ProductSerial, Long>, ProductSerialRepositoryCustom {

    Optional<ProductSerial> findFirstByProductIdAndSerialNumberAndCompanyIdAndDeletedIsFalse(Long productId, String serialNumber, Long companyId);

    boolean existsByProductIdAndSerialNumberAndCompanyIdAndDeletedIsFalse(Long productId, String serialNumber, Long companyId);

    boolean existsByProductIdAndSerialNumberAndCompanyIdAndDeletedIsFalseAndIdNot(Long productId, String serialNumber, Long companyId, Long id);
}
