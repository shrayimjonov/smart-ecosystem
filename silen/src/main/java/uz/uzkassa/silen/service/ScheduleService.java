package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.OrderProduct;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.OrganizationPermissions;
import uz.uzkassa.silen.dto.mq.NotificationPayload;
import uz.uzkassa.silen.enumeration.BufferStatus;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;
import uz.uzkassa.silen.enumeration.SerialNumberType;
import uz.uzkassa.silen.enumeration.ServiceType;
import uz.uzkassa.silen.repository.pg.ContractRepository;
import uz.uzkassa.silen.repository.pg.OrderProductRepository;
import uz.uzkassa.silen.repository.pg.OrganizationPermissionRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static uz.uzkassa.silen.enumeration.BufferStatus.PENDING;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleService extends BaseService {
    private final OrganizationPermissionRepository organizationPermissionRepository;
    private final ContractRepository contractRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderService orderService;
    private final CoreService coreService;
    private final AsilBelgiService asilBelgiService;

    @Profile({"dev", "prod"})
    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Tashkent") //30 minute
    public void refreshToken() {
        asilBelgiService.refreshTokens();
    }

    @Scheduled(cron = "1 1 23 31 12 *", zone = "Asia/Tashkent")
    public void expiredContracts() {
        log.info("Start checking expired contracts ");
        log.info("Date {}", LocalDateTime.now());
        contractRepository.changeExpiredContractsStatus();
    }


    //second, minute, hour, day of month, month, day(s) of week
    @Scheduled(cron = "0 1 1 * * ?", zone = "Asia/Tashkent")
    public void revokePrimaryAggregation() {
        final List<OrganizationPermissions> expiredPermissions = new LinkedList<>();
        organizationPermissionRepository.findAllByPermissionsKeyAndExpireDateBeforeAndCountGreaterThan(ServiceType.PRIMARY_AGGREGATION.name(), LocalDateTime.now(), 0)
                .forEach(permission -> {
                    permission.setCount(0);
                    expiredPermissions.add(permission);
                });

        organizationPermissionRepository.saveAll(expiredPermissions);
    }

    @Profile("prod")
    @Scheduled(cron = "0 0 1 * * MON")
    public void syncData() {
        coreService.syncRegions();
        coreService.syncDistricts();

        coreService.syncBanks("ru");
        coreService.syncBanks("uz");

        coreService.syncMeasures("ru");
        coreService.syncMeasures("uz");
    }

    /**
     * Check status of created orders from Turon.
     * This is scheduled to get fired every 1 minutes.
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    public void checkStatus() {
        List<OrderProduct> list = orderProductRepository.findAllByBufferStatusIsNullOrBufferStatus(PENDING);
        orderService.checkStatus(list);
    }

    /**
     * Update status of active orders from Turon.
     * This is scheduled to get fired every 1 hours.
     */
    @Scheduled(fixedDelay = 3600000, initialDelay = 15000)
    public void updateStatus() {
        List<OrderProduct> list = orderProductRepository.findAllByBufferStatusAndSerialNumberTypeNot(BufferStatus.ACTIVE, SerialNumberType.UPLOAD);
        orderService.checkStatus(list);
    }

/*    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Tashkent")
    public void syncPackagesInvoker() {
        log.info("Cron working at {}", LocalDateTime.now());
        try {
            productService.checkProductChanges(null);
        } catch (JsonProcessingException e) {
            log.error("Something went wrong on time syncing", e);
        }
    }*/

    @Scheduled(cron = "0 0 1 * * MON", zone = "Asia/Tashkent")
    public void checkAvailableCount() {
        organizationPermissionRepository.getSumOfByPermissionKeys(Arrays.asList(ServiceType.PRIMARY_AGGREGATION.name(), ServiceType.ADDITIONAL_AGGREGATION.name()), 100)
                .forEach(organizationPermissions -> {
                    NotificationPayload notify = new NotificationPayload();
                    notify.setUserId(null);
                    notify.setType(NotificationTemplateType.AGGREGATION_PACKAGE);
                    notify.setOrganizationId(organizationPermissions.getOrgId());
                    notify.addParams("count", String.valueOf(organizationPermissions.getQty()));
                    rabbitMqProducer.sendNotification(notify);
                });
        organizationPermissionRepository.getSumOfByPermissionKeys(Collections.singletonList(ServiceType.E_INVOICE.name()), 10)
                .forEach(organizationPermissions -> {
                    NotificationPayload notify = new NotificationPayload();
                    notify.setUserId(null);
                    notify.setType(NotificationTemplateType.INVOICE_PACKAGE);
                    notify.setOrganizationId(organizationPermissions.getOrgId());
                    notify.addParams("count", String.valueOf(organizationPermissions.getQty()));
                    rabbitMqProducer.sendNotification(notify);
                });
    }

    @Scheduled(cron = "0 0 11 * * ?", zone = "Asia/Tashkent")
    protected void checkTrialPeriod() {
        List<Organization> trialOrganizations = organizationRepository.findAllByDemoAllowedIsTrueAndDeletedIsFalseAndActiveIsTrue();
        log.debug("trialOrganizations list count {}", trialOrganizations.size());
        for (Organization organization : trialOrganizations) {
            log.info("Organization tin {} trialPeriod {}", organization.getTin(), organization.getTrialPeriod());
            if (organization.getTrialPeriod() == null) continue;
            if (organization.getTrialPeriod().atStartOfDay().isBefore(LocalDateTime.now())) {
                organization.setDemoAllowed(false);
                cacheService.evictCompany(organization.getTin());
                organizationRepository.save(organization);
            }
        }
    }
}
