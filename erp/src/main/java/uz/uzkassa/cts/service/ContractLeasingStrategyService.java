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

public interface ContractLeasingStrategyService extends BaseService<ContractForLeasing, Long> {

    ContractForLeasingResponseDto create(ContractForLeasingRequestDto dto);

    ContractForLeasingResponseDto reject(Long id);

    ContractForLeasingResponseDto rejectWithActs(Long id);

    String getContractHtmlTemplate(Long id);

    String getWarrantyHtmlTemplate(Long id);

    ContractForLeasingResponseDto uploadContractFile(Long id, MultipartFile file);

    ContractForLeasingResponseDto uploadWarrantyFile(Long id, MultipartFile file);

    ContractForLeasingResponseDto accept(Long id);

    ContractForLeasingResponseDto changeToAttach(Long id);

    Page<ContractForLeasingListResponseDto> listByTin(ContractForLeasingFilter filter, String tin);

    ContractForLeasingResponseDto addDevice(Long id, ContractForLeasingDeviceRequestDto deviceRequestDto);

    ContractForLeasingDeviceCheckResponseDto getForDeviceAdd(Long id);

    List<ContractForLeasingDeviceCheckResponseDto> getForDeviceAddByCustomerId(Long id, String deviceType);

    void earlyCloseContract(Long contractId);

    List<CommonCodeDTO> getItems();

    String getPaymentQuote(Long id);
}
