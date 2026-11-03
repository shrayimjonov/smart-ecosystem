package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.TradeInInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.tradeininvoice.admin.TradeInInvoiceListAdminDTO;
import uz.uzkassa.cts.dto.xfile.XfileShortDTO;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.repository.TradeInInvoiceRepository;
import uz.uzkassa.cts.service.TradeInInvoiceService;

import java.util.List;

@Service
public class TradeInInvoiceServiceImpl extends BaseServiceImpl<TradeInInvoice, Long> implements TradeInInvoiceService {

    private final TradeInInvoiceRepository tradeInInvoiceRepository;

    public TradeInInvoiceServiceImpl(TradeInInvoiceRepository tradeInInvoiceRepository) {
        super(tradeInInvoiceRepository);
        this.tradeInInvoiceRepository = tradeInInvoiceRepository;
    }

    @Override
    public Page<TradeInInvoiceListAdminDTO> getListForAdmin(TradeInInvoiceFilter filter) {
        ResultList<TradeInInvoice> resultList = tradeInInvoiceRepository.getResultList(filter);
        List<TradeInInvoiceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(tradeInInvoice -> {
                    TradeInInvoiceListAdminDTO tradeInInvoiceListAdminDTO = new TradeInInvoiceListAdminDTO();
                    tradeInInvoiceListAdminDTO.setId(tradeInInvoice.getId());
                    tradeInInvoiceListAdminDTO.setStatus(tradeInInvoice.getStatus());
                    tradeInInvoiceListAdminDTO.setInvoiceNumber(tradeInInvoice.getNumber());
                    tradeInInvoiceListAdminDTO.setInvoiceDate(tradeInInvoice.getInvoiceDate());
                    tradeInInvoiceListAdminDTO.setTotalProductCount(tradeInInvoice.getDetail().getTotalProductCount());
                    tradeInInvoiceListAdminDTO.setTotalSellPrice(tradeInInvoice.getTotalSellPrice());
                    tradeInInvoiceListAdminDTO.setTotalPrice(tradeInInvoice.getTotalPrice());
                    tradeInInvoiceListAdminDTO.setTotalVatPrice(tradeInInvoice.getTotalVatPrice());
                    tradeInInvoiceListAdminDTO.setContract(tradeInInvoice.getContract().toContractShortDTO());
                    tradeInInvoiceListAdminDTO.setCustomer(tradeInInvoice.getContract().getCustomer().toCustomerShortDTO());
                    tradeInInvoiceListAdminDTO.setXfile(new XfileShortDTO(tradeInInvoice.getXfileId(), tradeInInvoice.getXfileStatus()));
                    return tradeInInvoiceListAdminDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }
}
