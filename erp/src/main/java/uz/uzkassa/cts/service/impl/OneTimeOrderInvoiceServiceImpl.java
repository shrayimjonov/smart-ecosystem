package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.OneTimeOrderInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.onetimeorderinvoice.admin.OneTimeOrderInvoiceListAdminDTO;
import uz.uzkassa.cts.dto.xfile.XfileShortDTO;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;
import uz.uzkassa.cts.repository.OneTimeOrderInvoiceRepository;
import uz.uzkassa.cts.service.OneTimeOrderInvoiceService;

import java.util.List;

@Service
public class OneTimeOrderInvoiceServiceImpl extends BaseServiceImpl<OneTimeOrderInvoice, Long> implements OneTimeOrderInvoiceService {

    private final OneTimeOrderInvoiceRepository oneTimeOrderInvoiceRepository;

    public OneTimeOrderInvoiceServiceImpl(OneTimeOrderInvoiceRepository oneTimeOrderInvoiceRepository) {
        super(oneTimeOrderInvoiceRepository);
        this.oneTimeOrderInvoiceRepository = oneTimeOrderInvoiceRepository;
    }

    @Override
    public Page<OneTimeOrderInvoiceListAdminDTO> getListForAdmin(OneTimeOrderInvoiceFilter filter) {
        ResultList<OneTimeOrderInvoice> resultList = oneTimeOrderInvoiceRepository.getResultList(filter);
        List<OneTimeOrderInvoiceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(oneTimeOrderInvoice -> {
                    OneTimeOrderInvoiceListAdminDTO oneTimeOrderInvoiceListAdminDTO = new OneTimeOrderInvoiceListAdminDTO();
                    oneTimeOrderInvoiceListAdminDTO.setId(oneTimeOrderInvoice.getId());
                    oneTimeOrderInvoiceListAdminDTO.setStatus(oneTimeOrderInvoice.getStatus());
                    oneTimeOrderInvoiceListAdminDTO.setInvoiceNumber(oneTimeOrderInvoice.getNumber());
                    oneTimeOrderInvoiceListAdminDTO.setInvoiceDate(oneTimeOrderInvoice.getInvoiceDate());
                    oneTimeOrderInvoiceListAdminDTO.setTotalProductCount(oneTimeOrderInvoice.getTotalProductCount());
                    oneTimeOrderInvoiceListAdminDTO.setTotalSellPrice(oneTimeOrderInvoice.getTotalSellPrice());
                    oneTimeOrderInvoiceListAdminDTO.setTotalPrice(oneTimeOrderInvoice.getTotalPrice());
                    oneTimeOrderInvoiceListAdminDTO.setTotalVatPrice(oneTimeOrderInvoice.getTotalVatPrice());
                    oneTimeOrderInvoiceListAdminDTO.setContract(oneTimeOrderInvoice.getContract().toContractShortDTO());
                    oneTimeOrderInvoiceListAdminDTO.setCustomer(oneTimeOrderInvoice.getCustomer().toCustomerShortDTO());
                    oneTimeOrderInvoiceListAdminDTO.setXfile(new XfileShortDTO(oneTimeOrderInvoice.getXFileId(), oneTimeOrderInvoice.getXfileStatus()));
                    return oneTimeOrderInvoiceListAdminDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }
}
