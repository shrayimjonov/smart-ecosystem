package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.SaleInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.sale.invoice.admin.SaleInvoiceListAdminDTO;
import uz.uzkassa.cts.dto.xfile.XfileShortDTO;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.repository.SaleInvoiceRepository;
import uz.uzkassa.cts.service.SaleInvoiceService;

import java.util.List;

@Service
public class SaleInvoiceServiceImpl extends BaseServiceImpl<SaleInvoice, Long> implements SaleInvoiceService {

    private final SaleInvoiceRepository saleInvoiceRepository;

    public SaleInvoiceServiceImpl(SaleInvoiceRepository saleInvoiceRepository) {
        super(saleInvoiceRepository);
        this.saleInvoiceRepository = saleInvoiceRepository;
    }

    @Override
    public Page<SaleInvoiceListAdminDTO> getListForAdmin(TradeInInvoiceFilter filter) {
        ResultList<SaleInvoice> resultList = saleInvoiceRepository.getResultList(filter);
        List<SaleInvoiceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(saleInvoice -> {
                    SaleInvoiceListAdminDTO saleInvoiceListAdminDTO = new SaleInvoiceListAdminDTO();
                    saleInvoiceListAdminDTO.setId(saleInvoice.getId());
                    saleInvoiceListAdminDTO.setStatus(saleInvoice.getStatus());
                    saleInvoiceListAdminDTO.setInvoiceNumber(saleInvoice.getNumber());
                    saleInvoiceListAdminDTO.setInvoiceDate(saleInvoice.getInvoiceDate());
                    saleInvoiceListAdminDTO.setTotalProductCount(saleInvoice.getTotalProductCount());
                    saleInvoiceListAdminDTO.setTotalPrice(saleInvoice.getTotalPrice());
                    saleInvoiceListAdminDTO.setTotalVatPrice(saleInvoice.getTotalVatPrice());
                    saleInvoiceListAdminDTO.setTotalSellPrice(saleInvoice.getTotalSellPrice());
                    saleInvoiceListAdminDTO.setContract(saleInvoice.getSaleContract().toContractShortDTO());
                    saleInvoiceListAdminDTO.setCustomer(saleInvoice.getSaleContract().getCustomer().toCustomerShortDTO());
                    saleInvoiceListAdminDTO.setXfile(new XfileShortDTO(saleInvoice.getXfileId(), saleInvoice.getXfileStatus()));
                    return saleInvoiceListAdminDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }
}
