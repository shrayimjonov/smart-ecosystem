package uz.uzkassa.smartpos.repository.postgres.terminal;

import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.filter.DeviceFilter;

import java.util.List;

public interface DeviceRepositoryCustom {
    List<CommonDTO> lookup(DeviceFilter filter);
}
