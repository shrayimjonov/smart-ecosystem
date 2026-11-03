package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.ServiceOrderInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.serviceorderinvoice.admin.ServiceOrderInvoiceListAdminDTO;
import uz.uzkassa.cts.dto.xfile.XfileShortDTO;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;
import uz.uzkassa.cts.repository.ServiceOrderInvoiceRepository;
import uz.uzkassa.cts.service.ServiceOrderInvoiceService;

import java.util.List;

@Service
public class ServiceOrderInvoiceServiceImpl extends BaseServiceImpl<ServiceOrderInvoice, Long> implements ServiceOrderInvoiceService {

    private final ServiceOrderInvoiceRepository serviceOrderInvoiceRepository;

    public ServiceOrderInvoiceServiceImpl(ServiceOrderInvoiceRepository serviceOrderInvoiceRepository) {
        super(serviceOrderInvoiceRepository);
        this.serviceOrderInvoiceRepository = serviceOrderInvoiceRepository;
    }

    @Override
    public Page<ServiceOrderInvoiceListAdminDTO> getListForAdmin(ServiceOrderInvoiceFilter filter) {
        ResultList<ServiceOrderInvoice> resultList = serviceOrderInvoiceRepository.getResultList(filter);
        List<ServiceOrderInvoiceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(serviceOrderInvoice -> {
                    ServiceOrderInvoiceListAdminDTO serviceOrderInvoiceListAdminDTO = new ServiceOrderInvoiceListAdminDTO();
                    serviceOrderInvoiceListAdminDTO.setId(serviceOrderInvoice.getId());
                    serviceOrderInvoiceListAdminDTO.setStatus(serviceOrderInvoice.getStatus());
                    serviceOrderInvoiceListAdminDTO.setInvoiceNumber(serviceOrderInvoice.getNumber());
                    serviceOrderInvoiceListAdminDTO.setInvoiceDate(serviceOrderInvoice.getInvoiceDate());
                    serviceOrderInvoiceListAdminDTO.setTotalPrice(serviceOrderInvoice.getTotalPrice());
                    serviceOrderInvoiceListAdminDTO.setTotalVatPrice(serviceOrderInvoice.getTotalVatPrice());
                    serviceOrderInvoiceListAdminDTO.setTotalSellPrice(serviceOrderInvoice.getTotalSellPrice());
                    serviceOrderInvoiceListAdminDTO.setContract(serviceOrderInvoice.getContract().toContractShortDTO());
                    serviceOrderInvoiceListAdminDTO.setCustomer(serviceOrderInvoice.getContract().getCustomer().toCustomerShortDTO());
                    serviceOrderInvoiceListAdminDTO.setXfile(new XfileShortDTO(serviceOrderInvoice.getXFileId(), serviceOrderInvoice.getXfileStatus()));
                    return serviceOrderInvoiceListAdminDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }
}
