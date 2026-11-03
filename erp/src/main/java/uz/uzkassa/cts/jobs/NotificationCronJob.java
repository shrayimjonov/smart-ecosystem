package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.base.LocalizedMessageDTO;
import uz.uzkassa.cts.dto.notification.NotificationDTO;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.service.CustomerOldService;
import uz.uzkassa.cts.service.EmployeeService;
import uz.uzkassa.cts.service.NotificationService;
import uz.uzkassa.cts.service.TemplateService;
import uz.uzkassa.cts.utils.DateUtil;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class NotificationCronJob {

    RabbitMqDeviceSwitcherProducer rabbitMqProducer;
    CustomerOldService customerOldService;
    EmployeeService employeeService;
    NotificationService notificationService;
    TemplateService templateService;

    @Profile("prod")
    @Scheduled(cron = "0 0 9 * * ?")  // At 09:00 AM
    public void birthday() {
        employeeService.getTodayBirthdays().forEach(user -> {
            LocalizedMessageDTO localizedMessageDTO = templateService.getBirthdayGreeting(user);
            notificationService.create(NotificationDTO
                    .builder()
                    .messageRu(localizedMessageDTO.getMessageRu())
                    .messageUzLatin(localizedMessageDTO.getMessageUzLatin())
                    .messageUzCyrillic(localizedMessageDTO.getMessageUzCyrillic())
                    .companyId(user.getCompanyId())
                    .build());
        });
    }

    @Scheduled(cron = "0 0 4 ? * *")
    @Profile("prod")
    public void notifyAboutMonthlyServiceFee() {
        log.info(">>>>>>>>>> Start Cron Notify About Monthly Service Fee <<<<<<<<<<<<");
        LocalDate minDate5 = DateUtil.getLastNDayInAMonth(5);
        LocalDate minDate3 = DateUtil.getLastNDayInAMonth(3);
        LocalDate minDate1 = DateUtil.getLastNDayInAMonth(1);

        if (LocalDate.now().equals(minDate5) || LocalDate.now().equals(minDate3) || LocalDate.now().equals(minDate1)) {
            long count = customerOldService.count();
            int start = 0;
            int limit = 20;
            while (start <= count) {
                List<String> tins = customerOldService.getTinListInterval(start, limit);
                if (CollectionUtils.isNotEmpty(tins)) {
                    tins.forEach(rabbitMqProducer::notifyAboutMonthlyServiceFee);
                }
                start += limit;
            }
        }
        log.info(">>>>>>>>>> End Cron Notify About Monthly Service Fee <<<<<<<<<<<<");
    }
}
