package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Waybill;
import uz.uzkassa.silen.dto.filter.WaybillFilter;

import java.util.List;

public interface WaybillRepositoryCustom {

    Page<Waybill> findAllByFilter(WaybillFilter filter);

    List<Waybill> getWarehouseStats(WaybillFilter filter);

}
