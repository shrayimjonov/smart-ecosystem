package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CompanyDevice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.CompanyDeviceListNewDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.DeviceForTransferListDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;

import java.util.List;

@Repository
public interface CompanyDeviceRepositoryCustom {

    ResultList<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter);

    ResultList<CompanyDeviceListNewDTO> getList(CompanyDeviceListFilter filter);

    ResultList<CompanyTerminalListDTO> getListAll(CompanyBranchDeviceFilter filter);

    ResultList<CompanyDevice> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter);
}
