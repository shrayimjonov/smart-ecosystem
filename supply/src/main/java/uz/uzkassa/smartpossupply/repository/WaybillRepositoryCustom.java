package uz.uzkassa.smartpossupply.repository;


import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.waybill.Waybill;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.WaybillFilter;
@Repository
public interface WaybillRepositoryCustom {

    ResultList<Waybill> getResultList(WaybillFilter filter);

    Long getMonthStatistics(BaseFilter filter);
}
