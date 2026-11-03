package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.TradeInAct;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.tradeinact.response.TradeInActResponseDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.filter.ActOfTransmissionFilter;

public interface TradeInActService extends BaseService<TradeInAct, Long>{
    TradeInActResponseDto getByContractId(Long id);

    Page<TradeInActResponseDto> getList(ActOfTransmissionFilter filter);

    TradeInActResponseDto getById(Long id);
    String getHtmlTemplate(Long id);


    TradeInActResponseDto uploadFile(Long id, MultipartFile file);

    TradeInActResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file);

    TradeInActResponseDto accept(Long id);

    TradeInActResponseDto changeToAttach(Long id);

    TradeInActResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto);

    String generatePoaTemplate(TradeInActPoaRequestDto dto);
}
