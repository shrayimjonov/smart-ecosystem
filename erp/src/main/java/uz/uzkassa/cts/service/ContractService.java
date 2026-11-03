package uz.uzkassa.cts.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.Contract;
import uz.uzkassa.cts.dto.contract.request.ContractChangeStatusDto;
import uz.uzkassa.cts.dto.contract.request.ContractDto;
import uz.uzkassa.cts.dto.contract.request.ContractFilterRequestDto;
import uz.uzkassa.cts.dto.contract.response.ContactResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface ContractService extends BaseService<Contract, Long> {

    ContactResponseDto createContractForService(ContractDto contractDto);

    ContactResponseDto createContractForBuy(ContractDto contractDto);

    ContactResponseDto createContractForLeasing(ContractDto contractDto);

    ContactResponseDto createContractForRent(ContractDto contractDto);

    ContactResponseDto update(ContractDto contractDto);

    ContactResponseDto get(Long id);

    Page<ContactResponseDto> listFrCustomer(ContractFilterRequestDto dto, BaseFilter filter);

    Page<ContactResponseDto> listForCto(ContractFilterRequestDto dto, BaseFilter filter);

    UploadFileResponse uploadFile(MultipartFile file, Long id);

    Resource loadFileAsResource(String tin, String fileName);

    List<ContactResponseDto> getByCustomerId(Long id);

    ContactResponseDto changeContractStatus(Long id, ContractChangeStatusDto dto);

    String getContractForBuyHtmlTemplate(Long id);

    ContactResponseDto getCustomerActiveContractForBuy(Long id);

    ContactResponseDto getCustomerActiveContractForService(Long id);

    ContactResponseDto getCustomerActiveContractForLeasing(Long id);

    ContactResponseDto getCustomerActiveContractForRent(Long id);

    Contract findByCustomerId(Long customerId);
}
