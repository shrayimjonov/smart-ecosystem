package uz.uzkassa.smartpos.repository.postgres;

import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementAdminDto;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementDeviceListDto;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.filter.AdvertisementFilter;

import java.util.List;

public interface AdvertisementRepositoryCustom {
    PageDto<AdvertisementAdminDto> findAllByFilter(AdvertisementFilter filter);

    List<AdvertisementDeviceListDto> advertisementForDevice(Device device);
}
