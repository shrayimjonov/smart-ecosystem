package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Bonus;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.bonus.BillingBonusWebhookDTO;
import uz.uzkassa.cts.dto.bonus.BonusListDTO;
import uz.uzkassa.cts.dto.bonus.BonusListDetailByEmployeeDTO;
import uz.uzkassa.cts.dto.bonus.BonusStatisticsDTO;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BonusRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.service.BonusService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class BonusServiceImpl extends BaseServiceImpl<Bonus, Long> implements BonusService {

    private final BonusRepository bonusRepository;
    private final CustomerRepository customerRepository;

    public BonusServiceImpl(BonusRepository bonusRepository, CustomerRepository customerRepository) {
        super(bonusRepository);
        this.bonusRepository = bonusRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Page<BonusListDTO> getList(BaseFilter filter) {
        ResultList<BonusListDTO> resultList = bonusRepository.getResultList(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<BonusListDetailByEmployeeDTO> getListByEmployee(Long employeeId, BaseFilter filter) {
        filter.setEmployeeId(employeeId);
        ResultList<BonusListDetailByEmployeeDTO> resultList = bonusRepository.getResultListByEmployee(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public BonusStatisticsDTO getStatistic(BaseFilter filter) {
        return bonusRepository.getStatistic(filter).orElse(new BonusStatisticsDTO());
    }

    @Override
    public void createBatch(List<BillingBonusWebhookDTO> bonusDTOs) {
        if (CollectionUtils.isNotEmpty(bonusDTOs)) {
            log.info("Bonus list DTO: {}", bonusDTOs);
            String queryBillingId = bonusDTOs.stream().map(bonusDTO -> "('" + (bonusDTO.getBillingId() != null ? bonusDTO.getBillingId().replace("'", "''") : null) + "')").collect(Collectors.joining(",\n"));
            List<String> billingIds = bonusRepository.getNotExistBillingIds(queryBillingId);
            List<String> pinfls = new ArrayList<>();
            List<String> tins = new ArrayList<>();
            bonusDTOs.forEach(
                    bonusDTO -> {
                        if (billingIds.contains(bonusDTO.getBillingId())) {
                            if (StringUtils.isNotEmpty(bonusDTO.getPinfl())) {
                                pinfls.add(bonusDTO.getPinfl());
                            }
                            tins.add(bonusDTO.getInn());
                        }
                    }
            );
            Map<String, Customer> customerMap = new HashMap<>();
            customerRepository.findAllByTinInOrOldTinIn(pinfls, tins)
                    .forEach(customer -> customerMap.put(customer.getTin(), customer));
            Map<Long, User> managerMap = new HashMap<>();
            userRepository
                    .findAllById(customerMap.values().stream().map(Customer::getManagerId).filter(Objects::nonNull).toList())
                    .forEach(user -> managerMap.put(user.getId(), user));
            List<Bonus> bonuses = new ArrayList<>();
            bonusDTOs.forEach(
                    bonusDTO -> {
                        if (billingIds.contains(bonusDTO.getBillingId())) {
                            Bonus bonus = new Bonus();
                            Customer customer = customerMap.get(bonusDTO.getInn());
                            if (customer != null) {
                                if (customer.getManagerId() != null) {
                                    bonus.setEmployeeId(customer.getManagerId());
                                    bonus.setCompanyId(
                                            managerMap.containsKey(customer.getManagerId()) ? managerMap.get(customer.getManagerId()).getCompanyId() : null
                                    );
                                }
                                bonus.setCustomerId(customer.getId());
                                bonus.setAmount(bonusDTO.getPrice());
                                bonus.setEndDate(bonusDTO.getEndDate());
                                bonus.setBillingId(bonusDTO.getBillingId());
                                bonus.setTariffType(bonusDTO.getTariff());
                                bonus.setAppType(CustomerAppType.SMARTPOS);
                                bonus.setStartDate(bonusDTO.getStartDate());
                                bonus.setPhoneNumber(bonusDTO.getPhoneNumber());
                                bonus.setBonusAmount(calculateBonus(bonusDTO.getPrice()));
                                bonuses.add(bonus);
                            }
                        }
                    }
            );
            if (CollectionUtils.isNotEmpty(bonuses)) {
                bonusRepository.saveAll(bonuses);
            }
        }
    }

    private BigDecimal calculateBonus(BigDecimal amount) {
        return amount.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(10));
    }
}
