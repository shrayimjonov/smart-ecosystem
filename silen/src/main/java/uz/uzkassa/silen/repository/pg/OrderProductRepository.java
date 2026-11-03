package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.OrderProduct;
import uz.uzkassa.silen.enumeration.BufferStatus;
import uz.uzkassa.silen.enumeration.SerialNumberType;

import java.util.List;

/**
 * Spring Data repository for the OrderProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, String>, OrderProductRepositoryCustom {

    @EntityGraph(attributePaths = {"order", "product"})
    List<OrderProduct> findAllByBufferStatusIsNullOrBufferStatus(BufferStatus bufferStatus);

    @EntityGraph(attributePaths = {"order", "product"})
    List<OrderProduct> findAllByBufferStatusAndSerialNumberTypeNot(BufferStatus bufferStatus, SerialNumberType serialNumberType);
}
