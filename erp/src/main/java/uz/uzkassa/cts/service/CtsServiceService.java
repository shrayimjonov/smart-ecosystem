package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CtsService;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.ctsservice.request.ServiceRequestDto;
import uz.uzkassa.cts.dto.ctsservice.response.ServiceResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface CtsServiceService extends BaseService<CtsService, Long> {

    ServiceResponseDto create(ServiceRequestDto dto);

    ServiceResponseDto update(ServiceRequestDto dto);

    ServiceResponseDto get(Long id);

    Page<ServiceResponseDto> list(BaseFilter filter);

    List<ServiceResponseDto> listNotPaged();

    List<ServiceResponseDto> oneTimeList(BaseFilter filter);

    Page<CommonDTO> lookUp(BaseFilter filter);

    void customDeleteById(Long id);

    List<CommonDTO> getBillingServices();
}
