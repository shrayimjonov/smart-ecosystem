package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.TradeInContract;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInContractRequestDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInDeviceAddRequestDto;
import uz.uzkassa.cts.dto.tradeincontract.response.TradeInContractResponseDto;
import uz.uzkassa.cts.filter.TradeInContractFilter;

import java.util.List;

public interface TradeInContractService extends BaseService<TradeInContract, Long>{

    TradeInContractResponseDto create(TradeInContractRequestDto tradeInContractDTO);

    TradeInContractResponseDto getById(Long id);

    Page<TradeInContractResponseDto> list(TradeInContractFilter filter);
    Page<TradeInContractResponseDto> getList(TradeInContractFilter filter);

    TradeInContractResponseDto accept(Long id);

    TradeInContractResponseDto reject(Long id);

    TradeInContractResponseDto waitingForAttach(Long id);

    TradeInContractResponseDto rejectWithActs(Long id);

    String getContractHtmlTemplate(Long id);

    TradeInContractResponseDto uploadContractFile(Long id, MultipartFile file);

    Page<TradeInContractResponseDto> listByTin(TradeInContractFilter filter, String tin);

    TradeInContractResponseDto addDevice(Long id, TradeInDeviceAddRequestDto dto, boolean skipValidation);

    List<TradeInContractResponseDto> getContractWithoutAct(String tin);

//    String contractHTMLTest(Long id);
}
