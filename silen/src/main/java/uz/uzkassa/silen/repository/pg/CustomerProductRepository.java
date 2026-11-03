package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.CustomerProduct;
import uz.uzkassa.silen.domain.CustomerProductId;

import java.math.BigDecimal;
import java.util.List;

/**
 * Spring Data repository for the CustomerProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerProductRepository extends JpaRepository<CustomerProduct, CustomerProductId>, CustomerProductRepositoryCustom {
    @Query("update CustomerProduct set discountPercent=?2, discount=?3 where customerId=?1")
    @Modifying
    void updateDiscount(Long customerId, BigDecimal discountPercent, BigDecimal discount);

    @EntityGraph(attributePaths = "product")
    List<CustomerProduct> findAllByCustomerId(Long customerId);


    @EntityGraph(attributePaths = "product")
    List<CustomerProduct> findAllByCustomerIdAndProductId(Long customerId, String productId);

    @Query("select productId from CustomerProduct where customerId=?1")
    List<String> findAllIds(Long customerId);
}
