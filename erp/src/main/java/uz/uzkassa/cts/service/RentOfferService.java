package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.RentOffer;
import uz.uzkassa.cts.dto.rentoffer.request.RentOfferRequestDto;
import uz.uzkassa.cts.dto.rentoffer.response.RentOfferResponseDto;
import uz.uzkassa.cts.filter.TradeInContractFilter;

public interface RentOfferService extends BaseService <RentOffer, Long>{
    Page<RentOfferResponseDto> list(TradeInContractFilter filter);

    RentOfferResponseDto create(RentOfferRequestDto dto);

    RentOfferResponseDto getById(Long id);

    RentOffer getByIdAndCustomerId(Long id, Long customerId);

    RentOffer getByCustomerId(Long customerId);

    RentOfferResponseDto accept(Long id);

    RentOfferResponseDto reject(Long id);

    RentOfferResponseDto waitingForAttach(Long id);

    String getHtmlTemplate(Long id);

    RentOfferResponseDto uploadContractFile(Long id, MultipartFile file);

    Page<RentOfferResponseDto> listByTin(TradeInContractFilter filter, String tin);
}
