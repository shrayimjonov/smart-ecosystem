package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.HumoConnect;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectOperatorStatusRequestDto;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectStatusRequestDto;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectResponseDto;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectLastReceiptRequestDTO;
import uz.uzkassa.cts.filter.HumoConnectFilter;

public interface HumoConnectService extends BaseService<HumoConnect, Long> {
    Page<HumoConnectResponseDto> list(HumoConnectFilter filter);

    void updateStatus(HumoConnectStatusRequestDto dto);

    void updateOperatorStatus(HumoConnectOperatorStatusRequestDto dto);

    void changeDocFlowStatus(Long id, String docFlowStatus);

    byte[] download(HumoConnectFilter filter);

    void changeLicenseByImportExcel(MultipartFile file);

    byte[] changeDocFlowStatusByExcel(MultipartFile file, String docFlowStatus);

    void updateLastReceipt(HumoConnectLastReceiptRequestDTO humoConnectLastReceiptRequestDTO);
}
