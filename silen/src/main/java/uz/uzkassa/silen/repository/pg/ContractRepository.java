package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Contract;
import uz.uzkassa.silen.enumeration.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String>, ContractRepositoryCustom {

    Optional<Contract> findFirstByIdNotAndOrganizationIdAndNumberAndCustomerIdAndDateAndDeletedFalse(String contractId, String number, String organizationId, Long customerId, LocalDate date);

    Optional<Contract> findFirstByOrganizationIdAndCustomerIdAndNumberAndDateAndDeletedFalse(String organizationId, Long customerId, String number, LocalDate date);

    Optional<Contract> findFirstByOrganizationIdAndCustomerTinAndNumberAndDateAndDeletedFalse(String organizationId, String customerTin, String number, LocalDate date);

    @Modifying
    @Query(value = "UPDATE contract SET status='INVALID' WHERE DATE_PART('y',contract.created_date)=DATE_PART('y',now())", nativeQuery = true)
    void changeExpiredContractsStatus();

    List<Contract> findAllByOrganizationIdAndCustomerTinAndStatusAndDeletedFalse(String organizationId, String buyerTin, Status status);
}
