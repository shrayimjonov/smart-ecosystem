package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.PaymentAct;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.payment.PaymentActDetailDTO;
import uz.uzkassa.cts.dto.payment.PaymentActPoaRequestDTO;
import uz.uzkassa.cts.filter.PaymentActFilter;

import java.util.List;

public interface PaymentActService extends BaseService<PaymentAct, Long> {
    List<PaymentActDetailDTO> getByContractId(Long id);

    Page<PaymentActDetailDTO> getList(PaymentActFilter filter);

    PaymentActDetailDTO getById(Long id);

    String getHtmlTemplate(Long id);

    PaymentActDetailDTO uploadFile(Long id, MultipartFile file);

    PaymentActDetailDTO uploadPowerAttorneyFile(Long id, MultipartFile file);

    PaymentActDetailDTO accept(Long id);

    void calculateCustomerBalance(Long customerId);

    PaymentActDetailDTO changeToAttach(Long id);

    PaymentActDetailDTO defect(Long id);

    PaymentActDetailDTO changeDocFlowStatus(ActChangeDocFlowRequestDto dto);

    String generatePoaTemplate(PaymentActPoaRequestDTO dto);

    PaymentAct getByDeviceIdAndCustomerId(Long deviceId, Long customerId);
}
