package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.AslBelgiTokenDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyAggregationInfoDTO;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanyAggregationInfoService extends BaseService {

    AslBelgiClient aslBelgiClient;

    @Transactional
    public void createOrUpdate(CompanyAggregationInfoDTO companyAggregationInfoDTO) {
        Company company = companyAggregationInfoDTO.getCompanyId() != null ?
            companyRepository.findById(companyAggregationInfoDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyAggregationInfoDTO.getCompanyId()))
            : getCurrentCompany();
        CompanyAggregationInfo companyAggregationInfo = companyAggregationInfoRepository.findByCompanyId(company.getId()).orElse(new CompanyAggregationInfo());
        if (ProductGroup.Tobacco.equals(company.getProductGroup())) {
            if (companyAggregationInfoDTO.getFactoryId() == null) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ITEM_NECESSARY, new Object[]{"factoryId"})).get();
            }
            if (companyAggregationInfoDTO.getFactoryCountry() == null) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ITEM_NECESSARY, new Object[]{"factoryCountry"})).get();
            }
            companyAggregationInfo.setFactoryId(companyAggregationInfoDTO.getFactoryId());
            companyAggregationInfo.setFactoryCountry(companyAggregationInfoDTO.getFactoryCountry());
        }
        companyAggregationInfo.setCompanyId(company.getId());
        companyAggregationInfo.setContactPerson(companyAggregationInfoDTO.getContactPerson());
        if (StringUtils.isNotEmpty(companyAggregationInfoDTO.getGcp()) && !companyAggregationInfoDTO.getGcp().equals(companyAggregationInfo.getGcp())) {
            companyAggregationInfo.setGcp(companyAggregationInfoDTO.getGcp());
            companyAggregationInfoRepository.createSequence(companyAggregationInfoDTO.getGcp());
        }
        companyAggregationInfo.setOmsId(companyAggregationInfoDTO.getOmsId());
        companyAggregationInfo.setTuronToken(companyAggregationInfoDTO.getTuronToken());
        if (StringUtils.isNotEmpty(companyAggregationInfoDTO.getAslBelgiToken())) {
            companyAggregationInfo.setAslBelgiToken(companyAggregationInfoDTO.getAslBelgiToken());
        }
        if (companyAggregationInfoDTO.getAslBelgiTokenDate() != null) {
            companyAggregationInfo.setAslBelgiTokenDate(companyAggregationInfoDTO.getAslBelgiTokenDate());
        }

        companyAggregationInfoRepository.save(companyAggregationInfo);
    }

    @Transactional
    public void create(CompanyAggregationInfoDTO companyAggregationInfoDTO) {
        Company company = companyRepository.findById(companyAggregationInfoDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyAggregationInfoDTO.getCompanyId()));
        CompanyAggregationInfo companyAggregationInfo = companyAggregationInfoRepository.findByCompanyId(company.getId()).orElse(new CompanyAggregationInfo());
        companyAggregationInfo.setCompanyId(company.getId());
        companyAggregationInfo.setContactPerson(companyAggregationInfoDTO.getContactPerson());
        companyAggregationInfo.setGcp(companyAggregationInfoDTO.getGcp());
        companyAggregationInfo.setOmsId(companyAggregationInfoDTO.getOmsId());
        companyAggregationInfo.setTuronToken(companyAggregationInfoDTO.getTuronToken());
        if (companyAggregationInfoDTO.getAslBelgiToken() != null) {
            companyAggregationInfo.setAslBelgiToken(companyAggregationInfoDTO.getAslBelgiToken());
        }
        if (companyAggregationInfoDTO.getAslBelgiTokenDate() != null) {
            companyAggregationInfo.setAslBelgiTokenDate(companyAggregationInfoDTO.getAslBelgiTokenDate());
        }
        if (ProductGroup.Tobacco.equals(company.getProductGroup())) {
            if (companyAggregationInfoDTO.getFactoryId() == null) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ITEM_NECESSARY, new Object[]{"factoryId"})).get();
            }
            if (companyAggregationInfoDTO.getFactoryCountry() == null) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ITEM_NECESSARY, new Object[]{"factoryCountry"})).get();
            }
            companyAggregationInfo.setFactoryId(companyAggregationInfoDTO.getFactoryId());
            companyAggregationInfo.setFactoryCountry(companyAggregationInfoDTO.getFactoryCountry());
        }

        companyAggregationInfoRepository.save(companyAggregationInfo);
        companyAggregationInfoRepository.createSequence(companyAggregationInfoDTO.getGcp());
    }

    @Transactional
    public void update(Long id, CompanyAggregationInfoDTO companyAggregationInfoDTO) {
        companyAggregationInfoRepository.findById(id).map(companyAggregationInfo -> {

            if (ProductGroup.Tobacco.equals(companyAggregationInfo.getCompany().getProductGroup())) {
                if (companyAggregationInfoDTO.getFactoryId() == null) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ITEM_NECESSARY, new Object[]{"factoryId"})).get();
                }
                if (companyAggregationInfoDTO.getFactoryCountry() == null) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ITEM_NECESSARY, new Object[]{"factoryCountry"})).get();
                }
                companyAggregationInfo.setFactoryId(companyAggregationInfoDTO.getFactoryId());
                companyAggregationInfo.setFactoryCountry(companyAggregationInfoDTO.getFactoryCountry());
            }

            if (StringUtils.isNotEmpty(companyAggregationInfoDTO.getGcp()) && !companyAggregationInfoDTO.getGcp().equals(companyAggregationInfo.getGcp())) {
                companyAggregationInfoRepository.createSequence(companyAggregationInfoDTO.getGcp());
            }
            companyAggregationInfo.setContactPerson(companyAggregationInfoDTO.getContactPerson());
            companyAggregationInfo.setGcp(companyAggregationInfoDTO.getGcp());
            companyAggregationInfo.setOmsId(companyAggregationInfoDTO.getOmsId());
            companyAggregationInfo.setTuronToken(companyAggregationInfoDTO.getTuronToken());
            if (StringUtils.isNotEmpty(companyAggregationInfoDTO.getAslBelgiToken())) {
                companyAggregationInfo.setAslBelgiToken(companyAggregationInfoDTO.getAslBelgiToken());
            }
            if (companyAggregationInfoDTO.getAslBelgiTokenDate() != null) {
                companyAggregationInfo.setAslBelgiTokenDate(companyAggregationInfoDTO.getAslBelgiTokenDate());
            }
            companyAggregationInfoRepository.save(companyAggregationInfo);
            return companyAggregationInfo;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanyAggregationInfo.class.getSimpleName(), "id", id));
    }

    public CompanyAggregationInfoDTO get(Long companyId) {
        Long currentCompanyId = Optional.ofNullable(companyId).orElse(getCurrentCompanyId());
        if (currentCompanyId == null) {
            return new CompanyAggregationInfoDTO();
        }
        return companyAggregationInfoRepository.findByCompanyId(currentCompanyId).map(companyAggregationInfo -> {
            CompanyAggregationInfoDTO companyAggregationInfoDTO = new CompanyAggregationInfoDTO();
            companyAggregationInfoDTO.setId(companyAggregationInfo.getId());
            companyAggregationInfoDTO.setContactPerson(companyAggregationInfo.getContactPerson());
            companyAggregationInfoDTO.setGcp(companyAggregationInfo.getGcp());
            companyAggregationInfoDTO.setOmsId(companyAggregationInfo.getOmsId());
            companyAggregationInfoDTO.setTuronToken(companyAggregationInfo.getTuronToken());
            companyAggregationInfoDTO.setAslBelgiToken(companyAggregationInfo.getAslBelgiToken());
            companyAggregationInfoDTO.setAslBelgiTokenDate(companyAggregationInfo.getAslBelgiTokenDate());
            companyAggregationInfoDTO.setFactoryId(companyAggregationInfo.getFactoryId());
            companyAggregationInfoDTO.setFactoryCountry(companyAggregationInfo.getFactoryCountry());
            return companyAggregationInfoDTO;
        }).orElse(new CompanyAggregationInfoDTO());
    }

    @Transactional
    public void updateAslBelgiToken(String token, Long companyId) {
        CompanyAggregationInfo companyAggregationInfo = companyAggregationInfoRepository.findByCompanyId(Optional.ofNullable(companyId).orElseGet(BaseService::getCurrentCompanyId)).orElseGet(CompanyAggregationInfo::new);
        companyAggregationInfo.setAslBelgiToken(token);
        companyAggregationInfo.setAslBelgiTokenDate(LocalDateTime.now());
        companyAggregationInfoRepository.save(companyAggregationInfo);
    }

    @Transactional
    public void refreshAslBelgiTokens() {
        List<CompanyAggregationInfo> companyList = new ArrayList<>();
        List<CompanyAggregationInfo> companiesAggregationInfo = companyAggregationInfoRepository.findAllByAslBelgiTokenIsNotNullAndAslBelgiTokenDateIsAfter(LocalDateTime.now().minusHours(8));
        companiesAggregationInfo.forEach(aggregationInfo -> {
            try {
                String token = aslBelgiClient.refresh(aggregationInfo.getAslBelgiToken(), aggregationInfo.getCompany().getTin());
                aggregationInfo.setAslBelgiToken(token);
                aggregationInfo.setAslBelgiTokenDate(LocalDateTime.now());
                companyList.add(aggregationInfo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String url = applicationProperties.getAslBelgiConfig().getUrl() + "auth/refresh";
                StringBuilder message = new StringBuilder();
                message.append("REQUEST TO GET REFRESH TOKEN: \n");
                message.append(url).append("\n");
                message.append("TIN: ").append(aggregationInfo.getCompany().getTin()).append("\n");
                message.append("TOKEN: ").append(aggregationInfo.getAslBelgiToken()).append("\n");
                message.append("TOKEN DATE: ").append(aggregationInfo.getAslBelgiTokenDate()).append("\n");
                message.append("MESSAGE: ").append(e.getMessage()).append("\n");
                log.error(message.toString());
            }
        });
        if (!CollectionUtils.isEmpty(companyList)) {
            companyAggregationInfoRepository.saveAll(companyList);
        }
    }

    @Transactional
    public void refreshToken(String tin) {
        Company company = tin != null ? companyRepository.findByTinAndDeletedIsFalse(tin).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", tin)) : getCurrentCompany();
        companyAggregationInfoRepository.findByCompanyId(company.getId()).ifPresent(companyAggregationInfo -> {
            try {
                String token = aslBelgiClient.refresh(companyAggregationInfo.getAslBelgiToken(), companyAggregationInfo.getCompany().getTin());
                companyAggregationInfo.setAslBelgiToken(token);
                companyAggregationInfo.setAslBelgiTokenDate(LocalDateTime.now());
                companyAggregationInfoRepository.save(companyAggregationInfo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                StringBuilder message = new StringBuilder();
                message.append("REQUEST TO GET REFRESH TOKEN: \n");
                message.append(applicationProperties.getAslBelgiConfig().getUrl()).append("auth/refresh").append("\n");
                message.append("TIN: ").append(companyAggregationInfo.getCompany().getTin()).append("\n");
                message.append("TOKEN: ").append(companyAggregationInfo.getAslBelgiToken()).append("\n");
                message.append("TOKEN DATE: ").append(companyAggregationInfo.getAslBelgiTokenDate()).append("\n");
                message.append("MESSAGE: ").append(e.getMessage()).append("\n");
                log.error(message.toString());
            }
        });
    }

    @Transactional
    public void updateToken(AslBelgiTokenDTO aslBelgiTokenDTO) {
        companyAggregationInfoRepository.findByCompanyId(companyRepository.findByTinAndDeletedIsFalse(aslBelgiTokenDTO.getTin()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", aslBelgiTokenDTO.getTin())).getId()).map(companyAggregationInfo -> {
            companyAggregationInfo.setAslBelgiToken(aslBelgiTokenDTO.getToken());
            companyAggregationInfo.setAslBelgiTokenDate(LocalDateTime.now());
            companyAggregationInfoRepository.save(companyAggregationInfo);
            return companyAggregationInfo;
        }).orElseThrow(() -> validationService.notFoundExceptionThrow(localizationService.localize(MessageConstants.ENTITY_NOT_FOUND, new Object[]{localizationService.localize(CompanyAggregationInfo.class.getSimpleName())})).get());
    }

    @Transactional
    public void refreshAslBelgiTokensForDev() {
        List<CompanyAggregationInfo> companyList = new ArrayList<>();
        Optional<CompanyAggregationInfo> companiesAggregationInfo = companyAggregationInfoRepository.findAllByCompanyTinAndAslBelgiTokenIsNotNullAndAslBelgiTokenDateIsAfter("306351564", LocalDateTime.now().minusHours(8));
        companiesAggregationInfo.ifPresent(aggregationInfo -> {
            try {
                String token = aslBelgiClient.refresh(aggregationInfo.getAslBelgiToken(), aggregationInfo.getCompany().getTin());
                aggregationInfo.setAslBelgiToken(token);
                aggregationInfo.setAslBelgiTokenDate(LocalDateTime.now());
                companyList.add(aggregationInfo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String url = applicationProperties.getAslBelgiConfig().getUrl() + "auth/refresh";
                StringBuilder message = new StringBuilder();
                message.append("REQUEST TO GET REFRESH TOKEN: \n");
                message.append(url).append("\n");
                message.append("TIN: ").append(aggregationInfo.getCompany().getTin()).append("\n");
                message.append("TOKEN: ").append(aggregationInfo.getAslBelgiToken()).append("\n");
                message.append("TOKEN DATE: ").append(aggregationInfo.getAslBelgiTokenDate()).append("\n");
                message.append("MESSAGE: ").append(e.getMessage()).append("\n");
                log.error(message.toString());
            }
        });
        if (!CollectionUtils.isEmpty(companyList)) {
            companyAggregationInfoRepository.saveAll(companyList);
        }
    }

    @Transactional
    public void createDefault(Company company) {
        CompanyAggregationInfo companyAggregationInfo = companyAggregationInfoRepository.findByCompanyId(company.getId()).orElse(new CompanyAggregationInfo());
        if (companyAggregationInfo.getId() == null) {
            companyAggregationInfo.setCompanyId(company.getId());
            companyAggregationInfoRepository.save(companyAggregationInfo);
        }
    }
}
