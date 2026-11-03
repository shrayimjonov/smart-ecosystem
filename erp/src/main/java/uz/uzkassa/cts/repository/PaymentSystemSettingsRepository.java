package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.PaymentSystemSettings;

@Repository
public interface PaymentSystemSettingsRepository extends BaseRepository<PaymentSystemSettings, Long>{
    PaymentSystemSettings findFirstByOrderById();
}
