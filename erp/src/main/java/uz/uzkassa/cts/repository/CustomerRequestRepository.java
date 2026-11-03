package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.customer.CustomerRequest;
import uz.uzkassa.cts.repository.custom.CustomerRequestRepositoryCustom;

@Repository
public interface CustomerRequestRepository extends BaseRepository<CustomerRequest, Long>, CustomerRequestRepositoryCustom {
}
