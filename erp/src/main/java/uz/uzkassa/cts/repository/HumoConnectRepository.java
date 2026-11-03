package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.HumoConnect;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.repository.custom.HumoConnectRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface HumoConnectRepository extends BaseRepository<HumoConnect, Long>, HumoConnectRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = {"customer", "customer.branch", "lastModifier"})
    Page<HumoConnect> findAll(Specification<HumoConnect> spec, Pageable pageable);

    List<HumoConnect> findAllByCaseType(CaseType caseType);

    Optional<HumoConnect> findByCaseId(Long caseId);

    Optional<HumoConnect> findFirstByDeviceSerialNumberOrderByIdDesc(String serialNumber);

    Optional<HumoConnect> findFirstByDeviceSerialNumberAndCaseTypeOrderByIdDesc(String deviceSerialNumber, CaseType caseType);
}
