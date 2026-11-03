package uz.uzkassa.smartpos.service;


import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.company.BillingPermission;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyPermission;
import uz.uzkassa.smartpos.dto.BillingPermissionRequestDto;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.mq.CompanyUnblockPayload;
import uz.uzkassa.smartpos.integration.billing.enums.XizmatType;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.company.BillingPermissionRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyPermissionRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingPermissionService {
    private final CompanyPermissionRepository companyPermissionRepository;
    private final CompanyRepository companyRepository;
    private final BillingPermissionRepository billingPermissionRepository;
    private final RabbitMqProducer rabbitMqProducer;

    public void syncWithBilling(List<BillingPermissionRequestDto> request) {
        log.info("BILLING: {}", request.toString());
        List<BillingPermission> permissions = new ArrayList<>();
        List<String> tins = new ArrayList<>();
        request.forEach(dto -> {
            if (StringUtils.isNotBlank(dto.getTin())) {
                tins.add(dto.getTin());
            }
            if (StringUtils.isNotBlank(dto.getPinfl())) {
                tins.add(dto.getPinfl());
            }
        });
        Map<String, Company> companyMap = companyRepository.findAllByInnIn(tins).stream().collect(Collectors.toMap(Company::getInn, Function.identity()));
        var companyIds = new HashSet<Long>();
        List<CompanyPermission> companyPermissions = new ArrayList<>();
        for (BillingPermissionRequestDto dto : request) {
            Company company = companyMap.getOrDefault(dto.getTin(), companyMap.getOrDefault(dto.getPinfl(), null));
            if (company == null) {
                permissions.add(new BillingPermission(null, dto));
                continue;
            }
            CompanyPermission companyPermission = companyPermissionRepository.findAllByCompanyId(company.getId()).stream()
                    .filter(p -> p.getServiceType().equals(dto.getServiceType()))
                    .findFirst()
                    .map(p -> {
                        p.setEndDate(Optional.ofNullable(dto.getEndDate()).orElse(LocalDateTime.now()));
                        return p;
                    })
                    .orElseGet(() -> {
                        CompanyPermission permission = new CompanyPermission();
                        permission.setServiceType(dto.getServiceType());
                        permission.setCompanyId(company.getId());
                        permission.setEndDate(Optional.ofNullable(dto.getEndDate()).orElse(LocalDateTime.now()));
                        permission.setXizmatType(dto.getXizmatType());
                        companyPermissions.add(permission);
                        return permission;
                    });
            companyPermissions.add(companyPermission);
            permissions.add(new BillingPermission(company.getId(), dto));
            if (Sets.newHashSet(ServiceType.INVOICE, ServiceType.ADDITIONAL_INVOICE, ServiceType.SMS, ServiceType.ADDITIONAL_SMS).contains(dto.getServiceType())) {
                if (Arrays.asList(ServiceType.INVOICE, ServiceType.SMS).contains(dto.getServiceType())) {
                    if (company.getBalances().getOrDefault(dto.getServiceType(), 0) >= 0) {
                        company.updateBalance(dto.getServiceType(), -company.getBalances().getOrDefault(dto.getServiceType(), 0) + dto.getUnitCount());
                    } else {
                        company.updateBalance(dto.getServiceType(), dto.getUnitCount());
                    }
                } else {
                    company.updateBalance(dto.getServiceType(), dto.getUnitCount());
                }
            }
            if (dto.getXizmatType() == XizmatType.GROUPED) {
                companyIds.add(company.getId());
            }
        }
        if (!companyMap.isEmpty()) {
            companyRepository.saveAll(companyMap.values());
        }
        if (CollectionUtils.isNotEmpty(companyPermissions)) {
            companyPermissionRepository.saveAll(companyPermissions);
        }
        billingPermissionRepository.saveAll(permissions);
        if (CollectionUtils.isNotEmpty(companyIds)) {
            rabbitMqProducer.sendWithDelayTime(new CompanyUnblockPayload(companyIds), MqConstants.UNBLOCK_TERMINALS_QUEUE, 60_000L);
        }
    }
}
