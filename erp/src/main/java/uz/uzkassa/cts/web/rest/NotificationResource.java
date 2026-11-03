package uz.uzkassa.cts.web.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.launcher.LauncherNotification;
import uz.uzkassa.cts.dto.notifications.EtcNotificationRequestDto;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.service.CustomerOldService;
import uz.uzkassa.cts.service.NotificationOldService;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@Tag(name = "send notifications")
@Slf4j
public class NotificationResource {

    private final NotificationOldService notificationOldService;
    private final RabbitMqDeviceSwitcherProducer rabbitMqDeviceSwitcherProducer;
    private final CustomerOldService customerOldService;

    public NotificationResource(NotificationOldService notificationOldService,
                                RabbitMqDeviceSwitcherProducer rabbitMqDeviceSwitcherProducer,
                                CustomerOldService customerOldService) {
        this.notificationOldService = notificationOldService;
        this.rabbitMqDeviceSwitcherProducer = rabbitMqDeviceSwitcherProducer;
        this.customerOldService = customerOldService;
    }

    @Operation(summary ="notify customer about fee")
    @GetMapping("/about-fee/customer/{tin}")
    public ResponseEntity<Void> notifyAboutFee(@PathVariable String tin) {
        log.debug("REST request to notify about fee customer tin: {}", tin);
        notificationOldService.notifyAboutMonthlyServiceFee(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="notify all devices")
    @PostMapping("/notify")
    public ResponseEntity<Void> notifyDevices(@Valid @RequestBody LauncherNotification notification) {
        log.debug("REST request notification: {}", notification);
        notificationOldService.notifyAllDevices(notification);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="notify all devices")
    @PostMapping("/notify-all-by-sms")
    public ResponseEntity<Void> notifyAllBySms(@Valid @RequestBody EtcNotificationRequestDto notification) {
        log.debug("REST request notification: {}", notification.getText());
        log.info(">>>>>>>>>> Start Cron Notify all by sms <<<<<<<<<<<<");
//        rabbitMqDeviceSwitcherProducer.notifyAllBySms(new EtcRequestDto("998901234567", notification.getText()));
        long count = customerOldService.count();
        int start = 0;
        int limit = 20;
        while (start <= count) {
            List<String> phones = customerOldService.getPhoneNumberListInterval(start, limit);
            if (CollectionUtils.isNotEmpty(phones)) {
                for (String phone : phones){
                    EtcRequestDTO dto = new EtcRequestDTO(phone, notification.getText());
                    rabbitMqDeviceSwitcherProducer.notifyAllBySms(dto);
                }
            }
            start += limit;
        }
        log.info(">>>>>>>>>> End Cron Notify all by SMS <<<<<<<<<<<<");
        return ResponseEntity.ok().build();
    }
}
