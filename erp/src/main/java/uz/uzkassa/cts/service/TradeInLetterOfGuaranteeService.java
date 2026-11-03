package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.TradeInLetterOfGuarantee;
import uz.uzkassa.cts.dto.contract.request.ContractChangeStatusDto;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.request.LetterOfGuaranteeRequestDto;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.request.LetterOfGuaranteeUpdateRequestDto;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.response.LetterOfGuaranteeResponseDto;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;

import java.util.List;

public interface TradeInLetterOfGuaranteeService extends BaseService <TradeInLetterOfGuarantee, Long>{
    Page<LetterOfGuaranteeResponseDto> list(TradeInInvoiceFilter filter);

    LetterOfGuaranteeResponseDto getById(Long id);

    LetterOfGuaranteeResponseDto create(LetterOfGuaranteeRequestDto dto);

    String getHtmlTemplate(Long id);

    LetterOfGuaranteeResponseDto update(LetterOfGuaranteeUpdateRequestDto dto);

    LetterOfGuaranteeResponseDto changeStatus(Long id, ContractChangeStatusDto dto);

    LetterOfGuaranteeResponseDto uploadFile(Long id, MultipartFile file);

    List<LetterOfGuaranteeResponseDto> getByCustomerIdForAttach(Long customerId);
}
