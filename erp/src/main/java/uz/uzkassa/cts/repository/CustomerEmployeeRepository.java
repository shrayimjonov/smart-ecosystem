package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerEmployee;
import uz.uzkassa.cts.enums.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerEmployeeRepository extends BaseRepository<CustomerEmployee, Long> {

    List<CustomerEmployee> findAllByCustomerIdAndDeletedIsFalse(Long customerId);

    Optional<CustomerEmployee> findFirstByCustomerIdAndRole(Long customerId, Role role);
}
