package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.ActOfTransmissionInvoice;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.admin.ActOfTransmissionInvoiceListAdminDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.repository.ActOfTransmissionInvoiceRepository;
import uz.uzkassa.cts.service.ActOfTransmissionInvoiceService;

import java.util.List;

@Service
public class ActOfTransmissionInvoiceServiceImpl extends BaseServiceImpl<ActOfTransmissionInvoice, Long> implements ActOfTransmissionInvoiceService {

    private final ActOfTransmissionInvoiceRepository actOfTransmissionInvoiceRepository;

    public ActOfTransmissionInvoiceServiceImpl(ActOfTransmissionInvoiceRepository actOfTransmissionInvoiceRepository) {
        super(actOfTransmissionInvoiceRepository);
        this.actOfTransmissionInvoiceRepository = actOfTransmissionInvoiceRepository;
    }

    @Override
    public Page<ActOfTransmissionInvoiceListAdminDTO> getListForAdmin(PaymentScheduleInvoiceFilter filter) {
        ResultList<ActOfTransmissionInvoice> resultList = actOfTransmissionInvoiceRepository.getResultList(filter);
        List<ActOfTransmissionInvoiceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(actOfTransmissionInvoice -> {
                    ActOfTransmissionInvoiceListAdminDTO actOfTransmissionInvoiceListAdminDTO = new ActOfTransmissionInvoiceListAdminDTO();
                    actOfTransmissionInvoiceListAdminDTO.setId(actOfTransmissionInvoice.getId());
                    actOfTransmissionInvoiceListAdminDTO.setStatus(actOfTransmissionInvoice.getStatus());
                    actOfTransmissionInvoiceListAdminDTO.setInvoiceNumber(actOfTransmissionInvoice.getNumber());
                    actOfTransmissionInvoiceListAdminDTO.setInvoiceDate(actOfTransmissionInvoice.getInvoiceDate());
                    actOfTransmissionInvoiceListAdminDTO.setTotalProductCount(actOfTransmissionInvoice.getTotalProductCount());
                    actOfTransmissionInvoiceListAdminDTO.setPriceFor1pc(actOfTransmissionInvoice.getPriceFor1pc());
                    actOfTransmissionInvoiceListAdminDTO.setTotalPrice(actOfTransmissionInvoice.getTotalPrice());
                    actOfTransmissionInvoiceListAdminDTO.setTotalVatPrice(actOfTransmissionInvoice.getTotalVatPrice());
                    actOfTransmissionInvoiceListAdminDTO.setTotalSellPrice(actOfTransmissionInvoice.getTotalSellPrice());
                    actOfTransmissionInvoiceListAdminDTO.setAct(actOfTransmissionInvoice.getActOfTransmission().toActOfTransmissionShortDTO());
                    actOfTransmissionInvoiceListAdminDTO.setContract(actOfTransmissionInvoice.getContract().toContractShortDTO());
                    actOfTransmissionInvoiceListAdminDTO.setCustomer(actOfTransmissionInvoice.getActOfTransmission().getContract().getCustomer().toCustomerShortDTO());
                    actOfTransmissionInvoiceListAdminDTO.setXfileStatus(actOfTransmissionInvoice.getXfileStatus());
                    return actOfTransmissionInvoiceListAdminDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }
}
