package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.DeviceSuspendHistory;
import uz.uzkassa.cts.repository.custom.DeviceSuspendHistoryRepositoryCustom;

import java.util.Optional;

@Repository
public interface DeviceSuspendHistoryRepository extends BaseRepository<DeviceSuspendHistory, Long>, DeviceSuspendHistoryRepositoryCustom {
    Optional<DeviceSuspendHistory> findFirstByCustomerBranchDeviceIdOrderByIdDesc(Long customerBranchDeviceId);

    Page<DeviceSuspendHistory> findByContractIdOrderByIdDesc(Long contractId, Pageable pageable);

    @EntityGraph(attributePaths = {"contract", "tariff", "contract.customer"})
    Page<DeviceSuspendHistory> findAll(Specification specification, Pageable pageable);


}
