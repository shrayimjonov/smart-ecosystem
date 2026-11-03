package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.ContractForLeasing;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.contractforleasing.request.ContractForLeasingDeviceRequestDto;
import uz.uzkassa.cts.dto.contractforleasing.request.ContractForLeasingRequestDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingDeviceCheckResponseDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingListResponseDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingResponseDto;
import uz.uzkassa.cts.filter.ContractForLeasingFilter;

import java.util.List;

public interface ContractLeasingFactoryService extends BaseService<ContractForLeasing, Long> {

    Page<ContractForLeasingListResponseDto> list(ContractForLeasingFilter filter);

    ContractForLeasingResponseDto getById(Long id);

    void earlyCloseContract(Long id);

    ContractForLeasingResponseDto create(ContractForLeasingRequestDto dto);

    ContractForLeasingResponseDto addDevice(Long id, ContractForLeasingDeviceRequestDto deviceRequestDto);

    String getContractHtmlTemplate(Long id);

    String getOfferHtmlTemplate();

    String getWarrantyHtmlTemplate(Long id);

    ContractForLeasingResponseDto uploadContractFile(Long id, MultipartFile file);

    ContractForLeasingResponseDto uploadWarrantyFile(Long id, MultipartFile file);

    ContractForLeasingResponseDto accept(Long id);

    ContractForLeasingResponseDto changeToAttach(Long id);

    ContractForLeasingDeviceCheckResponseDto getForDeviceAdd(Long id);

    List<ContractForLeasingDeviceCheckResponseDto> getForDeviceAddByCustomerId(Long customerId, String companyType, String deviceType);

    Page<ContractForLeasingListResponseDto> listByTin(ContractForLeasingFilter filter, String tin);

    List<CommonCodeDTO> getItems(String type);

    ContractForLeasingResponseDto reject(Long id);

    ContractForLeasingResponseDto rejectWithActs(Long id);

    String getPaymentQuote(Long id);

    void verifyCode(Long id, String code);
}
