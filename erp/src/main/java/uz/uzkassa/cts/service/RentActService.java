package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.RentAct;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentact.request.RentActCreateRequestDto;
import uz.uzkassa.cts.dto.rentact.request.RentActRequestDto;
import uz.uzkassa.cts.dto.rentact.request.RentActUpdateRequestDto;
import uz.uzkassa.cts.dto.rentact.response.RentActListResponseDto;
import uz.uzkassa.cts.dto.rentact.response.RentActResponseDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface RentActService extends BaseService<RentAct, Long> {

    List<RentActListResponseDto> getByOfferId(Long id);

    Page<RentActListResponseDto> getList(BaseFilter filter);

    Page<RentActListResponseDto> listByTin(String tin, BaseFilter filter);

    RentActResponseDto create(RentActCreateRequestDto dto);

    List<RentActResponseDto> getActsToAddDevices(Long customerId);

    RentActResponseDto addDevice(RentActRequestDto dto);

    RentActResponseDto update(RentActUpdateRequestDto dto);

    RentActResponseDto getById(Long id);

    String getHtmlTemplate(Long id);

    RentActResponseDto uploadFile(Long id, MultipartFile file);

    RentActResponseDto uploadImage(Long id, MultipartFile file);

    RentActResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file);

    RentActResponseDto accept(Long id);

    RentActResponseDto changeToAttach(Long id);

    RentActResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto);

    String generatePoaTemplate(TradeInActPoaRequestDto dto);
}
