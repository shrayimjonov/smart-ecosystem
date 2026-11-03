package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.factura.FacturaCheckStatusDataDto;
import uz.uzkassa.cts.dto.factura.FacturaDTO;
import uz.uzkassa.cts.dto.factura.FacturaIncomingDto;
import uz.uzkassa.cts.dto.factura.GetIncomingInvoiceRequest;
import uz.uzkassa.cts.dto.xfile.request.XfileChangeInvoiceStatusRequestDto;
import uz.uzkassa.cts.integrations.XfileClient;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.XfileService;

@Service
@Slf4j
public class XfileServiceImpl implements XfileService {
    private final RentInvoiceRepository rentInvoiceRepository;
    private final ServiceOrderInvoiceRepository repository;
    private final ActOfTransmissionInvoiceRepository actOfTransmissionInvoiceRepository;
    private final OneTimeOrderInvoiceRepository oneTimeOrderInvoiceRepository;
    private final TradeInInvoiceRepository tradeInInvoiceRepository;
    private final XfileClient xfileClient;

    public XfileServiceImpl(RentInvoiceRepository rentInvoiceRepository,
                            ServiceOrderInvoiceRepository repository, ActOfTransmissionInvoiceRepository actOfTransmissionInvoiceRepository,
                            OneTimeOrderInvoiceRepository oneTimeOrderInvoiceRepository, TradeInInvoiceRepository tradeInInvoiceRepository, XfileClient xfileClient) {
        this.rentInvoiceRepository = rentInvoiceRepository;
        this.repository = repository;
        this.actOfTransmissionInvoiceRepository = actOfTransmissionInvoiceRepository;
        this.oneTimeOrderInvoiceRepository = oneTimeOrderInvoiceRepository;
        this.tradeInInvoiceRepository = tradeInInvoiceRepository;
        this.xfileClient = xfileClient;
    }

    @Override
    public String authenticate() {
        return xfileClient.getToken();
    }


    @Override
    public String sendInvoice(FacturaDTO dto) {
        return xfileClient.sendInvoice(dto);
    }

    @Override
    public Long getTotalDataByTin(GetIncomingInvoiceRequest request) {
        return xfileClient.getTotalDataByTin(request);
    }

    @Override
    public void changeInvoiceStatus(XfileChangeInvoiceStatusRequestDto dto) {
        ServiceOrderInvoice serviceOrderInvoice = repository.findByxFileId(dto.getFacturaId());
        if (serviceOrderInvoice != null) {
            serviceOrderInvoice.setXfileStatus(dto.getStatus());
            repository.save(serviceOrderInvoice);
//            if (dto.getStatus() == XfileStatus.ACCEPTED) {
//                rabbitMqBonusProducer.createCalculateBonus(
//                    CalculateBonusPayloadDTO
//                        .builder()
//                        .serviceOrderInvoiceId(serviceOrderInvoice.getId())
//                        .build());
//            }
            return;
        }
        OneTimeOrderInvoice invoice = oneTimeOrderInvoiceRepository.findByxFileId(dto.getFacturaId());
        if (invoice != null) {
            invoice.setXfileStatus(dto.getStatus());
            oneTimeOrderInvoiceRepository.save(invoice);
//            if (dto.getStatus() == XfileStatus.ACCEPTED) {
//                rabbitMqBonusProducer.createCalculateBonus(
//                    CalculateBonusPayloadDTO
//                        .builder()
//                        .serviceOrderInvoiceId(invoice.getId())
//                        .build());
//            }
            return;
        }
        ActOfTransmissionInvoice actOfTransmissionInvoice = actOfTransmissionInvoiceRepository.findByXfileId(dto.getFacturaId());
        if (actOfTransmissionInvoice != null) {
            actOfTransmissionInvoice.setXfileStatus(dto.getStatus());
            actOfTransmissionInvoiceRepository.save(actOfTransmissionInvoice);
            return;
        }
        TradeInInvoice tradeInInvoice = tradeInInvoiceRepository.findByXfileId(dto.getFacturaId());
        if (tradeInInvoice != null) {
            tradeInInvoice.setXfileStatus(dto.getStatus());
            tradeInInvoiceRepository.save(tradeInInvoice);
            return;
        }
        RentInvoice rentInvoice = rentInvoiceRepository.findByXfileId(dto.getFacturaId());
        if (rentInvoice != null) {
            rentInvoice.setXfileStatus(dto.getStatus());
            rentInvoiceRepository.save(rentInvoice);
            return;
        }
        log.error("Invoice not found with this xFileId: {}", dto.getFacturaId());
    }

    @Override
    public FacturaCheckStatusDataDto checkFacturaStatus(String facturaId) {
        return xfileClient.checkFacturaStatus(facturaId);
    }

    @Override
    public FacturaIncomingDto getDataByTin(GetIncomingInvoiceRequest request) {
        return xfileClient.getDataByTin(request);
    }
}
