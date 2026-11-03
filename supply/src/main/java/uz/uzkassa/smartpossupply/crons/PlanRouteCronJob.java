package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqPlanRouteProducer;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteTemplateRepository;
import uz.uzkassa.smartpossupply.service.PlanRouteService;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.01.2023 17:23
 */
@Component
@Profile("prod")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PlanRouteCronJob {

    PlanRouteTemplateRepository planRouteTemplateRepository;
    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;
    RabbitMqPlanRouteProducer rabbitMqPlanRouteProducer;
    PlanRouteService planRouteService;

    //Fire every Monday at midnight - 2am
    @Scheduled(cron = "0 0 2 * * MON", zone = "Asia/Tashkent")
    public void createPlanRouteByTemplate() {
        log.info("Plan route create cron job started: {}", LocalDate.now());
        planRouteTemplateRepository.getIdsByStatus(CommonStatus.ACTIVE, CompanyStatus.ACTIVE).forEach(id -> rabbitMqPlanRouteProducer.create(new Payload(id)));
    }

    //Fire at every day at 01:01 am
    @Scheduled(cron = "0 1 1 * * * ", zone = "Asia/Tashkent")
    public void changePlanRouteCustomerBranchStatus() {
        log.info("Plan route customer branch update status cron job started: {}", LocalDate.now());
        planRouteCustomerBranchRepository.updateStatus(PlanRouteCustomerBranchStatus.NOT_VISITED_YET, PlanRouteCustomerBranchStatus.NOT_VISITED, LocalDate.now());
    }

    //Fire at every week on MONDAY at 00:00 am
    @Scheduled(cron = "0 0 0 ? * MON", zone = "Asia/Tashkent")
    public void changePlanRouteStatus() {
        log.info("Plan route update status cron job started: {}", LocalDate.now());
        planRouteService.updateStatus();
    }
}
