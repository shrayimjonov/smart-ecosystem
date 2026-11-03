package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.PaymentContract;
import uz.uzkassa.cts.dto.payment.PaymentContractActDTO;
import uz.uzkassa.cts.dto.payment.PaymentContractDTO;
import uz.uzkassa.cts.dto.payment.PaymentContractDetailResponseDTO;
import uz.uzkassa.cts.filter.PaymentContractFilter;

import java.util.List;

public interface PaymentContractService extends BaseService<PaymentContract, Long> {

    void create(PaymentContractDTO paymentContractDTO);

    PaymentContractDetailResponseDTO getById(Long id);

    List<PaymentContractDetailResponseDTO> getByCustomerId(Long id);

    List<PaymentContractDetailResponseDTO> getByContractsIsAccepted(Long id, String deviceType);

    Page<PaymentContractDetailResponseDTO> list(PaymentContractFilter filter);

    String generateContractHtml(Long id);

    PaymentContractDetailResponseDTO accept(Long id);

    PaymentContractDetailResponseDTO reject(Long id);

    PaymentContractDetailResponseDTO waitingForAttach(Long id);

    PaymentContractDetailResponseDTO uploadContractFile(Long id, MultipartFile file);

    void generateAct(PaymentContractActDTO dto);
}
