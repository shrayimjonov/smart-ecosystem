package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.stock.ActiveCustomerBaseDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportByProductDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportByProductExtraInfoDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportCompanyDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportOrderDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.filters.ActiveCustomerFilter;
import uz.uzkassa.smartpossupply.filters.StockReportByProductFilter;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockReportService {
    StockOutRepository stockOutRepository;
    StockProductRepository stockProductRepository;
    PartnerRepository partnerRepository;
    OrderRepository orderRepository;

    public Page<ActiveCustomerBaseDTO> getActiveCustomerBase(ActiveCustomerFilter filter, Boolean notActive) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        if (Boolean.TRUE.equals(notActive)) {
            ResultList<ActiveCustomerBaseDTO> activeCustomerBase = partnerRepository.getNotActiveCustomers(filter);
            return new PageImpl<>(activeCustomerBase.getList(), filter.getSortedPageable(), activeCustomerBase.getCount());
        }
        ResultList<ActiveCustomerBaseDTO> activeCustomerBase = stockOutRepository.getActiveCustomerBase(filter);
        return new PageImpl<>(activeCustomerBase.getList(), filter.getSortedPageable(), activeCustomerBase.getCount());
    }

    public Page<StockReportByProductDTO> getStockReport(StockReportByProductFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        ResultList<StockReportByProductDTO> resultList = stockProductRepository.getStockReport(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public Page<StockReportByProductExtraInfoDTO> getStockReportExtraInfo(StockReportByProductFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        ResultList<StockReportByProductExtraInfoDTO> resultList = stockProductRepository.getStockReportExtraInfo(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public Map<String, Long> allCustomerCount() {
        Long allCustomerCount = partnerRepository.countDistinctByCompanyIdAndStatus(SecurityUtils.getCompanyId(), CompanyStatus.ACTIVE);
        return Map.of("allCustomerCount", allCustomerCount);
    }

    public List<StockReportCompanyDTO> getStockReportCompanyInfo(StockReportByProductFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        return stockProductRepository.getStockReportCompanyInfo(filter);
    }

    public StockReportOrderDTO getLastOrder(Long customerId) {
        Optional<Order> optionalOrder = orderRepository.findFirstByCompanyIdAndCustomerIdAndDeletedIsFalseOrderByOrderDateDesc(SecurityUtils.getCompanyId(), customerId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            return new StockReportOrderDTO(order.getId(), order.getOrderNumber(), order.getAgent().toCommonDTO(), order.getOrderDate());
        }
        return null;
    }
}
