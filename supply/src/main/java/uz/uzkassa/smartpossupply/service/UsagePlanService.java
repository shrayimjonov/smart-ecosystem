package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.CountableServiceTypes;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UsagePlanService extends BaseService {

    @Transactional
    public void createDefault(Company company) {
        UsagePlan usagePlan = new UsagePlan();
        usagePlan.setCompanyId(company.getId());
        usagePlan.setServiceType(CountableServiceTypes.INVOICE.getCode());
        usagePlan.setQty(0);
        usagePlan.setStartDate(LocalDateTime.now().with(LocalTime.MIN));
        usagePlan.setEndDate(LocalDateTime.now().plusYears(3).with(LocalTime.MAX));
        usagePlanRepository.save(usagePlan);
    }

    public UsagePlan getUsagePlan(String serviceType) {
        return usagePlanRepository
            .findFirstByCompanyIdAndServiceType(getCurrentCompanyId(), serviceType)
            .orElseGet(() -> {
                UsagePlan usagePlan = new UsagePlan();
                usagePlan.setCompanyId(getCurrentCompanyId());
                usagePlan.setQty(0);
                usagePlan.setStartDate(LocalDateTime.now().with(LocalTime.MIN));
                usagePlan.setEndDate(LocalDateTime.now().with(LocalTime.MAX));
                return usagePlan;
            });
    }
}
