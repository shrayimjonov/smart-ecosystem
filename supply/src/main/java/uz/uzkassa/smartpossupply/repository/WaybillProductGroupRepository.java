package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.waybill.WaybillProductGroup;

@Repository
public interface WaybillProductGroupRepository extends BaseRepository<WaybillProductGroup, Long> {

    @Modifying
    @Query("update WaybillProductGroup wpg set wpg.deleted=true where wpg.waybillTransportId =:waybillTransportId")
    void deleteAllByWaybillTransportId(@Param("waybillTransportId") Long waybillTransportId);
}
