package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.billing.UsagePlan;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.billing.BillingSyncDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BillingSyncService extends BaseService {


    public void syncWithBilling(List<BillingSyncDTO> billingSyncDTOS) {
        Map<String, List<BillingSyncDTO>> groupedDto = billingSyncDTOS.stream().collect(Collectors.groupingBy(BillingSyncDTO::getTin));

        for (Map.Entry<String, List<BillingSyncDTO>> entry : groupedDto.entrySet()) {
            List<BillingSyncDTO> values = entry.getValue();
            Company company = companyRepository.findByTinAndDeletedIsFalse(entry.getKey()).orElse(null);
            if (company == null) {
                continue;
            }

            Hibernate.isInitialized(company.getPermissions());
            Map<String, LocalDateTime> companyPermissions = company.getPermissions();
            for (BillingSyncDTO billingSyncDTO : values) {
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
                companyPermissions.put(billingSyncDTO.getServiceType(), usagePLan.getEndDate());
            }
        }
    }
}
