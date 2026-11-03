package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CtsService;
import uz.uzkassa.cts.enums.ServiceRegistrationType;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.repository.custom.CtsServiceRepositoryCustom;

import java.util.List;

@Repository
public interface CtsServiceRepository extends BaseRepository<CtsService, Long>, CtsServiceRepositoryCustom {
    List<CtsService> findAllByIdIn(List<Long> ids);

    List<CtsService> findByRegistrationTypeAndDeletedOrderByIdAsc(ServiceRegistrationType type, Boolean deleted);

    List<CtsService> findByRegistrationTypeAndServiceTypeAndDeletedOrderByIdAsc(ServiceRegistrationType registrationType, ServiceType serviceType, Boolean deleted);

    List<CtsService> findAllByBillingIdIsNotNullAndDeletedIsFalse();
}
