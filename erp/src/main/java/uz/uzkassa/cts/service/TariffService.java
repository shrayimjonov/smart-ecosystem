package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Tariff;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.ctsservice.response.ServiceResponseDto;
import uz.uzkassa.cts.dto.tariff.TariffTotalDTO;
import uz.uzkassa.cts.dto.tariff.request.TariffRequestDto;
import uz.uzkassa.cts.dto.tariff.request.TariffServiceDto;
import uz.uzkassa.cts.dto.tariff.response.TariffServiceResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;

import java.util.List;

public interface TariffService extends BaseService<Tariff, Long> {

    TariffServiceResponseDto create(TariffRequestDto tariffDto);

    TariffRequestDto update(TariffRequestDto tariffDto);

    TariffServiceResponseDto get(Long id);

    Page<TariffServiceResponseDto> list(BaseFilter filter);

    List<TariffTotalDTO> listByEmployee(EmployeeReportFilter filter);

    List<ServiceResponseDto> getTariffServices(Long id);

    TariffServiceResponseDto addService(TariffServiceDto dto);

    TariffServiceResponseDto removeService(TariffServiceDto dto);

    List<CommonCodeDTO> getItems();

}
