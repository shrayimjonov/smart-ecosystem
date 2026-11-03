package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.PaymentCompanyContract;
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractDTO;
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractDetailDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface PaymentCompanyContractService extends BaseService<PaymentCompanyContract, Long> {

    PaymentCompanyContractDetailDTO create(PaymentCompanyContractDTO paymentCompanyContractDTO);

    PaymentCompanyContractDetailDTO getById(Long id);

    List<PaymentCompanyContractDetailDTO> getByCompanyId(Long id);

    List<PaymentCompanyContractDetailDTO> getContractsByStatusIsAccepted(Long id);

    Page<PaymentCompanyContractDetailDTO> list(BaseFilter filter);

    String generateContractHtml(Long id);

    PaymentCompanyContractDetailDTO uploadContractFile(Long id, MultipartFile file);
}
