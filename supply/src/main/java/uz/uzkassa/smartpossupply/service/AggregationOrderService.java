package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.aggregation.Aggregation;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationOrder;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationUsedStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aggregationorder.AggregationOrderDTO;
import uz.uzkassa.smartpossupply.dto.aggregationorder.AggregationOrderListDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.payload.AggregationOrderPayload;
import uz.uzkassa.smartpossupply.enums.AggregationOrderStatus;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.executor.AfterCommitExecutor;
import uz.uzkassa.smartpossupply.filters.AggregationOrderFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqAggregationOrderProducer;
import uz.uzkassa.smartpossupply.repository.AggregationOrderRepository;
import uz.uzkassa.smartpossupply.repository.AggregationRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AggregationOrderService extends BaseService {

    AggregationOrderRepository aggregationOrderRepository;
    AfterCommitExecutor afterCommitExecutor;
    RabbitMqAggregationOrderProducer rabbitMqAggregationOrderProducer;
    AggregationRepository aggregationRepository;
    NumberingContext numberingContext;

    @Transactional
    public void create(AggregationOrderDTO aggregationOrderDTO) {
       final Long companyId = getCurrentCompanyId();
       final Long userId = getCurrentUserId();

        String companyAggregationGcp = companyAggregationInfoRepository.findByCompanyId(companyId).map(CompanyAggregationInfo::getGcp).orElseThrow(validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_CODE_NOT_FOUND), "Отсуствует код организации"));
        if (StringUtils.isEmpty(companyAggregationGcp)) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_CODE_NOT_FOUND), "Отсуствует код организации").get();
        }
        if (aggregationOrderDTO.getQty() == null) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.QUANTITY_ERROR), "Количество отсуствует").get();
        }
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.AGGREGATION_ORDER, companyId);
        AggregationOrder aggregationOrder = new AggregationOrder();
        aggregationOrder.setOrderNumber(numberingMap.get(NUMBER));
        aggregationOrder.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        aggregationOrder.setPackageType(aggregationOrderDTO.getPackageType());
        aggregationOrder.setQty(aggregationOrderDTO.getQty());
        aggregationOrder.setCompanyId(companyId);
        aggregationOrder.setForEmployeeId(aggregationOrderDTO.getForEmployeeId());
        aggregationOrder.setStatus(AggregationOrderStatus.IN_PROGRESS);
        aggregationOrderRepository.save(aggregationOrder);
        afterCommitExecutor.execute(() ->
                rabbitMqAggregationOrderProducer.order(
                        new AggregationOrderPayload(
                                aggregationOrder.getId(),
                            companyId,
                                aggregationOrder.getQty(),
                                aggregationOrder.getPackageType().getCode(),
                                userId
                        )
                )
        );
    }

    @Transactional
    public void queueOrder(AggregationOrderPayload aggregationOrderPayload) {
        CompanyAggregationInfo companyAggregationInfo = companyAggregationInfoRepository.findByCompanyId(aggregationOrderPayload.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanyAggregationInfo.class.getSimpleName(), "companyId", aggregationOrderPayload.getCompanyId()));
        AggregationOrder aggregationOrder = aggregationOrderRepository.findById(aggregationOrderPayload.getAggregationOrderId()).orElseThrow(validationService.entityNotFoundExceptionThrow(AggregationOrder.class.getSimpleName(), "id", aggregationOrderPayload.getAggregationOrderId()));

        Integer qty = aggregationOrderPayload.getQty();
        try {
            List<Aggregation> aggregations = new ArrayList<>();
            int i = 0;
            while (i < qty) {
                Aggregation aggregation = generateAggregation(aggregationOrderPayload, companyAggregationInfo.getGcp());
                aggregations.add(aggregation);
                i++;

                if (aggregations.size() == 50) {
                    aggregationRepository.saveAll(aggregations);
                    aggregationRepository.flush();
                    aggregations.clear();
                }
            }
            if (!aggregations.isEmpty()) {
                aggregationRepository.saveAll(aggregations);
            }
            aggregationOrder.setStatus(AggregationOrderStatus.CREATED);
        } catch (Exception e) {
            log.error("Error on generating aggregations {}", aggregationOrder.getOrderNumber(), e);
            aggregationOrder.setStatus(AggregationOrderStatus.ERROR);
        }
        aggregationOrderRepository.save(aggregationOrder);
    }

    public Aggregation generateAggregation(AggregationOrderPayload aggregationOrderPayload, String gcp) {
        Long incrementNumber = aggregationRepository.nextVal(gcp);
        Aggregation aggregation = new Aggregation();
        aggregation.setCompanyId(aggregationOrderPayload.getCompanyId());
        aggregation.setAggregationOrderId(aggregationOrderPayload.getAggregationOrderId());
        aggregation.setCreatorId(aggregationOrderPayload.getCreatorId());
        aggregation.setSsccCode("00");
        aggregation.setPackageType(PackageType.valueOf(aggregationOrderPayload.getPackageType()));
        aggregation.setGcp(gcp);
        aggregation.setIncrementNumber(incrementNumber);
        aggregation.setSerialNumber(AGGREGATION_FORMAT.format(incrementNumber));

        String tempNumber = StringUtils.join(
                aggregation.getPackageType().getCodeNumber(),
                aggregation.getGcp(),
                aggregation.getSerialNumber()
        );
        String code = StringUtils.join(aggregation.getSsccCode(), tempNumber, MarkUtils.generateControlNumber(tempNumber));
        aggregation.setCode(code);
        aggregation.setOriginalCode(code);
        aggregation.setStatus(AggregationStatus.DRAFT);
        return aggregation;
    }

    public Page<AggregationOrderListDTO> getList(AggregationOrderFilter filter) {
        ResultList<AggregationOrder> resultList = aggregationOrderRepository.getResultList(filter);
        final List<Long> idList = resultList.getList().stream().map(AggregationOrder::getId).toList();
        final Map<Long, Integer> usedCountByAggregationOrderIdIn = CollectionUtils.isEmpty(idList) ? Collections.emptyMap() :
                aggregationRepository.getUsedCountByAggregationOrderIdIn(idList)
                        .stream().collect(Collectors.toMap(AggregationUsedStatisticsDTO::getAggregationOrderId, AggregationUsedStatisticsDTO::getUsed));
        List<AggregationOrderListDTO> result = resultList.getList().stream()
                .map(aggregationOrder -> {
                    AggregationOrderListDTO orderListDTO = new AggregationOrderListDTO();
                    orderListDTO.setId(aggregationOrder.getId());
                    orderListDTO.setCreatedDate(aggregationOrder.getCreatedDate());
                    orderListDTO.setOrderNumber(aggregationOrder.getOrderNumber());
                    orderListDTO.setPackageType(aggregationOrder.getPackageType());
                    orderListDTO.setStatus(aggregationOrder.getStatus());
                    if (aggregationOrder.getForEmployee() != null) {
                        orderListDTO.setForEmployee(aggregationOrder.getForEmployee().toUserCommonDTO());
                    }
                    if (aggregationOrder.getCreator() != null) {
                        orderListDTO.setCreator(aggregationOrder.getCreator().toUserCommonDTO());
                    }
                    orderListDTO.setQty(aggregationOrder.getQty());
                    orderListDTO.setUsed(usedCountByAggregationOrderIdIn.getOrDefault(aggregationOrder.getId(), 0));
                    orderListDTO.setUnused(aggregationOrder.getQty() - orderListDTO.getUsed());
                    return orderListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public AggregationOrderListDTO get(Long id) {
        Long usedCountByAggregationOrderId = aggregationRepository.countByOrderId(id, AggregationStatus.DRAFT);
        return aggregationOrderRepository
                .findById(id)
                .map(aggregationOrder -> {
                    AggregationOrderListDTO orderListDTO = new AggregationOrderListDTO();
                    orderListDTO.setId(aggregationOrder.getId());
                    orderListDTO.setCreatedDate(aggregationOrder.getCreatedDate());
                    orderListDTO.setOrderNumber(aggregationOrder.getOrderNumber());
                    orderListDTO.setPackageType(aggregationOrder.getPackageType());
                    orderListDTO.setStatus(aggregationOrder.getStatus());
                    if (aggregationOrder.getForEmployee() != null) {
                        orderListDTO.setForEmployee(aggregationOrder.getForEmployee().toUserCommonDTO());
                    }
                    if (aggregationOrder.getCreator() != null) {
                        orderListDTO.setCreator(aggregationOrder.getCreator().toUserCommonDTO());
                    }
                    orderListDTO.setQty(aggregationOrder.getQty());
                    orderListDTO.setUsed(usedCountByAggregationOrderId.intValue());
                    orderListDTO.setUnused(aggregationOrder.getQty() - orderListDTO.getUsed());
                    return orderListDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(AggregationOrder.class.getSimpleName(), "id", id));
    }

    public AggregationStatisticsDTO getStatistics(BaseFilter filter) {
        return aggregationOrderRepository.getStatistics(filter);
    }
}
