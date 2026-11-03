package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.TradeInCounterAct;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.tradeincounteract.response.TradeInCounterActResponseDto;
import uz.uzkassa.cts.filter.ActOfTransmissionFilter;

public interface TradeInCounterActService extends BaseService<TradeInCounterAct, Long>{

    TradeInCounterActResponseDto getByContractId(Long id);

    Page<TradeInCounterActResponseDto> getList(ActOfTransmissionFilter filter);

    TradeInCounterActResponseDto getById(Long id);

    String getHtmlTemplate(Long id);

    TradeInCounterActResponseDto uploadFile(Long id, MultipartFile file);

    TradeInCounterActResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file);

    TradeInCounterActResponseDto accept(Long id);

    TradeInCounterActResponseDto changeToAttach(Long id);

    TradeInCounterActResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto);

}
