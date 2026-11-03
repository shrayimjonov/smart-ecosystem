package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.WhiteList;
import uz.uzkassa.cts.enums.WhiteListAction;
import uz.uzkassa.cts.repository.custom.WhiteListRepositoryCustom;

import java.time.LocalDate;

@Repository
public interface WhiteListRepository extends BaseRepository<WhiteList, Long>, WhiteListRepositoryCustom {

    boolean existsByCustomerIdAndAction(Long customerId, WhiteListAction action);

    boolean existsByCustomerIdAndActionAndIdNot(Long customerId, WhiteListAction action, Long id);

    @Modifying
    @Query(value = "update white_list set status = :status where to_date < :currentDate", nativeQuery = true)
    void updateStatus(@Param("status") String status, @Param("currentDate") LocalDate currentDate);

}
