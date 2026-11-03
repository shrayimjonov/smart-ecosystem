package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.freeperiodhistory.response.FreePeriodHistoryResponseDto;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.FreePeriodHistoryMapper;
import uz.uzkassa.cts.repository.FreePeriodHistoryRepository;
import uz.uzkassa.cts.service.FreePeriodHistoryService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
public class FreePeriodHistoryServiceImpl extends BaseServiceImpl<FreePeriodHistory, Long> implements FreePeriodHistoryService {

    private final FreePeriodHistoryRepository repository;
    private final FreePeriodHistoryMapper freePeriodHistoryMapper;

    public FreePeriodHistoryServiceImpl(FreePeriodHistoryRepository repository, FreePeriodHistoryMapper freePeriodHistoryMapper) {
        super(repository);
        this.repository = repository;
        this.freePeriodHistoryMapper = freePeriodHistoryMapper;
    }

    @Override
    public Page<FreePeriodHistoryResponseDto> list(BaseFilter filter) {
        Specification<FreePeriodHistory> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<FreePeriodHistory, CustomerBranchDevice> customerBranchDevicePath = root.join(FreePeriodHistory_.CUSTOMER_BRANCH_DEVICE, JoinType.INNER);
            Path<Device> devicePath = customerBranchDevicePath.join(CustomerBranchDevice_.DEVICE, JoinType.INNER);
            Path<Customer> customer = root.join(FreePeriodHistory_.CUSTOMER, JoinType.INNER);
            ArrayList<Predicate> list = new ArrayList<>();

            if (StringUtils.isNotEmpty(filter.getSearch())) {

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customer.get(Customer_.NAME)), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customer.get(Customer_.COMPANY_NAME)), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(customer.get(Customer_.TIN)), filter.getSearchForQuery());
                Predicate serialNumberKKMPred = criteriaBuilder.like(criteriaBuilder.lower(devicePath.get(Device_.SERIAL_NUMBER)), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred, serialNumberKKMPred));
            }
            if (filter.getCreatorId() != null) {
                list.add(criteriaBuilder.equal(root.get(FreePeriodHistory_.CREATOR_ID), filter.getCreatorId()));
            }
            if (filter.getStatus() != null) {
                list.add(criteriaBuilder.equal(customerBranchDevicePath.get(CustomerBranchDevice_.STATUS), DeviceStatus.valueOf(filter.getStatus())));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(FreePeriodHistory_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<FreePeriodHistory> freePeriodHistories = repository.findAll(specification, filter.getOrderedPageable());

        return freePeriodHistories.map(freePeriodHistoryMapper::toDto);
    }
}
