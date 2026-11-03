package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Order;

/**
 * Spring Data  repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, String>, OrderRepositoryCustom {
    @Query("select max(intNumber) from Order where organizationId=?1")
    Integer getMaxIntNumber(String organizationId);
}
