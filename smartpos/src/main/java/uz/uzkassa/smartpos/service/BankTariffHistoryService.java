package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.company.BankTariffHistory;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.BankTariffHistoryDTO;
import uz.uzkassa.smartpos.repository.postgres.BankTariffHistoryRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankTariffHistoryService extends BaseService {
    private final BankTariffHistoryRepository repository;
    private final CompanyRepository companyRepository;


    @Transactional
    public void saveBankTariff(BankTariffHistoryDTO dto) {
        log.info("BankTariffHistoryDto: {}", dto);
        Optional<Company> companyOptional = companyRepository.findFirstByCtoId(dto.getCustomerId());
        if (companyOptional.isEmpty()) {
            companyOptional = companyRepository.findByInnOrOldInn(dto.getTin(), dto.getOldTin());
        }
        Company company = companyOptional.orElseThrow(notFound(localizationService.getMessage("company")));
        var entity = new BankTariffHistory();
        entity.setCompanyId(company.getId());
        entity.setBankType(dto.getBankType());
        entity.setCreatedDate(dto.getDate() != null ? dto.getDate() : LocalDateTime.now());
        entity.setHumoPercentage(dto.getHumoPercentage());
        entity.setUzcardPercentage(dto.getUzcardPercentage());
        entity.setVisaPercentage(dto.getVisaPercentage());
        entity.setVisaDomesticPercentage(dto.getVisaDomesticPercentage());
        if (dto.getXizmat() != null) {
            entity.setXizmatId(dto.getXizmat().getId());
            entity.setXizmatName(dto.getXizmat().getName());
        }
        repository.save(entity);
    }
}
