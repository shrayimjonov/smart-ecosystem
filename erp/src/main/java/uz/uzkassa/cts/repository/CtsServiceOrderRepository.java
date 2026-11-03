package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CtsServiceOrder;

import java.util.Optional;

@Repository
public interface CtsServiceOrderRepository extends BaseRepository<CtsServiceOrder, Long> {

    @EntityGraph(attributePaths = {"serviceOrderSpecification", "customer"})
    Page<CtsServiceOrder> findAll(Specification<CtsServiceOrder> specification, Pageable pageable);

    @Query(value = """
            select string_agg(d.serial_number, '; ') from cts_service_order cso\s
            inner join order_tariff ot on cso.id = ot.service_order_id\s
            inner join device_tariff dt on ot.id = dt.order_tariff_id\s
            inner join customer_branch_device cbd on dt.customer_branch_devices_id = cbd.id\s
            inner join device d on cbd.device_id = d.id
            where cso.id =:orderId group by cso.id""", nativeQuery = true)
    Optional<String> findDevices(@Param("orderId") Long orderId);
}
