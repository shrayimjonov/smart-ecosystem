package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.OneTimeOrder;
import uz.uzkassa.cts.dto.onetimeorder.request.OneTimeOrderChangeStatusRequestDto;
import uz.uzkassa.cts.dto.onetimeorder.request.OneTimeRequestCreatDto;
import uz.uzkassa.cts.dto.onetimeorder.request.OneTimeRequestDto;
import uz.uzkassa.cts.dto.onetimeorder.response.OneTimeOrderResponseDto;
import uz.uzkassa.cts.dto.serviceorder.ServiceOrderListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;

public interface OneTimeOrderOldService extends BaseService<OneTimeOrder, Long> {

    Page<ServiceOrderListDTO> listByEmployee(EmployeeReportFilter filter);

    Page<OneTimeOrderResponseDto> list(OneTimeOrderFilter filter);

    OneTimeOrderResponseDto create(OneTimeRequestCreatDto dto);

    OneTimeOrderResponseDto update(OneTimeRequestDto dto);

    OneTimeOrderResponseDto getById(Long id);

    OneTimeOrderResponseDto uploadFile(Long id, MultipartFile file);

    String getSpecificationHtmlTemplate(Long id);

    OneTimeOrderResponseDto changeStatus(OneTimeOrderChangeStatusRequestDto dto);

    Page<OneTimeOrderResponseDto> getByContractId(Long contractId, BaseFilter filter);

    byte[] download(OneTimeOrderFilter filter);
}
