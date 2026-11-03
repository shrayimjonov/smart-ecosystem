package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.BankTariff;
import uz.uzkassa.cts.enums.CustomerAppType;

import java.util.Optional;

@Repository
public interface BankTariffRepository extends BaseRepository<BankTariff, Long> {

    Optional<BankTariff> findByXizmatId(Long xizmatId);

    Optional<BankTariff> findByXizmatIdAndPermissionIsTrue(Long xizmatId);
}
