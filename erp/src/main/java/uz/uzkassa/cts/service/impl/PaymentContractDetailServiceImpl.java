package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.domain.PaymentContractDetail;
import uz.uzkassa.cts.domain.report.ContractReport;
import uz.uzkassa.cts.dto.payment.PaymentContractActDTO;
import uz.uzkassa.cts.dto.payment.PaymentContractDetailDTO;
import uz.uzkassa.cts.enums.ContractReportInvoiceSaleStatus;
import uz.uzkassa.cts.repository.BaseRepository;
import uz.uzkassa.cts.repository.ContractReportRepository;
import uz.uzkassa.cts.repository.PaymentContractDetailRepository;
import uz.uzkassa.cts.service.DeviceService;
import uz.uzkassa.cts.service.PaymentContractDetailService;

import java.util.Optional;

@Transactional
@Service
@Slf4j
public class PaymentContractDetailServiceImpl extends BaseServiceImpl<PaymentContractDetail, Long> implements PaymentContractDetailService {

    private final PaymentContractDetailRepository paymentContractDetailRepository;
    private final DeviceService deviceService;
    private final ContractReportRepository contractReportRepository;

    public PaymentContractDetailServiceImpl(BaseRepository<PaymentContractDetail, Long> repository,
                                            PaymentContractDetailRepository paymentContractDetailRepository,
                                            DeviceService deviceService, ContractReportRepository contractReportRepository) {
        super(repository);
        this.paymentContractDetailRepository = paymentContractDetailRepository;
        this.deviceService = deviceService;
        this.contractReportRepository = contractReportRepository;
    }

    @Override
    public void create(PaymentContractDetailDTO detailDTO) {
        detailDTO.getDeviceIds().forEach(deviceId -> {
            Device device = deviceService.findById(deviceId);
            if (device != null) {
                Optional<ContractReport> optionalContractReport = contractReportRepository.findBySerialNumber(device.getSerialNumber());
                if (optionalContractReport.isPresent() && ContractReportInvoiceSaleStatus.RENTAL_CDTI.equals(optionalContractReport.get().getSaleStatus())) {
                    throw validationService.badRequestExceptionThrow(SELL_DEVICE).get();
                }
            }
            PaymentContractDetail detail = new PaymentContractDetail();
            detail.setDeviceId(deviceId);
            detail.setPaymentContractId(detailDTO.getPaymentContractId());
            detail.setVat1pc(detailDTO.getVat1pc());
            detail.setPriceFor1pc(detailDTO.getPrice1pc());
            detail.setTotalPriceWithVat(detailDTO.getPriceWithVat());
            detail.setTotalProductCount(1);
            detail.setPriceFor1pcWithVat(detailDTO.getPrice1pcWithVat());
            paymentContractDetailRepository.save(detail);
        });
    }

    @Override
    public void generateAct(PaymentContractActDTO dto) {

    }
}
