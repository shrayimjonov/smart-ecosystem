package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.domain.ActivityType;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.OneCIntegration;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.address.District;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.dto.activitytype.ActivityTypeDTO;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.auth.SignDTO;
import uz.uzkassa.smartpossupply.dto.bank.BankDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.company.BatchCompanyManagerDTO;
import uz.uzkassa.smartpossupply.dto.company.CheckTinResultDTO;
import uz.uzkassa.smartpossupply.dto.company.ChildCompanyDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyDetailDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyFreeTrialDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyListDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyManagerDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyMonthlyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyOneCIntegrationDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.company.ParentCompanyDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDetailDTO;
import uz.uzkassa.smartpossupply.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpossupply.dto.factura.CompanyCheckDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.integration.agreement.AgreementManagerDTO;
import uz.uzkassa.smartpossupply.dto.integration.agreement.AgreementManagerRequestDTO;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.UpdateCompanyEquipmentCountPayload;
import uz.uzkassa.smartpossupply.dto.sync.company.ActivityTypeSyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.company.AddressSyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceCompanySyncDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.SourceType;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.AgreementFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.filters.LedgerDetailAppTypeFilter;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.filters.UserFilter;
import uz.uzkassa.smartpossupply.integration.EimzoClient;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.integration.SoliqClient;
import uz.uzkassa.smartpossupply.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpossupply.mappers.FileUploadMapper;
import uz.uzkassa.smartpossupply.mappers.UserMapper;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCompanySyncProducer;
import uz.uzkassa.smartpossupply.repository.ActivityTypeRepository;
import uz.uzkassa.smartpossupply.repository.BankRepository;
import uz.uzkassa.smartpossupply.repository.DistrictRepository;
import uz.uzkassa.smartpossupply.repository.FacturaRepository;
import uz.uzkassa.smartpossupply.repository.GenericSettingsRepository;
import uz.uzkassa.smartpossupply.repository.OneCIntegrationRepository;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.RegionRepository;
import uz.uzkassa.smartpossupply.repository.StockBaseRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.repository.WaybillRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.FacturaException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import static lombok.AccessLevel.PRIVATE;
import static uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType.ENABLE_DISABLE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CompanyService extends BaseService {

    UserMapper userMapper;
    FileUploadMapper fileUploadMapper;
    FileUploadService fileUploadService;
    CategoryService categoryService;
    WarehouseRepository warehouseRepository;
    FacturaRestClient facturaRestClient;
    SoliqClient soliqClient;
    StockBaseRepository stockBaseRepository;
    FacturaRepository facturaRepository;
    WaybillRepository waybillRepository;
    OrderRepository orderRepository;
    BankRepository bankRepository;
    BillingServiceFeignClient billingServiceFeignClient;
    AccountService accountService;
    BranchService branchService;
    WarehouseService warehouseService;
    CompanyAggregationInfoService companyAggregationInfoService;
    UsagePlanService usagePlanService;
    ActivityTypeRepository activityTypeRepository;
    EimzoClient eimzoClient;
    OneCIntegrationRepository oneCIntegrationRepository;
    GenericSettingsRepository genericSettingsRepository;
    CacheService cacheService;
    RabbitMqCompanySyncProducer rabbitMqCompanySyncProducer;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    ValidationContext validationContext;

    @Transactional
    public Long create(CompanyDTO companyDTO) {
        validationContext.validate(companyDTO);
        Company company = companyRepository.findFirstByTinAndDeletedIsFalse(companyDTO.getTin()).orElse(null);
        if (company != null && company.getOwnerId() != null) {
            throw validationService.conflictExceptionThrow(localizationService.localize(
                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                new Object[]{
                    localizationService.localize(Company.class.getSimpleName()),
                    localizationService.localize(MessageConstants.INN),
                    company.getTin(),
                }
            )).get();
        }

        if (company == null && !companyDTO.isForeign()) {
            CompanyCheckDTO companyCheckDTO = soliqClient.checkTin(companyDTO.getTin());
            if (StringUtils.isEmpty(companyCheckDTO.getTin()) && StringUtils.isEmpty(companyCheckDTO.getPersonalNum())) {
                throw validationService.notFoundExceptionThrow("Компании с таким ИНН ".concat(companyDTO.getTin()).concat(" не существует")).get();
            }
        }

        String login = Optional.ofNullable(companyDTO.getLogin()).orElse(getCurrentUserLogin());
        User user = userRepository.findFirstByLogin(login).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", login));
        if (company != null && user.getCompanyId() != null && !company.getId().equals(user.getCompanyId())) {
            throw validationService.conflictExceptionThrow("Пользователь уже зарегистрирован").get();
        }
        if (company == null) {
            company = new Company();
        }
        SourceType sourceType = switch (user.getRole()) {
            case ROLE_MARKETPLACE_BUSINESS_OWNER -> SourceType.MARKETPLACE;
            case ROLE_BUSINESS_OWNER -> SourceType.SUPPLY;
            default -> throw validationService.conflictExceptionThrow(localizationService.localize(MessageConstants.NULL_VALUE_ERROR)).get();
        };

        company.setOwnerId(user.getId());
        company.setOwner(user);
        company.setTin(companyDTO.getTin());
        company.setName(companyDTO.getName());
        company.setBusinessType(companyDTO.getBusinessType());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setAllowInvoiceSync(true);
        company.setRegistrationDate(LocalDateTime.now());
        company.setProductGroup(companyDTO.getProductGroup());
        company.setCompanyType(companyDTO.getCompanyType());
        company.setDirector(companyDTO.getDirector());
        company.setAccountant(companyDTO.getAccountant());
        company.setSource(sourceType);

        if (companyDTO.getAddress() != null) {
            Address address = company.getAddress() != null ? company.getAddress() : new Address();
            company.setAddress(addressMapper.merge(companyDTO.getAddress(), address));
        }
        if (companyDTO.getActivityType() != null) {
            company.setActivityTypeId(companyDTO.getActivityType().getId());
        }
        companyRepository.save(company);
        cacheService.evictCompany(company.getTin());

        user.setCompanyId(company.getId());
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());

        //Create default branch
        Branch branch = branchService.createDefault(company, user);

        //Create default warehouse
        warehouseService.createDefault(branch);

        //Create default category
        categoryService.createDefault(branch);

        //Create default aggregation info
        companyAggregationInfoService.createDefault(company);

        //Create default usage plan
        usagePlanService.createDefault(company);

        //Create default accounts
        accountService.createDefault(company);

        //Sync catalogs from Tasnif
//        categoryService.syncFromTasnif(company, branch, user);

        //Create company settings
        setCompanySettings(company.getId());

        rabbitMqCompanySyncProducer.sync(new SyncPayload(company.getId(), AppType.ERP));
        return company.getId();
    }

    @Transactional
    public Long update(Long id, CompanyDTO companyDTO) {
        Company company = companyRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));

        if (companyDTO.getTin() != null) {
            if (companyRepository.existsCompanyByTinAndIdNotAndDeletedIsFalse(companyDTO.getTin(), id)) {
                throw validationService.conflictExceptionThrow(localizationService.localize(
                    MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                    new Object[]{
                        localizationService.localize(Company.class.getSimpleName()),
                        localizationService.localize(MessageConstants.INN),
                        companyDTO.getTin(),
                    }
                )).get();
            }
            company.setTin(companyDTO.getTin());
        }
        if (company.getRegistrationDate() == null) {
            company.setRegistrationDate(LocalDateTime.now());
        }
        company.setName(companyDTO.getName());
        company.setBusinessType(companyDTO.getBusinessType());
        company.setProductGroup(companyDTO.getProductGroup());
        company.setCompanyType(companyDTO.getCompanyType());
        company.setDirector(companyDTO.getDirector());
        company.setAccountant(companyDTO.getAccountant());

        if (companyDTO.getAddress() != null) {
            Address address = addressMapper.toEntity(companyDTO.getAddress());
            if (companyDTO.getAddress().getRegionId() != null) {
                Region region = regionRepository.findById(companyDTO.getAddress().getRegionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Region.class.getSimpleName(), "id", companyDTO.getAddress().getRegionId()));
                address.setRegion(region);
            }
            if (companyDTO.getAddress().getDistrictId() != null) {
                District district = districtRepository.findById(companyDTO.getAddress().getDistrictId()).orElseThrow(validationService.entityNotFoundExceptionThrow(District.class.getSimpleName(), "id", companyDTO.getAddress().getDistrictId()));
                address.setDistrict(district);
            }
            company.setAddress(addressMapper.merge(companyDTO.getAddress(), address));
        }
        ActivityType activityType = activityTypeRepository.findById(companyDTO.getActivityType().getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(ActivityType.class.getSimpleName(), "id", companyDTO.getActivityType().getId()));
        company.setActivityType(activityType);
        company.setActivityTypeId(activityType.getId());
        companyRepository.save(company);
        cacheService.evictCompany(company.getTin());
        if (company.getMarketplaceId() != null) {
            var data = new SyncPayload(id, AppType.MARKETPLACE);
            log.info("Sending message to queue: {}", data);
            rabbitMqCompanySyncProducer.sync(data);
        }
        if (company.getErpId() != null) {
            rabbitMqCompanySyncProducer.sync(new SyncPayload(id, AppType.ERP));
        }
        return company.getId();
    }

    public CompanyDetailDTO get(Long id) {
        Company company = companyRepository.getDetail(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
        CompanyDetailDTO companyDetailDTO = new CompanyDetailDTO();
        companyDetailDTO.setId(company.getId());
        companyDetailDTO.setName(company.getName());
        companyDetailDTO.setTin(company.getTin());
        companyDetailDTO.setBusinessType(company.getBusinessType());
        companyDetailDTO.setProductGroup(company.getProductGroup());
        companyDetailDTO.setCompanyType(company.getCompanyType());
        companyDetailDTO.setStatus(company.getStatus());
        companyDetailDTO.setDirector(company.getDirector());
        companyDetailDTO.setAccountant(company.getAccountant());
        if (company.getActivityType() != null) {
            companyDetailDTO.setActivityType(
                new ActivityTypeDTO(
                    company.getActivityType().getId(),
                    company.getActivityType().getName(),
                    company.getActivityType().getParent()
                )
            );
        }
        companyDetailDTO.setAddress(addressMapper.toDetailDTO(company.getAddress()));

        if (company.getCompanyBank() != null) {
            BankDetailDTO bankDetailDTO = new BankDetailDTO();
            bankDetailDTO.setId(company.getCompanyBank().getId());
            bankDetailDTO.setName(company.getCompanyBank().getBank().getName());
            bankDetailDTO.setAccountNumber(company.getCompanyBank().getAccountNumber());
            bankDetailDTO.setOked(company.getCompanyBank().getOked());
            bankDetailDTO.setMfo(company.getCompanyBank().getBank().getCode());
            companyDetailDTO.setBank(bankDetailDTO);
        }

        if (company.getLogo() != null) {
            companyDetailDTO.setLogo(fileUploadMapper.toLogoDTO(company.getLogo()));
        }
        if (company.getBackgroundImage() != null) {
            companyDetailDTO.setBackgroundImage(company.getBackgroundImage().toFileDTO());
        }
        companyDetailDTO.setOwner(userMapper.toOwnerDTO(company.getOwner()));
        if (company.getManager() != null) {
            companyDetailDTO.setManager(company.getManager().toCommonDTO());
        }
        companyDetailDTO.setFreeTrial(company.isFreeTrial());
        companyDetailDTO.setTrialExpireDate(company.getTrialExpireDate());
        companyDetailDTO.setHasContract(company.isHasContract());
        companyDetailDTO.setActiveBranches(branchRepository.countByCompanyIdAndStatusAndDeletedIsFalse(id, CommonStatus.ACTIVE));
        return companyDetailDTO;
    }

    @Transactional
    public FileDTO uploadLogo(Long id, MultipartFile file) {
        return companyRepository.findById(id).map(company -> {
            if (company.getLogo() != null) {
                fileUploadService.delete(company.getLogo());
            }
            FileUpload logo = fileUploadService.fileUpload(file);
            company.setLogoId(logo.getId());
            company.setLogo(logo);
            companyRepository.save(company);
            cacheService.evictCompany(company.getTin());
            return fileUploadMapper.toLogoDTO(logo);
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void removeLogo(Long id) {
        companyRepository.findById(id).map(company -> {
            FileUpload logo = company.getLogo();
            if (logo != null) {
                company.setLogo(null);
                company.setLogoId(null);
                companyRepository.save(company);
                fileUploadService.delete(logo);
                cacheService.evictCompany(company.getTin());
            }
            return company;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    public CheckTinResultDTO checkTin(String tin) {
        CompanyCheckDTO companyCheckDTO = soliqClient.checkTin(tin);
        if (StringUtils.isEmpty(companyCheckDTO.getTin()) && StringUtils.isEmpty(companyCheckDTO.getPersonalNum())) {
            throw validationService.notFoundExceptionThrow("Компании с таким ИНН ".concat(tin).concat(" не существует")).get();
        }
        CheckTinResultDTO result = new CheckTinResultDTO();
        companyRepository.findByTinAndDeletedIsFalse(tin).ifPresent(company -> result.setId(company.getId()));
        result.setName(companyCheckDTO.getName());
        result.setTin(Optional.ofNullable(companyCheckDTO.getTin()).orElse(companyCheckDTO.getPersonalNum()));
        if (StringUtils.isNotBlank(companyCheckDTO.getMfo())) {
            bankRepository
                .findByMfo(companyCheckDTO.getMfo())
                .ifPresent(bank -> {
                    CompanyBankDetailDTO companyBankDetailDTO = new CompanyBankDetailDTO();
                    companyBankDetailDTO.setBank(bank.toLookUpDTO());
                    if (bank.getParentId() != null) {
                        companyBankDetailDTO.setParentBank(bank.getParent().toLookUpDTO());
                    } else {
                        companyBankDetailDTO.setParentBank(bank.toLookUpDTO());
                    }
                    companyBankDetailDTO.setAccountNumber(companyCheckDTO.getAccount());
                    companyBankDetailDTO.setOked(companyCheckDTO.getOked());
                    companyBankDetailDTO.setMain(true);
                    result.setBankDetail(companyBankDetailDTO);
                });
        }
        if (companyCheckDTO.getNs10Code() != null) {
            regionRepository
                .findByCode(companyCheckDTO.getNs10Code())
                .ifPresent(region -> {
                    AddressDetailDTO addressDetailDTO = new AddressDetailDTO();
                    addressDetailDTO.setRegion(region.toCommonDTO());
                    addressDetailDTO.setStreet(companyCheckDTO.getAddress());
                    if (companyCheckDTO.getNs11Code() != null) {
                        districtRepository
                            .findByRegionCodeAndCode(companyCheckDTO.getNs10Code(), companyCheckDTO.getNs11Code())
                            .ifPresent(district -> addressDetailDTO.setDistrict(district.toDTO()));
                    }
                    result.setAddress(addressDetailDTO);
                });
        }
        return result;
    }

    public Page<CompanyListDTO> getList(CompanyFilter filter) {
        ResultList<Company> resultList = this.getResultList(filter);
        List<CompanyListDTO> result = resultList
            .getList()
            .stream()
            .map(company -> {
                CompanyListDTO adminCompanyListDTO = new CompanyListDTO();
                adminCompanyListDTO.setId(company.getId());
                adminCompanyListDTO.setName(company.getName());
                adminCompanyListDTO.setTin(company.getTin());
                adminCompanyListDTO.setRegistrationDate(company.getRegistrationDate());
                adminCompanyListDTO.setOwner(userMapper.toOwnerDTO(company.getOwner()));
                adminCompanyListDTO.setStatus(company.getStatus());
                if (company.getManager() != null) {
                    adminCompanyListDTO.setManager(company.getManager().toCommonDTO());
                }
                adminCompanyListDTO.setXFileEnabled(company.isXFileEnabled());
                adminCompanyListDTO.setManagerAssignedDate(company.getManagerAssignedDate());
                if (company.getAddress() != null) {
                    if (company.getAddress().getRegionId() != null) {
                        adminCompanyListDTO.setRegion(company.getAddress().getRegion().toCommonDTO());
                    }
                    if (company.getAddress().getDistrictId() != null) {
                        adminCompanyListDTO.setDistrict(company.getAddress().getDistrict().toCommonDTO());
                    }
                }
                return adminCompanyListDTO;
            }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<CompanyBaseDTO> lookUp(CompanyFilter filter) {
        filter.setStatus(CompanyStatus.ACTIVE.getCode());
        return this.getResultList(filter)
            .getList()
            .stream()
            .map(Company::toCommonBaseDTO)
            .toList();
    }

    public ResultList<Company> getResultList(CompanyFilter filter) {
        if (isFromAdmin()) {
            if (filter.getSupervisorId() != null || filter.getManagerId() != null) {
                return companyRepository.getResultList(filter);
            } else {
                User currentUser = getCurrentUser();
                if (Role.ROLE_SUPERVISOR.equals(currentUser.getRole())) {
                    filter.setManagerIds(userRepository.getIdsBySupervisorId(currentUser.getId()));
                } else if (Role.ROLE_SUPPORT.equals(currentUser.getRole())) {
                    filter.setManagerId(currentUser.getId());
                }
            }
        }
        return companyRepository.getResultList(filter);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        companyRepository.findById(id).ifPresent(company -> {
            company.setDeleted(true);
            companyRepository.save(company);
            cacheService.evictCompany(company.getTin());
        });
    }

    @Transactional
    public void bindingProvider(SignDTO signDTO) {
        Company company = getCurrentCompany();
        Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(signDTO.getSign());
        if (pkcs7Dto.isSuccess()) {
            facturaRestClient.bindingProvider(pkcs7Dto.getPkcs7b64());
            company.setXFileEnabled(true);
            companyRepository.save(company);
            cacheService.evictCompany(company.getTin());
        } else {
            log.error("Provider binding error JSON: {}", pkcs7Dto);
            throw new FacturaException(pkcs7Dto.getMessage());
        }
    }

    public CompanyStatisticsDTO getStatistics(CompanyFilter filter) {
        CompanyStatisticsDTO result = new CompanyStatisticsDTO();

        result.setTotalBranch(branchRepository.countByCompanyIdAndDeletedIsFalse(filter.getCompanyId()));
        result.setTotalWarehouse(
            warehouseRepository.countByBranch_CompanyIdAndStatusIs(filter.getCompanyId(), CommonStatus.ACTIVE)
        );
        result.setTotalOrder(orderRepository.getTotalStatistics(filter.getCompanyId(), OrderStatus.COMPLETED));
        result.setTotalStockIncoming(stockBaseRepository.getTotalStatistics(filter.getCompanyId(), StockStatus.COMPLETED, TransferType.IN));
        result.setTotalStockOutgoing(stockBaseRepository.getTotalStatistics(filter.getCompanyId(), StockStatus.COMPLETED, TransferType.OUT));
        result.setTotalInvoice(
            facturaRepository.getTotalStatistics(filter.getCompanyId(), Arrays.asList(Status.CANCELLED, Status.REJECTED))
        );

        return result;
    }

    public CompanyMonthlyStatisticsDTO getMonthStatistics(BaseFilter filter) {
        return new CompanyMonthlyStatisticsDTO(facturaRepository.getMonthStatistics(filter), waybillRepository.getMonthStatistics(filter));
    }

    @Transactional
    public void assignManager(CompanyManagerDTO dto) {
        Company company = companyRepository
            .findById(dto.getCompanyId())
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", dto.getCompanyId()));
        User manager = userRepository
            .findById(dto.getManagerId())
            .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", dto.getManagerId()));
        company.setManagerId(dto.getManagerId());

        // send sync request to billing
        AgreementManagerRequestDTO requestPayload = new AgreementManagerRequestDTO();
        requestPayload.setTinPinfl(company.getTin());
        requestPayload.setManagerId(String.valueOf(manager.getId()));
        requestPayload.setManagerFirstName(manager.getFirstName());
        requestPayload.setManagerLastName(manager.getLastName());
        requestPayload.setManagerPatronymic(manager.getPatronymic());
        requestPayload.setManagerLogin(manager.getName());
        /*try {
            log.info("Billing assign manager: {}", objectMapper.writeValueAsString(requestPayload));
        } catch (JsonProcessingException e) {
            log.info("Write value error");
        }*/
        try {
            billingServiceFeignClient.updateAgreementManager(getBillingToken(), requestPayload);
        } catch (Exception e) {
            log.error(" Error message: {}", e.getMessage());
        }
        companyRepository.save(company);
        cacheService.evictCompany(company.getTin());
    }

    @Transactional
    public void batchAssignManager(BatchCompanyManagerDTO dto) {
        User manager = userRepository
            .findById(dto.getManagerId())
            .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", dto.getManagerId()));
        dto.getCompanyIds().forEach(companyId -> {
            Company company = companyRepository
                .findById(companyId)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
            company.setManagerId(manager.getId());

            // send sync request to billing
            AgreementManagerRequestDTO requestPayload = new AgreementManagerRequestDTO();
            requestPayload.setTinPinfl(company.getTin());
            requestPayload.setManagerId(String.valueOf(manager.getId()));
            requestPayload.setManagerFirstName(manager.getFirstName());
            requestPayload.setManagerLastName(manager.getLastName());
            requestPayload.setManagerPatronymic(manager.getPatronymic());
            requestPayload.setManagerLogin(manager.getName());
            /*try {
                log.info("Billing assign manager: {}", objectMapper.writeValueAsString(requestPayload));
            } catch (JsonProcessingException e) {
                log.info("Write value error");
            }*/
            try {
                billingServiceFeignClient.updateAgreementManager(getBillingToken(), requestPayload);
            } catch (Exception e) {
                log.error(" Error message: {}", e.getMessage());
            }
            companyRepository.save(company);
            cacheService.evictCompany(company.getTin());
        });
    }

    @Transactional
    public void removeManager(Long companyId) {
        companyRepository
            .findById(companyId)
            .map(company -> {
                company.setManagerId(null);
                companyRepository.save(company);
                cacheService.evictCompany(company.getTin());
                return company;
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
    }

    public AgreementManagerDTO getManagerAgreement(String tin) {
        return companyRepository
            .findFirstByTinAndDeletedIsFalse(tin)
            .map(company -> {
                if (company.getManager() == null) {
                    throw validationService.badRequestExceptionThrow("Для создания услуги необходимо назначить менеджера для компании: " + company.getTin()).get();
                }
                AgreementManagerDTO agreementManagerDTO = new AgreementManagerDTO();
                agreementManagerDTO.setSmartposCreatorId(String.valueOf(company.getManagerId()));
                agreementManagerDTO.setFirstName(company.getManager().getFirstName());
                agreementManagerDTO.setLastName(company.getManager().getLastName());
                agreementManagerDTO.setPatronymic(company.getManager().getPatronymic());
                agreementManagerDTO.setLogin(company.getManager().getLogin());
                return agreementManagerDTO;
            })
            .orElse(new AgreementManagerDTO());
    }

    private String getBillingToken() {
        return billingServiceFeignClient.getToken(
            applicationProperties.getBilling().getUsername(),
            applicationProperties.getBilling().getPassword()
        );
    }

    public Map<String, Integer> getCountableUsagePlans(String serviceType) {
        return getCountableUsagePlans(serviceType, null);
    }

    public Map<String, Integer> getCountableUsagePlans(String serviceType, String tin) {
        Company company;
        if (StringUtils.isNotEmpty(tin)) {
            company = companyRepository.findByTinAndDeletedIsFalse(tin).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", tin));
        } else if (getCurrentCompanyId() != null) {
            company = companyRepository.getReferenceById(getCurrentCompanyId());
        } else {
            throw validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName()).get();
        }
        if (StringUtils.isNotEmpty(serviceType)) {
            Map<String, Integer> countableUsagePlans = new HashMap<>();
            usagePlanRepository
                    .findFirstByCompanyIdAndServiceType(company.getId(), serviceType)
                    .ifPresent(usagePlan -> countableUsagePlans.put(usagePlan.getServiceType(), usagePlan.getQty()));
            return countableUsagePlans;
        }

        return usagePlanRepository.findAllByCompanyIdAndQtyIsNotNullAndServiceTypeIsNotNull(company.getId())
                .stream()
                .collect(Collectors.toMap(UsagePlan::getServiceType, UsagePlan::getQty));
    }

    @Transactional
    public Boolean switchFreeTrial(Long id, CompanyFreeTrialDTO freeTrialDTO) {
        if (freeTrialDTO.isFreeTrial() && freeTrialDTO.getTrialExpireDate() == null) {
            throw validationService.badRequestExceptionThrow("Если включена бесплатная пробная версия, необходимо указать дату окончания срока действия.").get();
        }
        if (freeTrialDTO.isFreeTrial() && LocalDateTime.now().isAfter(freeTrialDTO.getTrialExpireDate().with(LocalTime.MAX))) {
            throw validationService.badRequestExceptionThrow("Неправильная дата").get();
        }
        if (SecurityUtils.isCurrentUserInRole(Role.ROLE_SUPER_ADMIN.getCode()) || SecurityUtils.isCurrentUserInRole(Role.ROLE_ADMIN.getCode())) {
            return companyRepository.findById(id)
                .map(company -> {
                    company.setFreeTrial(freeTrialDTO.isFreeTrial());
                    if (company.isFreeTrial()) {
                        company.setAllowInvoiceSync(true);
                        company.setAllowCatalogSync(true);
                        company.setTrialExpireDate(freeTrialDTO.getTrialExpireDate());
                    }
                    companyRepository.save(company);
                    cacheService.evictCompany(company.getTin());
                    return company.isFreeTrial();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
        } else {
            throw validationService.badRequestExceptionThrow("Неадостаточно прав").get();
        }
    }

    @Transactional
    public void turnOffFreeTrial(List<Company> companies) {
        List<Company> list = new ArrayList<>();
        for (Company company : companies) {
            if (company.getTrialExpireDate() != null && LocalDateTime.now().isAfter(company.getTrialExpireDate())) {
                list.add(company);
            }
        }
        List<Company> companyList = new ArrayList<>();
        for (Company company : list) {
            company.setFreeTrial(false);
            company.setTrialExpireDate(null);
            companyList.add(company);
            cacheService.evictCompany(company.getTin());
        }
        companyRepository.saveAllAndFlush(companyList);
    }

    public boolean isBlocked(String tin) {
        return companyRepository.findByTinAndDeletedIsFalse(tin).map(company -> CompanyStatus.BLOCKED.equals(company.getStatus())).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", tin));
    }

    @Transactional
    public void updateStatus(Long id, CompanyStatus status) {
        companyRepository.findById(id).map(company -> {
            company.setStatus(status);
            company.setAllowInvoiceSync(CompanyStatus.ACTIVE.equals(status));
            companyRepository.save(company);
            cacheService.evictCompany(company.getTin());
            return company;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    @Transactional
    public FileDTO uploadBackgroundImage(Long id, MultipartFile file) {
        return companyRepository.findById(id).map(company -> {
            if (company.getBackgroundImage() != null) {
                fileUploadService.delete(company.getBackgroundImage());
            }
            FileUpload backgroundImage = fileUploadService.fileUpload(file);
            company.setBackgroundImageId(backgroundImage.getId());
            company.setBackgroundImage(backgroundImage);
            companyRepository.save(company);
            cacheService.evictCompany(company.getTin());
            return backgroundImage.toFileDTO();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void removeBackgroundImage(Long id) {
        companyRepository.findById(id).map(company -> {
            FileUpload backgroundImage = company.getBackgroundImage();
            if (backgroundImage != null) {
                company.setBackgroundImage(null);
                company.setBackgroundImageId(null);
                companyRepository.save(company);
                fileUploadService.delete(backgroundImage);
                cacheService.evictCompany(company.getTin());
            }
            return company;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    public Page<String> getAllToSync(SimpleFilter filter) {
        ResultList<String> resultList = companyRepository.getAllToSync(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public ResponseEntity<Object> getledgerDetailGruppedTransactions(String token, LedgerDetailAppTypeFilter filter) {
        User currentUser = getCurrentUser();
        UserFilter userFilter = new UserFilter();
        if (Role.ROLE_SUPERVISOR.equals(currentUser.getRole())) {
            userFilter.setSupervisorId(currentUser.getId());
        } else if (Role.ROLE_SUPPORT.equals(currentUser.getRole())) {
            userFilter.setManagerId(currentUser.getId());
        }
        List<String> companyTins = companyRepository.getCompanyTinsByManager(userFilter);
        filter.setTins(companyTins);
        return billingServiceFeignClient.getledgerDetailGruppedTransactions(token, filter);
    }

    public ResponseEntity<Object> getAllAgreementsPageable(String token, AgreementFilter filter) {
        UserFilter userFilter = new UserFilter();
        if (SecurityUtils.isFromAdmin()) {
            User currentUser = getCurrentUser();
            if (Role.ROLE_SUPERVISOR.equals(currentUser.getRole())) {
                userFilter.setSupervisorId(currentUser.getId());
            } else if (Role.ROLE_SUPPORT.equals(currentUser.getRole())) {
                userFilter.setManagerId(currentUser.getId());
            }
            filter.setTins(companyRepository.getCompanyTinsByManager(userFilter));
        } else if (getCurrentCompany() != null && StringUtils.isNotEmpty(getCurrentCompany().getTin())) {
            filter.setTins(List.of(getCurrentCompany().getTin()));
            filter.setInn(getCurrentCompany().getTin());
        } else {
            throw new BadRequestException(localizationService.localize(MessageConstants.ACCESS_DENIED), localizationService.localize(MessageConstants.CANNOT_UPDATE_OTHER_COMPANY_DATA));
        }
        return billingServiceFeignClient.getAllAgreementsPageable(token, filter);
    }

    @Transactional
    public void xFileProviderBinding(String tin) {
        Company company = StringUtils.isNotEmpty(tin) ? companyRepository.findByTinAndDeletedIsFalse(tin).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", tin)) :
            getCurrentCompany();
        boolean isXFileBind = facturaRestClient.getProviderBinding(tin)
            .stream().anyMatch(provider -> "306344019".equals(provider.getProviderTin()) && provider.isEnabled());
        company.setXFileEnabled(isXFileBind);
        companyRepository.save(company);
        cacheService.evictCompany(company.getTin());
    }

    @Deprecated
    @Transactional
    public Long oneCIntegration(CompanyOneCIntegrationDTO oneCIntegrationDTO) {
        return companyRepository.findById(oneCIntegrationDTO.getCompanyId() != null ? oneCIntegrationDTO.getCompanyId() : SecurityUtils.getCurrentCompanyId())
            .map(company -> {
                OneCIntegration oneCIntegration = oneCIntegrationRepository.findByCompanyId(company.getId())
                    .orElse(new OneCIntegration());
                if (oneCIntegration.getId() == null) {
                    oneCIntegration.setCompanyId(oneCIntegrationDTO.getCompanyId() != null ? oneCIntegrationDTO.getCompanyId() : SecurityUtils.getCurrentCompanyId());
                }
                oneCIntegration.setUrl(oneCIntegrationDTO.getUrl());
                oneCIntegration.setUsername(oneCIntegrationDTO.getUsername());
                oneCIntegration.setPassword(oneCIntegrationDTO.getPassword());
                oneCIntegrationRepository.save(oneCIntegration);
                company.setOneCEnabled(oneCIntegrationDTO.isOneCEnabled());
                companyRepository.save(company);
                cacheService.evictCompany(company.getTin());
                return oneCIntegration.getId();
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", oneCIntegrationDTO.getCompanyId() != null ? oneCIntegrationDTO.getCompanyId() : SecurityUtils.getCurrentCompanyId()));
    }

    @Deprecated
    public CompanyOneCIntegrationDTO oneCIntegration(Long companyId) {
        return oneCIntegrationRepository.findByCompanyId(companyId != null ? companyId : SecurityUtils.getCurrentCompanyId())
            .map(oneCIntegration -> {
                CompanyOneCIntegrationDTO oneCIntegrationDTO = new CompanyOneCIntegrationDTO();
                oneCIntegrationDTO.setId(oneCIntegration.getId());
                oneCIntegrationDTO.setCompanyId(oneCIntegration.getCompanyId());
                oneCIntegrationDTO.setUrl(oneCIntegration.getUrl());
                oneCIntegrationDTO.setUsername(oneCIntegration.getUsername());
                oneCIntegrationDTO.setPassword(oneCIntegration.getPassword());
                oneCIntegrationDTO.setOneCEnabled(oneCIntegration.getCompany().isOneCEnabled());
                return oneCIntegrationDTO;
            }).orElse(new CompanyOneCIntegrationDTO());
    }

    @Transactional
    public void updateForProductGroup(Long companyId, ProductGroup productGroup) {
        companyRepository.findById(companyId).ifPresent(company -> {
            company.setProductGroup(productGroup);
            companyRepository.save(company);
            cacheService.evictCompany(company.getTin());
        });
    }

    @Transactional
    public void updateEquipmentCount(UpdateCompanyEquipmentCountPayload payload) {
        companyRepository.updateEquipmentCount(payload.getId(), payload.getCount());
    }

    private void setCompanySettings(Long companyId) {
        log.info("Setting company settings for a new company with id {}, time = {}", companyId, LocalDateTime.now());
        Set<CompanySettings> newCompanySettingsList = genericSettingsRepository.findAllByForCabinetOrderByIdAsc(true)
            .stream()
                .map(genericSettings -> {
                    if (genericSettings.getSettingsType().equals(GenericSettingsType.IN_MARKETPLACE)) {
                        CompanySettings newCompanySettings = new CompanySettings();
                        newCompanySettings.setSettingsId(genericSettings.getId());
                        newCompanySettings.setCompanyId(companyId);
                        newCompanySettings.addValue(ENABLE_DISABLE.getCode(), true); // first time all settings are disabled
                        return newCompanySettings;
                    }
                    CompanySettings newCompanySettings = new CompanySettings();
                    newCompanySettings.setSettingsId(genericSettings.getId());
                    newCompanySettings.setCompanyId(companyId);
                    newCompanySettings.addValue(ENABLE_DISABLE.getCode(), false); // first time all settings are disabled except IN_MARKETPLACE
                    return newCompanySettings;
            }).collect(Collectors.toSet());
        companySettingsRepository.saveAll(newCompanySettingsList);


    }

    @Transactional
    public Long setParent(ParentCompanyDTO dto) {
        if (Objects.equals(dto.getParentCompanyId(), dto.getChildCompanyId())) {
            throw new BadRequestException("Нельзя указать одну и ту же компанию как родительскую и дочернюю.");
        }

        Company parentCompany = companyRepository.findByIdAndDeletedIsFalseAndStatus(dto.getParentCompanyId(), CompanyStatus.ACTIVE)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", dto.getParentCompanyId()));

        Company childCompany = companyRepository.findByIdAndDeletedIsFalseAndStatus(dto.getChildCompanyId(), CompanyStatus.ACTIVE)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", dto.getChildCompanyId()));

        if (childCompany.getParentId() != null)
            throw new BadRequestException("Для данной компании родитель уже установлен.");

        childCompany.setParentId(parentCompany.getId());
        childCompany.setPatentDate(LocalDateTime.now());
        return companyRepository.save(childCompany).getId();
    }

    public Page<ChildCompanyDTO> getChildren(CompanyFilter filter) {
        filter.setParentId(filter.getParentId() != null ? filter.getParentId() : SecurityUtils.getCompanyId());
        ResultList<Company> resultList = companyRepository.getResultList(filter);
        List<ChildCompanyDTO> result = resultList.getList().stream().map(company -> {
            ChildCompanyDTO childCompanyDTO = new ChildCompanyDTO();
            childCompanyDTO.setId(company.getId());
            childCompanyDTO.setName(company.getName());
            childCompanyDTO.setTin(company.getTin());
            childCompanyDTO.setParentDate(company.getPatentDate());
            return childCompanyDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Long removeChild(ParentCompanyDTO dto) {
        if (Objects.equals(dto.getParentCompanyId(), dto.getChildCompanyId())) {
            throw new BadRequestException("Нельзя указать одну и ту же компанию как родительскую и дочернюю.");
        }

        Company childCompany = companyRepository.findByIdAndDeletedIsFalseAndStatus(dto.getChildCompanyId(), CompanyStatus.ACTIVE)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", dto.getChildCompanyId()));

        if (!childCompany.getParentId().equals(dto.getParentCompanyId())) {
            throw new BadRequestException("Указанная родительская компания не является текущей родительской для данной компании.");
        }

        childCompany.setParentId(null);
        childCompany.setPatentDate(LocalDateTime.now());
        companyRepository.save(childCompany);
        return childCompany.getId();
    }

    public void batchSync(AppType appType) {
        companyRepository.getIdsToSync().forEach(id -> rabbitMqCompanySyncProducer.sync(new SyncPayload(id, appType)));
    }

    public void sync(Long id, AppType appType) {
        rabbitMqCompanySyncProducer.sync(new SyncPayload(id, appType));
    }

    public MarketplaceCompanySyncDTO getForMarketplace(Long id) {
        Company company = companyRepository.getDetail(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
        MarketplaceCompanySyncDTO marketplaceCompanySyncDTO = new MarketplaceCompanySyncDTO();
        marketplaceCompanySyncDTO.setAppType(AppType.SUPPLY.name());
        marketplaceCompanySyncDTO.setId(company.getId());
        marketplaceCompanySyncDTO.setName(company.getName());
        marketplaceCompanySyncDTO.setBrand(company.getBrand());
        if (SecurityUtils.isTin(company.getTin())) {
            marketplaceCompanySyncDTO.setTin(company.getTin());
        } else {
            marketplaceCompanySyncDTO.setPinfl(company.getTin());
        }
        if (company.getCompanyBank() != null) {
            marketplaceCompanySyncDTO.setOked(company.getCompanyBank().getOked());
        }
        if (company.getVat() != null) {
            marketplaceCompanySyncDTO.setVatRate(company.getVat().getAmount());
        }
        if (company.getBusinessType() != null) {
            marketplaceCompanySyncDTO.setBusinessType(company.getBusinessType().getCode());
        }
        if (company.getActivityType() != null) {
            ActivityTypeSyncDTO activityTypeSyncDTO = new ActivityTypeSyncDTO();
            activityTypeSyncDTO.setCode(company.getActivityType().getCode());
            activityTypeSyncDTO.setParentCode(company.getActivityType().getParent().getCode());
            marketplaceCompanySyncDTO.setActivityType(activityTypeSyncDTO);
        }
        if (company.getAddress() != null) {
            AddressSyncDTO addressSyncDTO = new AddressSyncDTO();
            if (company.getAddress().getRegion() != null) {
                addressSyncDTO.setRegionCode(company.getAddress().getRegion().getCode());
            }
            if (company.getAddress().getDistrict() != null) {
                addressSyncDTO.setDistrictCode(company.getAddress().getDistrict().getCode());
            }
            addressSyncDTO.setStreet(company.getAddress().getStreet());
            addressSyncDTO.setHouse(company.getAddress().getHouse());
            addressSyncDTO.setApartment(company.getAddress().getApartment());
            addressSyncDTO.setLongitude(company.getAddress().getLongitude());
            addressSyncDTO.setLatitude(company.getAddress().getLatitude());
            marketplaceCompanySyncDTO.setAddress(addressSyncDTO);
        }
        return marketplaceCompanySyncDTO;
    }
}
