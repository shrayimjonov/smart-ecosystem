package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBranchRent;
import uz.uzkassa.cts.enums.CommonStatus;

import java.util.Optional;

@Repository
public interface CustomerBranchRentRepository extends BaseRepository<CustomerBranchRent, Long> {

    Optional<CustomerBranchRent> findFirstByCustomerBranchIdAndStatus(Long customerBranchId, CommonStatus status);

    Optional<CustomerBranchRent> findFirstByCustomerBranchIdAndRentNumber(Long customerBranchId, String rentNumber);

    @Modifying
    @Query("update CustomerBranchRent cbr set cbr.status=:status where cbr.customerBranchId=:customerBranchId")
    void updateStatus(@Param("status") CommonStatus status, @Param("customerBranchId") Long customerBranchId);

    boolean existsByCustomerBranchIdAndRentNumber(Long customerBranchId, String rentNumber);

    boolean existsByCustomerBranchIdAndRentNumberAndIdNot(Long customerBranchId, String rentNumber, Long id);

}
