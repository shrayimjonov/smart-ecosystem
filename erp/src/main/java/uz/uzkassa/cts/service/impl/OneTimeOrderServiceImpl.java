package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.OneTimeOrder;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.onetimeorder.OneTimeOrderListDTO;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;
import uz.uzkassa.cts.repository.OneTimeOrderRepository;
import uz.uzkassa.cts.service.OneTimeOrderService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OneTimeOrderServiceImpl extends BaseServiceImpl<OneTimeOrder, Long> implements OneTimeOrderService {

    OneTimeOrderRepository oneTimeOrderRepository;

    public OneTimeOrderServiceImpl(OneTimeOrderRepository oneTimeOrderRepository) {
        super(oneTimeOrderRepository);
        this.oneTimeOrderRepository = oneTimeOrderRepository;
    }

    @Override
    public Page<OneTimeOrderListDTO> getList(OneTimeOrderFilter filter) {
        ResultList<OneTimeOrder> resultList = oneTimeOrderRepository.getResultList(filter);
        List<OneTimeOrderListDTO> list = resultList
                .getList()
                .stream()
                .map(oneTimeOrder -> {
                    OneTimeOrderListDTO oneTimeOrderListDTO = new OneTimeOrderListDTO();
                    oneTimeOrderListDTO.setId(oneTimeOrder.getId());
                    oneTimeOrderListDTO.setContract(oneTimeOrder.getContract().toCommonDTO());
                    oneTimeOrderListDTO.setCustomer(oneTimeOrder.getCustomer().toCustomerShortDTO());
                    oneTimeOrderListDTO.setService(oneTimeOrder.getService().toCommonDTO());
                    oneTimeOrderListDTO.setCreator(oneTimeOrder.getCreator().toCommonDTO());
                    oneTimeOrderListDTO.setLastModifier(oneTimeOrder.getLastModifier().toCommonDTO());
                    oneTimeOrderListDTO.setFileUri(oneTimeOrder.getFileUri());
                    oneTimeOrderListDTO.setFileName(oneTimeOrder.getFileName());
                    oneTimeOrderListDTO.setContentType(oneTimeOrder.getContentType());
                    oneTimeOrderListDTO.setTotalProductCount(oneTimeOrder.getTotalProductCount());
                    oneTimeOrderListDTO.setTotalSellPrice(oneTimeOrder.getTotalSellPrice());
                    oneTimeOrderListDTO.setTotalPrice(oneTimeOrder.getTotalPrice());
                    oneTimeOrderListDTO.setTotalVatPrice(oneTimeOrder.getTotalVatPrice());
                    oneTimeOrderListDTO.setTotalVatPriceInWord(oneTimeOrder.getTotalVatPriceInWord());
                    oneTimeOrderListDTO.setTotalSellPriceInWord(oneTimeOrder.getTotalSellPriceInWord());
                    oneTimeOrderListDTO.setTotalPriceInWord(oneTimeOrder.getTotalPriceInWord());
                    oneTimeOrderListDTO.setStatus(oneTimeOrder.getStatus());
                    oneTimeOrderListDTO.setCreatedDate(oneTimeOrder.getCreatedDate());
                    oneTimeOrderListDTO.setNumber(oneTimeOrder.getNumber());
                    oneTimeOrderListDTO.setSerialNumberKKM(oneTimeOrder.getSerialNumberKKM());
                    return oneTimeOrderListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(list, filter.getOrderedPageable(), filter.getSize());
    }
}
