package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.repository.custom.CaseRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends BaseRepository<Cases, Long>, CaseRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = {"customer", "creator", "creator.branch"})
    List<Cases> findAll(Specification<Cases> specification);

    @Query(value = """
            select c.*\s
            from cases c
            where c.case_type = 'DEREGISTER_KKM'
            and cast(c.json_properties as json) ->> 'deviceSerialNumber' = :deviceSerialNumber\s
            and cast(c.json_properties as json) ->> 'fmSerialNumber' = :fmSerialNumber
            and c.customer_id = :customerId
            """, nativeQuery = true)
    List<Cases> findByJsonPropertiesInfo(@Param("deviceSerialNumber") String deviceSerialNumber,
                                         @Param("fmSerialNumber") String fmSerialNumber,
                                         @Param("customerId") Long customerId);

    @Query(value = """
            select c.*
            from cases c
            where c.customer_id = :customerId
            and c.case_type = :caseType
            and (cast(c.json_properties as json) ->> 'deviceId' <> '' and cast(c.json_properties as json) ->> 'deviceId' = :deviceId
             or cast(c.json_properties as json) ->> 'deviceSerialNumber' <> '' and cast(c.json_properties as json) ->> 'deviceSerialNumber' = :deviceSerialNumber)
            and c.status in (:caseStatuses)
            and c.deleted is false
            """, nativeQuery = true)
    Optional<Cases> findByTypeAndDeviceAndStatuses(
            @Param("customerId") Long customerId,
            @Param("caseType") String caseType,
            @Param("deviceId") String deviceId,
            @Param("deviceSerialNumber") String deviceSerialNumber,
            @Param("caseStatuses") List<Integer> caseStatuses);
}
