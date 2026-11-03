package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Vehicle;
import uz.uzkassa.cts.dto.vehicle.VehicleDTO;
import uz.uzkassa.cts.dto.vehicle.VehicleDetailDTO;
import uz.uzkassa.cts.dto.vehicle.VehicleListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

public interface VehicleService extends BaseService<Vehicle, Long> {

    Long create(VehicleDTO vehicleDTO);

    Long update(Long id, VehicleDTO vehicleDTO);

    VehicleDetailDTO get(Long id);

    Page<VehicleListDTO> getList(BaseFilter filter);

    void updateStatus(Long id, CommonStatus commonStatus);
}
