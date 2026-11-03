package uz.uzkassa.cts.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.CtsServiceOrderSpecification;
import uz.uzkassa.cts.dto.orderspecification.request.OrderSpecificationChangeStatusDto;
import uz.uzkassa.cts.dto.orderspecification.request.OrderSpecificationRequestDto;
import uz.uzkassa.cts.dto.orderspecification.response.OrderSpecificationResponseDto;
import uz.uzkassa.cts.dto.orderspecification.response.ServiceOrderSpecificationListDto;
import uz.uzkassa.cts.filter.BaseFilter;

public interface ServiceOrderSpecificationOldService extends BaseService<CtsServiceOrderSpecification, Long> {
    OrderSpecificationResponseDto create(OrderSpecificationRequestDto dto);

    String getSpecificationHtmlTemplate(Long id);

    OrderSpecificationResponseDto uploadFile(Long id, MultipartFile file);

    Resource downloadFile(Long id);

    OrderSpecificationResponseDto changeStatus(Long id, OrderSpecificationChangeStatusDto dto);

    OrderSpecificationRequestDto update(OrderSpecificationRequestDto dto);

    OrderSpecificationResponseDto getById(Long id);

    Page<OrderSpecificationResponseDto> getByContractId(Long contractId, BaseFilter filter);

    Page<ServiceOrderSpecificationListDto> list(BaseFilter filter);
}
