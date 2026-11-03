package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.ServicePaymentHistory;
import uz.uzkassa.cts.dto.servicepaymenthistory.response.ServicePaymentHistoryListDto;
import uz.uzkassa.cts.dto.servicepaymenthistory.response.ServicePaymentHistoryResponseDto;
import uz.uzkassa.cts.mappings.ServicePaymentHistoryMapper;
import uz.uzkassa.cts.repository.ServicePaymentHistoryRepository;
import uz.uzkassa.cts.service.ServicePaymentHistoryService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ServicePaymentHistoryServiceImpl extends BaseServiceImpl<ServicePaymentHistory, Long> implements ServicePaymentHistoryService {

    private final ServicePaymentHistoryRepository repository;
    private final ServicePaymentHistoryMapper servicePaymentHistoryMapper;

    public ServicePaymentHistoryServiceImpl(ServicePaymentHistoryRepository repository, ServicePaymentHistoryMapper servicePaymentHistoryMapper) {
        super(repository);
        this.repository = repository;
        this.servicePaymentHistoryMapper = servicePaymentHistoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ServicePaymentHistoryListDto getByCustomerId(Long id) {
        List<ServicePaymentHistory> histories = repository.findByPaidIsFalseAndCustomerIdOrderByCreatedDateDesc(id);
        return getServicePaymentHistoryListDto(histories);
    }


    @Override
    @Transactional(readOnly = true)
    public ServicePaymentHistoryListDto getByInvoiceId(Long id) {
        List<ServicePaymentHistory> histories = repository.findByServiceOrderInvoiceId(id);
        return getServicePaymentHistoryListDto(histories);
    }


    private ServicePaymentHistoryListDto getServicePaymentHistoryListDto(List<ServicePaymentHistory> histories) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        ServicePaymentHistoryListDto listDto = new ServicePaymentHistoryListDto();
        List<ServicePaymentHistoryResponseDto> content = new ArrayList<>();
        for (ServicePaymentHistory history : histories) {
            totalAmount = totalAmount.add(history.getAmount());
            content.add(servicePaymentHistoryMapper.toDto(history));
        }
        listDto.setTotalAmount(totalAmount);
        listDto.setContent(content);
        return listDto;
    }
}
