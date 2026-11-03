package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.IncomingInvoice;
import uz.uzkassa.cts.domain.IncomingInvoiceDetail;
import uz.uzkassa.cts.dto.factura.*;
import uz.uzkassa.cts.mappings.IncomingInvoiceDetailMapper;
import uz.uzkassa.cts.mappings.IncomingInvoiceMapper;
import uz.uzkassa.cts.mappings.IncomingInvoiceProductMapper;
import uz.uzkassa.cts.repository.IncomingInvoiceRepository;
import uz.uzkassa.cts.service.IncomingInvoiceService;
import uz.uzkassa.cts.service.XfileService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IncomingInvoiceServiceImpl extends BaseServiceImpl<IncomingInvoice, Long> implements IncomingInvoiceService {

    private final IncomingInvoiceRepository repository;
    private final XfileService xfileService;
    private final IncomingInvoiceDetailMapper incomingInvoiceDetailMapper;
    private final IncomingInvoiceMapper incomingInvoiceMapper;
    private final IncomingInvoiceProductMapper incomingInvoiceProductMapper;

    public IncomingInvoiceServiceImpl(IncomingInvoiceRepository repository,
                                      XfileService xfileService,
                                      IncomingInvoiceDetailMapper incomingInvoiceDetailMapper,
                                      IncomingInvoiceMapper incomingInvoiceMapper,
                                      IncomingInvoiceProductMapper incomingInvoiceProductMapper) {
        super(repository);
        this.repository = repository;
        this.xfileService = xfileService;
        this.incomingInvoiceDetailMapper = incomingInvoiceDetailMapper;
        this.incomingInvoiceMapper = incomingInvoiceMapper;
        this.incomingInvoiceProductMapper = incomingInvoiceProductMapper;
    }

    private Set<IncomingInvoice> map(FacturaIncomingDto dto) {
        FacturaIncomingDataDto dataDto = dto.getData();
        List<InvoiceDTO> invoices = dataDto.getItems();
        Set<IncomingInvoice> incomingInvoices = new HashSet<>();
        for (InvoiceDTO invoiceDTO : invoices) {
            IncomingInvoice incomingInvoice = incomingInvoiceMapper.toEntity(invoiceDTO);
            incomingInvoice.setXfileId(invoiceDTO.getId());

            FacturaDTO facturaDTO = invoiceDTO.getFacturaDTO();
            IncomingInvoiceDetail invoiceDetail = incomingInvoiceDetailMapper.toEntity(facturaDTO);
            invoiceDetail.setIncomingInvoice(incomingInvoice);
            incomingInvoice.setInvoiceDetail(invoiceDetail);

            for (FacturaProductDTO facturaProductDTO : facturaDTO.getProductList().getProducts()) {
                invoiceDetail.addChild(incomingInvoiceProductMapper.toEntity(facturaProductDTO));
            }
            incomingInvoices.add(incomingInvoice);
        }
        return incomingInvoices;
    }

    @Override
    public void saveInvoices(GetIncomingInvoiceRequest dto) {
        Set<IncomingInvoice> incomingInvoices = map(xfileService.getDataByTin(dto));
        Set<IncomingInvoice> invoicesFromDb = findAllByCreatedDateBetween(dto.getFrom(), dto.getTo(), dto.getSupplierTin());

        List<IncomingInvoice> invoiceToAddDb = new ArrayList<>();
        for (IncomingInvoice invoice : incomingInvoices) {
            if (!invoicesFromDb.contains(invoice)) {
                invoiceToAddDb.add(invoice);
            }
        }
        if (!invoiceToAddDb.isEmpty())
            repository.saveAll(invoiceToAddDb);
    }

    private Set<IncomingInvoice> findAllByCreatedDateBetween(LocalDateTime createDate, LocalDateTime endDate, String tin) {
        return repository.findAllByCreatedDateBetween(createDate.toLocalDate().atStartOfDay(), endDate.toLocalDate().atTime(LocalTime.MAX), tin);
    }
}
