package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyPermission;
import uz.uzkassa.smartpossupply.dto.billingpermission.BillingPermissionDTO;
import uz.uzkassa.smartpossupply.dto.billingpermission.ChildCompanyInfoDTO;
import uz.uzkassa.smartpossupply.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BillingSyncService extends BaseService {
    TerminalService terminalService;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void syncWithBilling(List<BillingPermissionDTO> billingPermissionDTOS) {
        log.info("Billing permissions: {}", billingPermissionDTOS);
        Map<String, List<BillingPermissionDTO>> groupedDto = billingPermissionDTOS.stream().collect(Collectors.groupingBy(billingSyncDTO -> {
            if (!StringUtils.isEmpty(billingSyncDTO.getNoteFromExternalSystem())) {
                try {
                    ChildCompanyInfoDTO childCompanyInfoDTO = objectMapper.readValue(billingSyncDTO.getNoteFromExternalSystem(), ChildCompanyInfoDTO.class);
                    return childCompanyInfoDTO.getTin() == null ? billingSyncDTO.getTin() : childCompanyInfoDTO.getTin();
                } catch (Exception e) {
                    log.error("BillingPermissionRequestDto NoteFromExternalSystem response convert to JSON error: {}", e.getMessage());
                }
            }
            return billingSyncDTO.getTin();
        }));

        // Collect terminal updates here
        List<TerminalUpdate> terminalUpdates = new ArrayList<>();

        for (Map.Entry<String, List<BillingPermissionDTO>> entry : groupedDto.entrySet()) {
            List<BillingPermissionDTO> values = entry.getValue();
            Company company = companyRepository.findFirstByTinAndDeletedIsFalse(entry.getKey()).orElse(null);
            if (company == null) {
                continue;
            }

            Hibernate.isInitialized(company.getPermissions());
            Map<String, CompanyPermission> existingPermissions = company.getPermissions().stream()
                    .collect(Collectors.toMap(CompanyPermission::getServiceType, p -> p));

            for (BillingPermissionDTO billingSyncDTO : values) {
                UsagePlan usagePLan = usagePlanRepository
                        .findFirstByCompanyIdAndServiceType(company.getId(), billingSyncDTO.getServiceType())
                        .orElse(new UsagePlan());
                if (billingSyncDTO.getUnitCount() != 0) {
                    usagePLan.setQty(usagePLan.getQty() + billingSyncDTO.getUnitCount());
                }
                usagePLan.setStartDate(billingSyncDTO.getStartDate());
                usagePLan.setEndDate(
                        (billingSyncDTO.getEndDate() != null) ? billingSyncDTO.getEndDate() : LocalDateTime.now().plusYears(3)
                );
                usagePLan.setServiceType(billingSyncDTO.getServiceType());
                usagePLan.setCompanyId(company.getId());
                company.addUsagePLan(usagePLan);

                CompanyPermission permission = existingPermissions.get(billingSyncDTO.getServiceType());

                if (permission == null) {
                    // Create new permission
                    permission = new CompanyPermission();
                    permission.setCompanyId(company.getId());
                    permission.setServiceType(billingSyncDTO.getServiceType());
                    company.addPermission(permission); // add to Set
                    existingPermissions.put(billingSyncDTO.getServiceType(), permission);
                }

                // Update fields (both new and existing)
                permission.setStartDate(billingSyncDTO.getStartDate());
                permission.setEndDate(usagePLan.getEndDate());
                permission.setUnitCount(billingSyncDTO.getUnitCount());
                permission.setAmount(billingSyncDTO.getAmount());
                permission.setAgreementId(billingSyncDTO.getAgreementId());
                if (billingSyncDTO.getXizmat() != null) {
                    permission.setXizmatId(billingSyncDTO.getXizmat().getId());
                    permission.setTitle(billingSyncDTO.getXizmat().getTitle());
                    permission.setTitleUz(billingSyncDTO.getXizmat().getTitleUz());
                    permission.setXizmatType(billingSyncDTO.getXizmat().getXizmatType());
                }
                if (Role.ONLINE_KKM.getCode().equals(billingSyncDTO.getCalculatePer())) {
                    try {
                        List<String> serialNumbers = objectMapper.readValue(billingSyncDTO.getNoteFromExternalSystem(),
                                new TypeReference<List<String>>() {});
                        terminalUpdates.add(new TerminalUpdate(permission, serialNumbers));
                    } catch (Exception e) {
                        log.error("NoteFromExternalSystem convert to ActiveOnlineKKMDTO error: {}", e.getMessage());
                    }
                }
            }
        }

        entityManager.flush();
        for (TerminalUpdate update : terminalUpdates) {
            try {
                terminalService.setPermissions(update.permission(), update.serialNumbers());
            } catch (Exception e) {
                log.error("Terminal permission update error: {}", e.getMessage());
            }
        }
    }

    private record TerminalUpdate(CompanyPermission permission, List<String> serialNumbers) {}
}
