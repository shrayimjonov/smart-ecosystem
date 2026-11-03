package uz.uzkassa.smartpos.repository.postgres.warehouse;

import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.filter.ShipmentFilter;

import java.util.List;

public interface ShipmentRepositoryCustom {
    List<CommonDTO> lookup(ShipmentFilter filter);
}
