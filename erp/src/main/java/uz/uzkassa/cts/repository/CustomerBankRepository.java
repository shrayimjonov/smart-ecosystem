package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBank;

import java.util.Optional;

@Repository
public interface CustomerBankRepository extends BaseRepository<CustomerBank, Long> {

    @Query(value = "select cb from CustomerBank cb where cb.customerId = :customerId and cb.isDefault is true")
    Optional<CustomerBank> findFirstByCustomerIdAndDefaultIsTrue(@Param("customerId") Long customerId);

    CustomerBank findByCustomerIdAndBankId(Long customerId, Long bankId);

    @Modifying
    @Query(value = "update CustomerBank set isDefault =:isDefault where customerId=:customerId and id !=:id")
    void update(@Param("isDefault") boolean isDefault, @Param("customerId") Long customerId, @Param("id") Long id);

    boolean existsByBankIdAndCustomerIdAndAccountNumberAndDeletedIsFalse(Long bankId, Long customerId, String accountNumber);

    boolean existsByBankIdAndCustomerIdAndAccountNumberAndDeletedIsFalseAndIdNot(Long bankId, Long customerId, String accountNumber, Long id);
}
