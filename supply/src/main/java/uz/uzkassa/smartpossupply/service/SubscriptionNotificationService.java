package uz.uzkassa.smartpossupply.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyPermission;
import uz.uzkassa.smartpossupply.dto.payload.NotificationPayload;
import uz.uzkassa.smartpossupply.enums.TemplateType;
import uz.uzkassa.smartpossupply.enums.TemplateTypeAlias;
import uz.uzkassa.smartpossupply.repository.CompanyPermissionRepository;
import uz.uzkassa.smartpossupply.repository.CompanyRepository;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SubscriptionNotificationService {

    CompanyPermissionRepository permissionRepository;
    CompanyRepository companyRepository;
    NotificationService notificationService;

    public void process() {
        LocalDateTime fromDate = LocalDateTime.now();
        LocalDateTime toDate = fromDate.plusDays(3);
        List<CompanyPermission> permissions = permissionRepository.findByPeriod(fromDate, toDate);

        if (permissions.isEmpty()) return;

        Map<Long, List<CompanyPermission>> grouped = permissions.stream()
                .collect(Collectors.groupingBy(CompanyPermission::getCompanyId));

        Map<Long, Company> companies = companyRepository.findAllById(grouped.keySet())
                .stream()
                .collect(Collectors.toMap(Company::getId, Function.identity()));

        for (Map.Entry<Long, List<CompanyPermission>> entry : grouped.entrySet()) {

            Company company = companies.get(entry.getKey());
            if (company == null) continue;

            sendExpirationReminder(company, entry.getValue());
        }
    }

    private void sendExpirationReminder(Company company, List<CompanyPermission> permissions) {
        String servicesText = permissions.stream()
                .map(companyPermission -> String.format(
                        "%s | %s | %s",
                        Optional.ofNullable(companyPermission.getTitle()).orElse(""),
                        companyPermission.getEndDate() != null
                                ? companyPermission.getEndDate().format(
                                DateUtils.DATE_TIME_HYPHEN_WITHOUT_SECOND_FORMATTER)
                                : "",
                        Optional.ofNullable(companyPermission.getAmount()).orElse(BigDecimal.ZERO)
                ))
                .collect(Collectors.joining("\n"));

        Map<TemplateTypeAlias, Object> params = new HashMap<>();
        params.put(TemplateTypeAlias.SUBSCRIPTION_SERVICES, servicesText);

        NotificationPayload payload = new NotificationPayload(
                TemplateType.SUBSCRIPTION_PAYMENT_REMINDER,
                company.getId(),
                params);

        notificationService.send(payload);
    }
}