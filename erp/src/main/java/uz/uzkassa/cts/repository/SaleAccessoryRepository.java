package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.SaleAccessory;

import java.util.List;

@Repository
public interface SaleAccessoryRepository extends BaseRepository<SaleAccessory, Long> {

    List<SaleAccessory> findBySaleContractIdAndDeletedIsFalse(Long contractId);

    List<SaleAccessory> findBySaleActIdAndDeletedIsFalse(Long actId);

    @Modifying
    @Query(value = "update sale_accessory set sale_act_id = :saleActId where sale_contract_id = :saleContractId\n", nativeQuery = true)
    void updateActByContractId(@Param("saleActId") Long saleActId,
                               @Param("saleContractId") Long saleContractId);
}
