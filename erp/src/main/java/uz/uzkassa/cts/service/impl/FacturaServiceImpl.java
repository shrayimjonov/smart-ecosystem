package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.factura.FacturaBankDTO;
import uz.uzkassa.cts.dto.factura.FacturaDTO;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.integrations.FacturaClient;
import uz.uzkassa.cts.service.*;

import java.util.List;

@Service
@Slf4j
public class FacturaServiceImpl implements FacturaService {

    private final XfileService xfileService;
    private final ServiceOrderInvoiceOldService serviceOrderInvoiceOldService;
    private final OneTimeOrderInvoiceOldService oneTimeOrderInvoiceOldService;
    private final ActOfTransmissionInvoiceOldService actOfTransmissionInvoiceOldService;
    private final TradeInInvoiceOldService tradeInInvoiceOldService;
    private final RentInvoiceService rentInvoiceService;
    private final SaleInvoiceOldService saleInvoiceOldService;
    private final FacturaClient facturaClient;

    public FacturaServiceImpl(XfileService xfileService,
                              ServiceOrderInvoiceOldService serviceOrderInvoiceOldService,
                              OneTimeOrderInvoiceOldService oneTimeOrderInvoiceOldService,
                              ActOfTransmissionInvoiceOldService actOfTransmissionInvoiceOldService,
                              TradeInInvoiceOldService tradeInInvoiceOldService,
                              RentInvoiceService rentInvoiceService, SaleInvoiceOldService saleInvoiceOldService,
                              FacturaClient facturaClient) {
        this.xfileService = xfileService;
        this.serviceOrderInvoiceOldService = serviceOrderInvoiceOldService;
        this.oneTimeOrderInvoiceOldService = oneTimeOrderInvoiceOldService;
        this.actOfTransmissionInvoiceOldService = actOfTransmissionInvoiceOldService;
        this.tradeInInvoiceOldService = tradeInInvoiceOldService;
        this.rentInvoiceService = rentInvoiceService;
        this.saleInvoiceOldService = saleInvoiceOldService;
        this.facturaClient = facturaClient;
    }

    @Override
    public void sendServiceOrderInvoice(FacturaDTO facturaDTO) {
        String xfileId = xfileService.sendInvoice(facturaDTO);
        log.info("ServiceOrderInvoice response from x-file xFileId:{}", xfileId);
        ServiceOrderInvoice invoice = serviceOrderInvoiceOldService.findById(facturaDTO.getInvoiceId());
        invoice.setXFileId(xfileId);
        invoice.setXfileStatus(XfileStatus.DRAFT);
        serviceOrderInvoiceOldService.save(invoice);
    }

    @Override
    public void sendActOfTransmissionInvoice(FacturaDTO facturaDTO) {
        String xfileId = xfileService.sendInvoice(facturaDTO);
        log.info("OneTimeOrderInvoice response from x-file xFileId:{}", xfileId);
        ActOfTransmissionInvoice invoice = actOfTransmissionInvoiceOldService.findById(facturaDTO.getInvoiceId());
        invoice.setXfileId(xfileId);
        invoice.setXfileStatus(XfileStatus.DRAFT);
        actOfTransmissionInvoiceOldService.save(invoice);
    }

    @Override
    public void sendTradeInInvoice(FacturaDTO facturaDTO) {
        String xfileId = xfileService.sendInvoice(facturaDTO);
        log.info("OneTimeOrderInvoice response from x-file xFileId:{}", xfileId);
        TradeInInvoice invoice = tradeInInvoiceOldService.findById(facturaDTO.getInvoiceId());
        invoice.setXfileId(xfileId);
        invoice.setXfileStatus(XfileStatus.DRAFT);
        tradeInInvoiceOldService.save(invoice);
    }


    @Override
    @Transactional
    public void sendOneTimeOrderInvoice(FacturaDTO facturaDTO) {
        String xfileId = xfileService.sendInvoice(facturaDTO);
        log.info("OneTimeOrderInvoice response from x-file xFileId:{}", xfileId);
        /*customer_branch_rent where active not found*/
        OneTimeOrderInvoice invoice = oneTimeOrderInvoiceOldService.findById(facturaDTO.getInvoiceId());
        invoice.setXFileId(xfileId);
        invoice.setXfileStatus(XfileStatus.DRAFT);
        oneTimeOrderInvoiceOldService.save(invoice);
    }

    @Override
    public void sendRentInvoice(FacturaDTO facturaDTO) {
        String xfileId = xfileService.sendInvoice(facturaDTO);
        log.info("RentInvoic response from x-file xFileId:{}", xfileId);
        RentInvoice invoice = rentInvoiceService.findById(facturaDTO.getInvoiceId());
        invoice.setXfileId(xfileId);
        invoice.setXfileStatus(XfileStatus.DRAFT);
        rentInvoiceService.save(invoice);
    }

    @Override
    public void sendSaleInvoice(FacturaDTO facturaDTO) {
        String xfileId = xfileService.sendInvoice(facturaDTO);
        log.info("Sale Invoice response from x-file xFileId:{}", xfileId);
        SaleInvoice invoice = saleInvoiceOldService.findById(facturaDTO.getInvoiceId());
        invoice.setXfileId(xfileId);
        invoice.setXfileStatus(XfileStatus.DRAFT);
        saleInvoiceOldService.save(invoice);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FacturaBankDTO> getBanks(String language) {
        return facturaClient.getBanks(language);
    }
}
