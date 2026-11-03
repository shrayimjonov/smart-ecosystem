package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CtsServiceOrderSpecification;
import uz.uzkassa.cts.repository.custom.CtsServiceOrderSpecificationRepositoryCustom;

@Repository
public interface CtsServiceOrderSpecificationRepository extends BaseRepository<CtsServiceOrderSpecification, Long>, CtsServiceOrderSpecificationRepositoryCustom {
    Page<CtsServiceOrderSpecification> findByContractIdOrderByIdDesc(Long contractId, Pageable pageable);
}
