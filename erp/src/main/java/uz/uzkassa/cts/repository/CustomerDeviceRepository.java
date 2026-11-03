package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerDevice;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.repository.custom.CustomerDeviceRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDeviceRepository extends BaseRepository<CustomerDevice, Long>, CustomerDeviceRepositoryCustom {

    @Query("select count(cd.id) from CustomerDevice cd where cd.deleted=false and cd.deviceId= ?1")
    Long totalByDeviceId(Long deviceId);

    @Query(value = """
            select coalesce(sum(iip.count),0) from incoming_invoices ii\s
                 inner join incoming_invoice_detail iid on ii.id = iid.incoming_invoice_id\s
                 inner join incoming_invoices_products iip on iid.id = iip.incoming_invoice_detail_id where iid.buyer_tin =?1""", nativeQuery = true)
    Long getPaidDeviceCount(String tin);

    @Query(value = """
            select coalesce(count(cd.id),0) from customer_device cd
                                         inner join device d on cd.device_id = d.id
                                         inner join customer c on cd.customer_id = c.id
                                         inner join customer_branch cb on cb.customer_id = c.id
                                         inner join customer_branch_device cbd on cbd.device_id = d.id and cbd.customer_branch_id=cb.id
            where cd.paid=false and cd.deleted=false and cbd.deleted=false and cb.deleted=false and c.tin=?1""", nativeQuery = true)
    Long totalByCustomerTin(String tin);

    CustomerDevice findFirstByCustomerIdAndDeviceIdAndStatus(Long customerId, Long deviceId, DeviceStatus status);

    List<CustomerDevice> findAllByDeviceId(Long deviceId);

    CustomerDevice findFirstByCustomerIdAndDeviceId(Long customerId, Long deviceId);

    @Query(value = "select d.serial_number from customer_device cd inner join device d on cd.device_id=d.id where cd.customer_id = ?1", nativeQuery = true)
    List<String> findByCustomerIdWithDeleted(Long customerId);

    @Query(value = "select * from customer_device cd where cd.id=?1", nativeQuery = true)
    CustomerDevice findByIdWithDeleted(Long id);

    @Query(value = """
            SELECT count(cd.id) FROM customer_device cd
            INNER JOIN customer c ON c.id = cd.customer_id
            WHERE c.company_id =?1
            """, nativeQuery = true)
    Long countByCompanyId(Long companyId);

    Optional<CustomerDevice> findFirstByDeviceIdOrderByIdDesc(Long deviceId);
}
