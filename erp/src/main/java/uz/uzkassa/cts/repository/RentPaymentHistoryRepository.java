package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RentPaymentHistory;
import uz.uzkassa.cts.domain.RentPaymentHistoryId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentPaymentHistoryRepository extends  BaseRepository<RentPaymentHistory, RentPaymentHistoryId>{

    List<RentPaymentHistory> findByIdIn(List<RentPaymentHistoryId> ids);

    @EntityGraph(attributePaths = {"rentActAccessory", "rentActAccessory.accessory"})
    List<RentPaymentHistory> findByRentActAccessoryIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"customer"})
    Page<RentPaymentHistory> findAll(Specification<RentPaymentHistory> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"customer"})
    List<RentPaymentHistory> findByOfferId(Long offerId);

    @EntityGraph(attributePaths = {"customer"})
    List<RentPaymentHistory> findByOfferIdAndInvoiceIdIsNull(Long offerId);

    @EntityGraph(attributePaths = {"customer"})
    List<RentPaymentHistory> findByInvoiceId(Long invoiceId);

    @EntityGraph(attributePaths = {"customer"})
    List<RentPaymentHistory> findByInvoiceIdIsNull();

    @Query("select distinct sum(rph.amount) from RentPaymentHistory rph " +
        "inner join rph.customer c " +
        "where c.id = ?1 and rph.invoiceId is null ")
    BigDecimal unpaidHistoriesSum(Long customerId);

    @EntityGraph(attributePaths = {"customer"})
    Page<RentPaymentHistory> findByCustomerIdAndInvoiceIdIsNull(Long customerId, Pageable pageable);
    @Query("select his from RentPaymentHistory his" +
        " inner join fetch his.rentActAccessory acc " +
        " inner join fetch acc.accessory a " +
        " where acc.createdDate <= :accCreateDate " +
        " and his.createdDate >= :hisCreateDate " +
        " and (his.bonus is null or his.bonus = 0) ")
    List<RentPaymentHistory> findBonusCalcList(@Param("accCreateDate") LocalDateTime accCreateDate,
                                               @Param("hisCreateDate") LocalDateTime hisCreateDate);
}
