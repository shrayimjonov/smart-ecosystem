package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.RentActOfReturn;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentactofreturn.request.RentActOfReturnRequestDto;
import uz.uzkassa.cts.dto.rentactofreturn.request.RentActOfReturnUpdateRequestDto;
import uz.uzkassa.cts.dto.rentactofreturn.response.RentActOfReturnListResponseDto;
import uz.uzkassa.cts.dto.rentactofreturn.response.RentActOfReturnResponseDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface RentActOfReturnService extends BaseService<RentActOfReturn, Long> {
    List<RentActOfReturnListResponseDto> getByCustomerId(Long id);

    Page<RentActOfReturnListResponseDto> getList(BaseFilter filter);

    Page<RentActOfReturnListResponseDto> listByTin(String tin, BaseFilter filter);

    RentActOfReturnResponseDto create(RentActOfReturnRequestDto dto);

    RentActOfReturnResponseDto update(RentActOfReturnUpdateRequestDto dto);

    RentActOfReturnResponseDto getById(Long id);

    String getHtmlTemplate(Long id);

    RentActOfReturnResponseDto uploadFile(Long id, MultipartFile file);

    RentActOfReturnResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file);

    RentActOfReturnResponseDto accept(Long id);

    RentActOfReturnResponseDto changeToAttach(Long id);

    RentActOfReturnResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto);

    String generatePoaTemplate(TradeInActPoaRequestDto dto);

    RentActOfReturnListResponseDto findByCustomerIdAndDeviceSerialNumber(Long customerId, String deviceSerialNumber);

    RentActOfReturnResponseDto uploadImage(Long id, MultipartFile file);
}
