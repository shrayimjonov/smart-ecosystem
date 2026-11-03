package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.tradeincountercontract.request.TradeInCounterContractEditRequestDto;
import uz.uzkassa.cts.dto.tradeincountercontract.request.TradeInCounterContractRequestDto;
import uz.uzkassa.cts.dto.tradeincountercontract.response.TradeInCounterContractResponseDto;
import uz.uzkassa.cts.filter.TradeInCounterContractFilter;

public interface TradeInCounterContractService {
    TradeInCounterContractResponseDto create(TradeInCounterContractRequestDto tradeInCounterContractDTO);

    TradeInCounterContractResponseDto update(TradeInCounterContractEditRequestDto tradeInCounterContractDTO);

    TradeInCounterContractResponseDto getById(Long id);

    Page<TradeInCounterContractResponseDto> list(TradeInCounterContractFilter filter);

    TradeInCounterContractResponseDto accept(Long id);

    TradeInCounterContractResponseDto reject(Long id);

    String getContractHtmlTemplate(Long id);

    TradeInCounterContractResponseDto uploadContractFile(Long id, MultipartFile file);

    Page<TradeInCounterContractResponseDto> listByTin(TradeInCounterContractFilter filter, String tin);

    TradeInCounterContractResponseDto uploadNote(Long id, MultipartFile file);

    TradeInCounterContractResponseDto uploadDeviceImage(Long id, MultipartFile file);
}
