package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.repository.CompanyRepository;
import uz.uzkassa.smartpossupply.service.CompanyService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.05.2024
 */
@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Profile("prod")
public class CompanyCronJob {

    CompanyService companyService;
    CompanyRepository companyRepository;

    //Fire every day at 00:01
    @Scheduled(cron = "0 1 0 * * ?")
    public void turnOffFreeTrial() {
        log.info("=======================FREE TRIAL CHECK CRON JOB STARTED==============================");
        CompanyFilter filter = new CompanyFilter();
        filter.setFreeTrial(true);
        filter.setStatus(CompanyStatus.ACTIVE.getCode());
        filter.setSize(1000);
        companyService.turnOffFreeTrial(companyRepository.getResultList(filter).getList());
    }
}