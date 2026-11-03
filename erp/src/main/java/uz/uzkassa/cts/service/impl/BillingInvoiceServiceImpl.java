package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.BillingInvoice;
import uz.uzkassa.cts.dto.billing.BillingInvoiceDTO;
import uz.uzkassa.cts.dto.billing.BillingInvoicePayloadDTO;
import uz.uzkassa.cts.dto.integration.billing.InvoiceResponseDto;
import uz.uzkassa.cts.integrations.BillingServiceFeignClient;
import uz.uzkassa.cts.repository.BillingInvoiceRepository;
import uz.uzkassa.cts.service.BillingInvoiceService;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class BillingInvoiceServiceImpl extends BaseServiceImpl<BillingInvoice, Long> implements BillingInvoiceService {

    private final BillingInvoiceRepository billingInvoiceRepository;
    private final BillingServiceFeignClient billingServiceFeignClient;

    public BillingInvoiceServiceImpl(BillingInvoiceRepository billingInvoiceRepository,
                                     BillingServiceFeignClient billingServiceFeignClient) {
        super(billingInvoiceRepository);
        this.billingInvoiceRepository = billingInvoiceRepository;
        this.billingServiceFeignClient = billingServiceFeignClient;
    }

    @Override
    @Transactional
    public Long create(BillingInvoiceDTO billingInvoiceDTO) {
        BillingInvoice billingInvoice = new BillingInvoice();
        billingInvoice.setEntityId(billingInvoiceDTO.getEntityId());
        billingInvoice.setEntityType(billingInvoiceDTO.getEntityType());
        saveAndFlash(billingInvoice);
        return billingInvoice.getId();
    }

    @Override
    @Transactional
    public void createPrimeInvoice(BillingInvoicePayloadDTO billingInvoicePayloadDTO) {
        Optional<BillingInvoice> optionalBillingInvoice = billingInvoiceRepository.findById(billingInvoicePayloadDTO.getBillingInvoiceId());
        InvoiceResponseDto invoiceResponseDto = billingServiceFeignClient.createPrimeInvoice(billingInvoicePayloadDTO);
        log.info("Response create prime invoice object: {}", invoiceResponseDto);
        if (optionalBillingInvoice.isPresent()) {
            BillingInvoice billingInvoice = optionalBillingInvoice.get();
            billingInvoice.setDate(LocalDateTime.now());
            billingInvoice.setBillingInvoiceId(invoiceResponseDto.getId());
            saveAndFlash(billingInvoice);
        }
    }
}
