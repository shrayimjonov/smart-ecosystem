package uz.uzkassa.smartpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.common.Bank;
import uz.uzkassa.smartpos.domain.postgres.common.Bank_;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.domain.postgres.company.*;
import uz.uzkassa.smartpos.domain.postgres.user.OperatorPermission;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.*;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.customer.CustomerBranchCtoDTO;
import uz.uzkassa.smartpos.dto.customer.CustomerBranchOwnerCtoDTO;
import uz.uzkassa.smartpos.dto.customer.CustomerCtoDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CompanyReceiptsDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CompanyReceiptsStatDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CurrentBranchesAndTerminalsDTO;
import uz.uzkassa.smartpos.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpos.dto.eimzo.SignatureCertificateDto;
import uz.uzkassa.smartpos.dto.factura.BankDTO;
import uz.uzkassa.smartpos.dto.factura.FacturaBankDTO;
import uz.uzkassa.smartpos.dto.factura.VATCompanyDate;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.CompanyFilter;
import uz.uzkassa.smartpos.dto.filter.CompanySpecificationFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.dto.integration.BillingTypeSyncDTO;
import uz.uzkassa.smartpos.dto.integration.agreement.CreateAgreementDTO;
import uz.uzkassa.smartpos.dto.mq.CompanyUnblockPayload;
import uz.uzkassa.smartpos.dto.mq.Payload;
import uz.uzkassa.smartpos.dto.nic.TINAbstractDTO;
import uz.uzkassa.smartpos.dto.terminal.company.TerminalCompanyDto;
import uz.uzkassa.smartpos.dto.terminal.company.TuronInfoDto;
import uz.uzkassa.smartpos.enums.*;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.EimzoClient;
import uz.uzkassa.smartpos.integration.FacturaClient;
import uz.uzkassa.smartpos.integration.apay.ApayClient;
import uz.uzkassa.smartpos.integration.apay.ApplicationStatusDTO;
import uz.uzkassa.smartpos.integration.apay.CheckCompanyDTO;
import uz.uzkassa.smartpos.integration.apay.FileDTO;
import uz.uzkassa.smartpos.integration.apay.request.CompanyRegisterDTO;
import uz.uzkassa.smartpos.integration.apay.response.ApaySubmitApplicationResponseDTO;
import uz.uzkassa.smartpos.integration.trueApi.AuthKey;
import uz.uzkassa.smartpos.integration.trueApi.TuronApi;
import uz.uzkassa.smartpos.mappers.CompanyMapper;
import uz.uzkassa.smartpos.mappers.FileUploadMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptReportByCompany;
import uz.uzkassa.smartpos.repository.postgres.FileUploadRepository;
import uz.uzkassa.smartpos.repository.postgres.common.ActivityTypeRepository;
import uz.uzkassa.smartpos.repository.postgres.common.BankRepository;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.common.RegionRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyTuronInfoRepository;
import uz.uzkassa.smartpos.repository.postgres.specifications.CompanySpecification;
import uz.uzkassa.smartpos.repository.postgres.user.AuthorityRepository;
import uz.uzkassa.smartpos.repository.postgres.user.OperatorPermissionRepository;
import uz.uzkassa.smartpos.repository.postgres.user.TelegramAccountRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.utils.RandomUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService extends SmartposService {
    private final NicService nicService;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final BankRepository bankRepository;
    private final OperatorPermissionRepository operatorPermissionRepository;
    private final ApayClient apayClient;
    private final EimzoClient eimzoClient;
    private final ObjectMapper objectMapper;
    private final FileUploadService fileUploadService;
    private final FileUploadMapper fileUploadMapper;
    private final FileUploadRepository fileUploadRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final LauncherService launcherService;
    private final FacturaClient facturaClient;
    private final TokenStoreService tokenStoreService;
    private final RabbitMqProducer rabbitMqProducer;
    private final UserRepository userRepository;
    private final ClickhouseReceiptReportByCompany clickhouseReceiptReportByCompany;
    private final TelegramAccountRepository telegramAccountRepository;
    private final TuronApi turonApi;
    private final CompanyTuronInfoRepository companyTuronInfoRepository;
    private final CompanyMapper companyMapper;
    private final TrialDateAuditService trialDateAuditService;

    @Transactional
    public CompanyDTO create(final CompanyDTO companyDTO) {
        if (StringUtils.isEmpty(companyDTO.getInn())) {
            throw badRequest(localizationService.getMessage("tin.required")).get();
        }
        Company company = companyRepository.findOneByInn(companyDTO.getInn())
                .orElseGet(Company::new);
        if (company.getId() != null) {
            throw badRequest(localizationService.getMessage("company.tin.exists")).get();
        }
        company.setInn(companyDTO.getInn());
        company.setName(companyDTO.getName());
        company.setBrand(companyDTO.getBrand());
        company.setOked(companyDTO.getOked());
        company.setAccountNumber(companyDTO.getAccountNumber());
        company.setMfo(companyDTO.getMfo());
        company.setAddress(companyDTO.getAddress());
        if (companyDTO.getRegion() != null && companyDTO.getRegion().getId() != null) {
            Region region = regionRepository.findById(companyDTO.getRegion().getId()).orElseThrow(notFound(localizationService.getMessage("region")));
            company.setRegion(region);
            company.setRegionId(region.getId());
        }
        if (companyDTO.getCity() != null && companyDTO.getCity().getId() != null) {
            City city = cityRepository.findById(companyDTO.getCity().getId()).orElseThrow(notFound(localizationService.getMessage("city")));
            company.setCity(city);
            company.setCityId(city.getId());
        }
        if (CollectionUtils.isEmpty(companyDTO.getTypes())) {
            throw new BadRequestException("Укажите сферу деятельности");
        }
        final List<ActivityType> activityTypes = activityTypeRepository.findAllById(
                companyDTO.getTypes().stream().map(ActivityTypeDTO::getId).collect(Collectors.toSet())
        );
        if (CollectionUtils.isEmpty(activityTypes) || activityTypes.stream().noneMatch(activityType1 -> activityType1.getParentId() != null)) {
            throw new BadRequestException("Укажите сферу деятельности");
        }
        company.setActivityTypes(new HashSet<>(activityTypes));
        company.setBusinessType(companyDTO.getBusinessType());
        company.setCompanyType(companyDTO.getCompanyType());
        company.setNdsPercent(companyDTO.getNdsPercent());
        setManager(companyDTO, company);
        company.setReservationEnabled(companyDTO.isReservationEnabled());
        company.setOwnerId(Optional.ofNullable(companyDTO.getOwnerId()).orElseGet(this::getCurrentUserId));
        company.setBillingType(BillingType.SMARTPOS);
        company.setFreeTrial(true);
        company.setTrialDate(LocalDateTime.now().plusDays(3));
        company = companyRepository.save(company);
        trialDateAuditService.save(company);
        companyDTO.setId(company.getId());
        companyDTO.setTypes(company.getActivityTypes().stream()
                .map(activityType -> new ActivityTypeDTO(activityType.getId(), activityType.getNameRu(), activityType.getParentId()))
                .collect(Collectors.toList())
        );

        final Long branchId = updateBranch(companyDTO);
        updateUser(companyDTO, branchId);

        return companyDTO;
    }

    @Transactional
    public CompanyDTO update(final CompanyDTO companyDTO, final boolean fromAdmin) {
        final Long companyId = fromAdmin ? companyDTO.getId() : getCurrentCompanyId();
        if (companyId == null || companyId == 0L) {
            throw notFound(localizationService.getMessage("company"), companyId).get();
        }
        final Company company = companyRepository.getReferenceById(companyId);
        if (!company.isFiscal()) {
            company.setInn(companyDTO.getInn());
            company.setName(companyDTO.getName());
            company.setBrand(companyDTO.getBrand());
            company.setOked(companyDTO.getOked());
            company.setAccountNumber(companyDTO.getAccountNumber());
            company.setMfo(companyDTO.getMfo());
            company.setAddress(companyDTO.getAddress());
            if (companyDTO.getRegion() != null && companyDTO.getRegion().getId() != null) {
                Region region = regionRepository.findById(companyDTO.getRegion().getId()).orElseThrow(notFound(localizationService.getMessage("region")));
                company.setRegion(region);
                company.setRegionId(region.getId());
            } else {
                company.setRegionId(null);
            }
            if (companyDTO.getCity() != null && companyDTO.getCity().getId() != null) {
                City city = cityRepository.findById(companyDTO.getCity().getId()).orElseThrow(notFound(localizationService.getMessage("city")));
                company.setCity(city);
                company.setCityId(city.getId());
            } else {
                company.setCityId(null);
            }
            if (CollectionUtils.isEmpty(companyDTO.getTypes())) {
                throw new BadRequestException("Укажите сферу деятельности");
            }
            final List<ActivityType> activityTypes = activityTypeRepository.findAllById(
                    companyDTO.getTypes().stream().map(ActivityTypeDTO::getId).collect(Collectors.toSet())
            );
            if (CollectionUtils.isEmpty(activityTypes) || activityTypes.stream().noneMatch(activityType -> activityType.getParentId() != null)) {
                throw new BadRequestException("Укажите сферу деятельности");
            }
            company.setActivityTypes(new HashSet<>(activityTypes));
        }
        boolean trialDateChanged = false;
        if (fromAdmin) {
            company.setNdsPercent(companyDTO.getNdsPercent());
            company.setPaymentTypes(companyDTO.getPaymentTypes());
            if (!company.isFreeTrial() && companyDTO.isFreeTrial()) {
                rabbitMqProducer.send(new CompanyUnblockPayload(Set.of(companyId)), MqConstants.UNBLOCK_TERMINALS_QUEUE);
            }
            if (!Objects.equals(company.getTrialDate(), companyDTO.getTrialDate())) {
                trialDateChanged = true;
            }
            company.setFreeTrial(companyDTO.isFreeTrial());
            company.setTrialDate(companyDTO.getTrialDate());
            company.setSupportId(companyDTO.getSupportId());
            company.setCompanyType(companyDTO.getCompanyType());
            company.setBusinessType(companyDTO.getBusinessType());
            company.setCompanyType(companyDTO.getCompanyType());
            company.setNdsPercent(companyDTO.getNdsPercent());
            setManager(companyDTO, company);
        }

        company.setReservationEnabled(companyDTO.isReservationEnabled());
        updateUser(companyDTO, null);
        companyRepository.save(company);
        if (trialDateChanged) {
            trialDateAuditService.save(company);
        }
        return companyDTO;
    }

    public void updatePinFl() {
        List<Company> companies = companyRepository.findAllByCtoIdIsNotNull();
        for (Company company : companies) {
            rabbitMqProducer.send(new Payload(company.getId(), company.getInn()), MqConstants.UPDATE_PINFL_QUEUE);
        }
    }

    public void updatePinFl(Payload payload) {
        final String inn = payload.getExtra();
        TINAbstractDTO tinAbstractDTO = nicService.getCustomerByTin(inn);
        if (StringUtils.isNotEmpty(tinAbstractDTO.getTin()) && !tinAbstractDTO.getTin().equals(inn)) {
            Company company = companyRepository.getReferenceById(payload.getId());
            company.setOldInn(inn);
            company.setInn(tinAbstractDTO.getTin());
            companyRepository.save(company);
        }
    }

    private Long updateBranch(CompanyDTO companyDTO) {
        final Long branchId = !isOperator() && !isAdmin() ? getCurrentBranchId() : null;
        if (branchId != null) {
            Branch branch = Optional.of(branchId)
                    .flatMap(branchRepository::findById).orElseGet(Branch::new);
            branch.setCompanyId(companyDTO.getId());
            branch.setName("Основной филиал");
            branch.setAddress(companyDTO.getAddress());
            if (companyDTO.getRegion() != null) {
                Region region = Optional.ofNullable(companyDTO.getRegion().getId())
                        .flatMap(regionRepository::findById).orElse(null);
                branch.setRegion(region);
                branch.setRegionId(companyDTO.getRegion().getId());
            }
            if (companyDTO.getCity() != null) {
                City city = Optional.ofNullable(companyDTO.getCity().getId())
                        .flatMap(cityRepository::findById).orElse(null);
                branch.setCity(city);
                branch.setCityId(companyDTO.getCity().getId());
                branch.setCityKeyId(companyDTO.getCity().getId());
            }
            if (companyDTO.getTypes() != null) {
                branch.setActivityTypeId(companyDTO.getTypes().stream().filter(activityTypeDTO -> activityTypeDTO.getParentId() != null).findFirst().map(ActivityTypeDTO::getId).orElseThrow(badRequest("Activity type doesn't have parent")));
            }
            branch = branchRepository.save(branch);
            return branch.getId();
        }
        return null;
    }

    private void updateUser(CompanyDTO companyDTO, Long branchId) {
        final Long userId = companyDTO.getOwnerId() != null ? companyDTO.getOwnerId() : null;
        if (userId != null && userId > 0L) {
            User user = userRepository.getReferenceById(userId);
            if (companyDTO.getContactFullName() != null) {
                user.setFirstName(companyDTO.getContactFullName().getFirstName());
                user.setLastName(companyDTO.getContactFullName().getLastName());
                user.setPatronymic(companyDTO.getContactFullName().getPatronymic());
            }
            user.setCompanyId(companyDTO.getId());
            if (branchId != null) {
                user.setBranchId(branchId);
            }
            userRepository.save(user);
            this.clearLoginCaches(user.getLogin());
        }
    }

    private void setManager(CompanyDTO companyDTO, Company company) {
        if (StringUtils.isNotBlank(companyDTO.getManagerLogin())) {
            final User manager = userRepository.findOneWithAuthoritiesByLogin(companyDTO.getManagerLogin()).orElseThrow(notFound("Manager"));
            if (manager.getAuthorities().stream().noneMatch(role -> role.getName().equals(AuthoritiesConstants.SALES_MANAGER))) {
                throw new BadRequestException("This user isn't manager");
            }
            company.setManager(manager);
            company.setManagerId(manager.getId());
        } else {
            company.setManager(null);
            company.setManagerId(null);
        }
    }

    public Page<CompanyDTO> getCompanies(CompanyFilter companyFilter) {
        CompanySpecificationFilter filter = CompanySpecificationFilter.builder()
                .companyId(companyFilter.getCompanyId())
                .to(companyFilter.getTo())
                .from(companyFilter.getFrom())
                .businessType(companyFilter.getBusinessType())
                .cityId(companyFilter.getCityId())
                .regionId(companyFilter.getRegionId())
                .freeTrial(companyFilter.getFreeTrial())
                .managerId(companyFilter.getUserId())
                .activityTypeId(companyFilter.getActivityTypeId())
                .search(companyFilter.getSearch())
                .build();
        return companyRepository.findAll((Specification<Company>) (root, criteriaQuery, criteriaBuilder) ->
                        CompanySpecification.getPredicateWithSort(root, criteriaQuery, criteriaBuilder, filter),
                companyFilter.getPageable()).map(Company::getDTO);
    }

    public List<CompanyBaseDTO> getCompaniesLookup(CompanyFilter companyFilter) {
        CompanySpecificationFilter specificationFilter = CompanySpecificationFilter.builder()
                .managerId(companyFilter.getUserId())
                .activityTypeId(companyFilter.getActivityTypeId())
                .businessType(companyFilter.getBusinessType())
                .from(companyFilter.getFrom())
                .to(companyFilter.getTo())
                .search(companyFilter.getSearch())
                .build();
        return companyRepository.companyLookup(specificationFilter);
    }


    public CompanyDTO getCurrentCompany() {
        final Long companyId = getCurrentCompanyId();
        if (companyId == null || companyId == 0L) {
            throw notFound(localizationService.getMessage("company")).get();
        }
        CompanyDTO dto = companyRepository.findById(companyId).map(Company::getDTO)
                .orElseThrow(notFound(localizationService.getMessage("company"), getCurrentCompanyId()));
        companyTuronInfoRepository.findById(companyId).ifPresent(info->{
            dto.setTrueToken(info.getToken());
            dto.setTokenDate(info.getReceivedDate());
        });
        return dto;
    }

    public void sync() {
        final Long companyId = getCurrentCompanyId();
        if (companyId == null || companyId == 0L) {
            throw notFound(localizationService.getMessage("company")).get();
        }
        Company company = companyRepository.findById(companyId)
                .orElseThrow(notFound(localizationService.getMessage("company"), getCurrentCompanyId()));
        if (StringUtils.isEmpty(company.getInn())) {
            throw badRequest(localizationService.getMessage("tin.missing")).get();
        }
        TINAbstractDTO dto = nicService.getCustomerByTin(company.getInn());
        company.setName(dto.getName());
        company.setInn(dto.getTin());
        company.setAccountNumber(dto.getAccount());
        company.setOked(dto.getOked());
        company.setMfo(dto.getMfo());
        company.setAddress(dto.getAddress());
        Optional<Region> region = regionRepository.findOneByCode(dto.getNs10Code());
        region.ifPresent(value -> company.setRegionId(value.getId()));
        Optional<City> city = cityRepository.findOneByCodeAndRegionCode(dto.getNs11Code(), dto.getNs10Code());
        city.ifPresent(value -> company.setCityId(value.getId()));
        companyRepository.save(company);
    }

    public String getTinFromSign(String sign) {
        Pkcs7Dto pkcs7Dto = eimzoClient.backendAuth(sign);
        if (pkcs7Dto.isSuccess()) {
            SignatureCertificateDto certificateDto = pkcs7Dto.getSubjectCertificateInfo();
            return certificateDto.getSubjectName().getTinOrPinFl();
        } else {
            throw new AuthenticationServiceException(localizationService.getMessage("cannot.authentication", pkcs7Dto.getMessage()));
        }
    }

    public boolean checkCompany(String tin) {
        return companyRepository.existsCompanyByInn(tin);
    }

    public CustomerCtoDTO getByTin(String tin) {
        Company company = companyRepository.findOneByInn(tin).orElse(null);
        return getCustomerCtoDto(company);
    }

    public CustomerCtoDTO getByCtoId(Long ctoId) {
        Company company = companyRepository.findFirstByCtoId(ctoId).orElse(null);
        return getCustomerCtoDto(company);
    }

    private CustomerCtoDTO getCustomerCtoDto(Company company) {
        CustomerCtoDTO customerCtoDTO = new CustomerCtoDTO();
        if (company != null) {
            customerCtoDTO.setId(company.getId());
            customerCtoDTO.setName(company.getName());
            customerCtoDTO.setTin(company.getInn());
            if (company.getBusinessType() != null) {
                customerCtoDTO.setBusinessType(company.getBusinessType().getNameRu());
            }
            if (company.getOwner() != null) {
                customerCtoDTO.setOwner(new CustomerBranchOwnerCtoDTO(company.getOwner()));
            }

            List<CustomerBranchCtoDTO> branches = null;
            if (CollectionUtils.isNotEmpty(company.getBranches())) {
                branches = company.getBranches().stream().map(branch -> new CustomerBranchCtoDTO(branch.getId(), branch.getName(), branch.getAddress(),
                        branch.getRegion() != null ? branch.getRegion().getNameRu() : null, branch.getCity() != null ? branch.getCity().getNameRu() : null)).collect(Collectors.toList());
            }
            customerCtoDTO.setBranches(branches);
        }

        return customerCtoDTO;
    }

    public CompanyDTO getCompany(Long id) {
//        checkForAccessToCompany(id);
        CompanyDTO companyDTO = companyRepository.findById(id).map(Company::getDTO)
                .orElseThrow(notFound(localizationService.getMessage("company"), id));

        companyTuronInfoRepository.findById(id).ifPresent(info-> {
            companyDTO.setTrueToken(info.getToken());
            companyDTO.setTokenDate(info.getReceivedDate());
        });

        Optional<OperatorPermission> permission =
                operatorPermissionRepository.findTopByCompanyIdAndLastModifiedDateIsNotNullOrderByLastModifiedDateDesc(id);
        permission.ifPresent(operatorPermission -> companyDTO.setSmsCode(operatorPermission.getCode()));
        return companyDTO;
    }

    public Company getCompanyById(Long id) {
        return companyRepository.getReferenceById(id);
    }

    public String getSmsCode(Long id) {
        return operatorPermissionRepository.findTopByCompanyIdAndLastModifiedDateIsNotNullOrderByLastModifiedDateDesc(id)
                .map(OperatorPermission::getCode)
                .orElse(null);
    }

    public List<CommonDTO> search(String searchKey) {
        return companyRepository.search(searchKey);
    }

    public Page<CompanyReceiptsDTO> getReceiptsAmountByCompany(CustomerReceiptsFilter filter) {
        Page<CompanyReceiptsDTO> result = clickhouseReceiptReportByCompany.getReceiptsAmountByCompany(filter);
        if (result != null && !result.getContent().isEmpty()) {
            Map<Long, CompanyReceiptsDTO> map = result.getContent().stream().collect(Collectors.toMap(CompanyReceiptsDTO::getId, Function.identity()));
            for (Company company : companyRepository.findAllById(map.keySet())) {
                if (map.containsKey(company.getId())) {
                    map.get(company.getId()).setActivityType(
                            CollectionUtils.isNotEmpty(company.getActivityTypes()) ?
                                    company.getActivityTypes().stream().map(ActivityType::getNameRu).collect(Collectors.joining(",")) :
                                    null
                    );
                    map.get(company.getId()).setRegionName(company.getRegionId() != null ? company.getRegion().getNameRu() : null);
                    map.get(company.getId()).setCityName(company.getCityId() != null ? company.getCity().getNameRu() : null);
                    map.get(company.getId()).setCompanyName(company.getName());
                }
            }
        }
        return result;
    }

    public CompanyReceiptsStatDTO getReceiptsAmountByCompanyStat(CustomerReceiptsFilter filter) {
        return clickhouseReceiptReportByCompany.getReceiptsAmountByCompanyStat(filter);
    }

    public CurrentBranchesAndTerminalsDTO getCurrentBranchesAndTerminalsCountByCompany(Long companyId) {
        return companyRepository.getCurrentBranchesAndTerminalsCountByCompany(Optional.ofNullable(companyId).orElseGet(SecurityUtils::getCurrentCompanyId));
    }

    public boolean getTelegramByPhoneNumber(String phoneNUmber) {
        boolean exists = telegramAccountRepository.existsByPhoneAndTelegramIdNotNull(phoneNUmber);
        if (!exists) {
            throw new EntityNotFound("You are not shared your telegram in smartpostrade_bot OR Telegram account not found with this number: " + phoneNUmber);
        }
        return true;
    }

    public boolean hasTelegramAccount(Long id) {
        return telegramAccountRepository.existsByCompanyId(id);
    }

    public void saveCadastre(String cadastreId) {
        final Long companyId = getCurrentCompanyId();
        if (companyId == null || companyId == 0L) {
            throw notFound(localizationService.getMessage("company")).get();
        }
        Company company = companyRepository.findById(getCurrentCompanyId())
                .orElseThrow(notFound(localizationService.getMessage("company"), getCurrentCompanyId()));
        company.setCadastreFileId(cadastreId);
        companyRepository.save(company);
    }

    public void saveApplication(String applicationId) {
        final Long companyId = getCurrentCompanyId();
        if (companyId == null || companyId == 0L) {
            throw notFound(localizationService.getMessage("company")).get();
        }
        Company company = companyRepository.findById(getCurrentCompanyId())
                .orElseThrow(notFound(localizationService.getMessage("company"), getCurrentCompanyId()));
        company.setApplicationFileId(applicationId);
        companyRepository.save(company);
    }

    public AuthKey getAuthKey() {
        return turonApi.authKey();
    }

    @Transactional
    public void sendApplication() {
        sendToApay(new CompanyRegisterBranchesDTO(getCurrentCompanyId(), null));
    }

    @Transactional
    public void sendToApay(CompanyRegisterBranchesDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if (StringUtils.isBlank(company.getInn())) {
            throw badRequest(localizationService.getMessage("tin.missing")).get();
        }
        if (company.getBusinessType() == null) {
            throw badRequest(localizationService.getMessage("business.not.defined")).get();
        }
        if (company.getActivityTypes() == null || company.getActivityTypes().stream().noneMatch(activityType -> activityType.getParent() != null)) {
            throw badRequest(localizationService.getMessage("sphere.activity.missing")).get();
        }
        if (company.getRegion() == null) {
            throw badRequest(localizationService.getMessage("company.region.missing")).get();
        }
        if (company.getCity() == null) {
            throw badRequest(localizationService.getMessage("company.city.missing")).get();
        }
        if (company.getOwner() == null) {
            throw badRequest(localizationService.getMessage("company.owner.missing")).get();
        }
        if (StringUtils.isBlank(company.getMfo())) {
            throw badRequest(localizationService.getMessage("company.mfo.missing")).get();
        }
        List<Branch> branches;
        if (CollectionUtils.isNotEmpty(dto.getBranchIds())) {
            branches = branchRepository.findAllByIdInAndCompanyId(dto.getBranchIds(), dto.getCompanyId());
        } else {
            branches = company.getBranches();
        }
        if (CollectionUtils.isEmpty(branches)) {
            throw badRequest(localizationService.getMessage("branch.missing")).get();
        }
        branches.forEach(branch -> {
            if (branch.getActivityType() == null || branch.getActivityType().getParent() == null) {
                throw badRequest(localizationService.getMessage("branch.sphere.activity.missing")).get();
            }
            if (branch.getRegion() == null) {
                throw badRequest(localizationService.getMessage("branch.region.missing")).get();
            }
            if (branch.getCity() == null) {
                throw badRequest(localizationService.getMessage("branch.city.missing")).get();
            }
        });
        boolean exists = false;
        try {
            ResponseEntity<CheckCompanyDTO> checkCompanyResponse = apayClient.checkCompanyExist(tokenStoreService.getToken(TokenType.APAY), company.getInn());
            if (checkCompanyResponse.getStatusCode().is2xxSuccessful() && checkCompanyResponse.getBody() != null) {
                CheckCompanyDTO companyDTO = checkCompanyResponse.getBody();
                company.setApayId(companyDTO.getApayCompanyId());
                company.setApplicationStatus(ApplicationStatus.APPROVED);
                company.getPaymentTypes().add(PaymentType.APAY);
                company.getPaymentTypes().add(PaymentType.APAY_WALLET);
                company.setApayTariffId(companyDTO.getTariff() != null ? companyDTO.getTariff().getId() : null);
                exists = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        CompanyRegisterDTO request = company.toApayDTO();
        request.setBranchList(branches.stream().map(Branch::toMerchantDTO).toList());
        try {
            log.info("Apay quick register request: {}", objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException ignored) {
        }
        if (!exists) {
            ResponseEntity<ApaySubmitApplicationResponseDTO> response = apayClient.registerCompany(tokenStoreService.getToken(TokenType.APAY), request);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Apay quick register response: {}", response.getBody());
                company.setApayId(response.getBody().getId());
                company.setApplicationStatus(response.getBody().getStatus());
            }
        } else {
            apayClient.registerBranches(tokenStoreService.getToken(TokenType.APAY), request);
        }
        company.getPaymentTypes().add(PaymentType.APAY);
        company.getPaymentTypes().add(PaymentType.APAY_WALLET);
        branches.forEach(branch -> branch.setApayStatus(ApayRegisterStatus.REGISTERED));
        companyRepository.save(company);
        branchRepository.saveAll(branches);
    }


    public void updateStatus(ApplicationStatusDTO statusDto) {
        Optional<Company> companyOptional = companyRepository.findOneByApayId(statusDto.getId());
        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();
            company.setApplicationStatus(statusDto.getStatus());
            company.setFiscalNumber(statusDto.getFiscalNumber());
            company.setFiscal(true);
            companyRepository.save(company);
        }
    }

    public List<BankDTO> getBanks(final BaseFilter filter) {
        return bankRepository.findAll((Specification<Bank>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Bank_.id)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Bank_.nameRu)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Bank_.nameUz)), filter.getSearchForQuery())
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        }).stream().map(Bank::toDto).collect(Collectors.toList());
    }

    public void syncBanks(FacturaBankDTO[] bankDTOS, String lang) {
        List<Bank> banks = new ArrayList<>();
        for (FacturaBankDTO dto : bankDTOS) {
            Optional<Bank> bankOptional = bankRepository.findById(dto.getBankId());
            if (bankOptional.isPresent()) {
                continue;
            }
            Bank bank = new Bank();
            bank.setId(dto.getBankId());
            if ("uz".equalsIgnoreCase(lang)) {
                bank.setNameUz(dto.getName());
            } else {
                bank.setNameRu(dto.getName());
            }
            banks.add(bank);
            if (banks.size() == 50) {
                bankRepository.saveAll(banks);
                bankRepository.flush();
                banks.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(banks)) {
            bankRepository.saveAll(banks);
            bankRepository.flush();
        }
    }

    public FileDTO uploadLogo(Long companyId, MultipartFile file) {
        if (!Sets.newHashSet("image/jpeg", "image/png", "image/bmp", "image/ico", "image/heif", "image/heic").contains(file.getContentType())) {
            throw new BadRequestException("File format error");
        }
        Company company = companyRepository.findById(companyId).orElseThrow(notFound(localizationService.getMessage("company")));
        if (company.getLogo() != null) {
            removeLogo(companyId);
        }
        FileUpload logo = fileUploadService.fileUpload(file, companyId, EntityType.COMPANY, true);
        company.setLogoId(logo.getId());
        company.setLogo(logo);
        companyRepository.save(company);
        return fileUploadMapper.toDTO(logo);
    }

    public void removeLogo(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(notFound(localizationService.getMessage("company")));
        FileUpload logo = company.getLogo();
        if (logo != null) {
            company.setLogo(null);
            company.setLogoId(null);
            companyRepository.save(company);
            fileUploadService.removeObject(logo);
            fileUploadRepository.delete(logo);
        }
    }

    @Transactional
    public void testCompany(UserTestDTO testDTO) {
        List<ActivityType> activityTypes = activityTypeRepository.findAllById(Collections.singleton(1L));

        Company company = new Company();
        company.setName(testDTO.getCompanyName());
        company.setCreatedBy(Constants.SYSTEM_ACCOUNT);
        company.setBusinessType(BusinessType.OOO);
        company.setInn(RandomUtil.generateNumericKey());
        company.setActivityTypes(new HashSet<>(activityTypes));
        company.setCompanyType(CompanyType.TEST);
        companyRepository.save(company);

        Branch branch = new Branch();
        branch.setName("Основная филиал");
        branch.setCompanyId(company.getId());
        branch.setCreatedBy(Constants.SYSTEM_ACCOUNT);
        branch.setCityId(9L);
        branch.setCityKeyId(9L);
        branchRepository.save(branch);

        User user = new User();
        user.setLogin(testDTO.getLogin());
        user.setFirstName(testDTO.getFirstName());
        user.setLastName(testDTO.getLastName());
        user.setPassword(passwordEncoder.encode(testDTO.getPassword()));
        user.setActivated(true);
        user.setCreatedBy(Constants.SYSTEM_ACCOUNT);
        user.setCompanyId(company.getId());
        user.setBranchId(branch.getId());
        user.getAuthorities().add(authorityRepository.getReferenceById(AuthoritiesConstants.USER));
        user.getAuthorities().add(authorityRepository.getReferenceById(AuthoritiesConstants.OWNER));
        userRepository.save(user);
        company.setOwnerId(user.getId());

    }

    public void addManager(ManagerCompanyDTO managerCompanyDTO) {
        companyRepository.clearManager(managerCompanyDTO.getManagerId());
        User manager = userRepository.getReferenceById(managerCompanyDTO.getManagerId());
        List<Company> companies = companyRepository.findAllByIdIn(managerCompanyDTO.getCompanyIds());
        for (Company company : companies) {
            company.setManager(manager);
            company.setManagerId(managerCompanyDTO.getManagerId());
        }
        companyRepository.saveAll(companies);
    }

    public ResponseEntity<TINAbstractDTO> getCompanyFromNic(String tin) {
        if (checkCompany(tin)) {
            throw new BadRequestException(localizationService.getMessage("company.tin.exists"));
        }
        TINAbstractDTO company = nicService.getCustomerByTin(tin);
        if (StringUtils.isEmpty(company.getTin())) {
            throw new BadRequestException(localizationService.getMessage("company.tin.not.exists"));
        }
        return ResponseEntity.ok(company);
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void removeFreeTrial() {
        List<Company> freeTrialCompanies = companyRepository.findAllByTrialDateLessThanEqual(LocalDateTime.now());
        freeTrialCompanies.forEach(company -> {
            company.setFreeTrial(false);
            company.setTrialDate(null);
        });
        companyRepository.saveAll(freeTrialCompanies);
    }

    @Transactional
    public void billingTypeSync(BillingTypeSyncDTO dto) {
        List<Company> companies = companyRepository.findByCtoIdOrInnIn(dto.getCtoId(), dto.getTins());
        companies.forEach(company -> company.setBillingType(dto.getBillingType() == null ? BillingType.SMARTPOS : dto.getBillingType()));
        if (BillingType.SMARTPOS.equals(dto.getBillingType())) {
            Set<Long> companyIds = companies.stream()
                    .filter(company ->
                            company.getPermissions()
                                    .values().stream()
                                    .anyMatch(endTime -> endTime.isAfter(LocalDateTime.now())))
                    .map(Company::getId).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(companyIds)) {
                launcherService.unblockTerminals(companyIds);
            }
        }
        companyRepository.saveAll(companies);
    }

    public CommonUserDTO getManagerFromCompany() {
        Company company = companyRepository.findById(getCurrentCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        CommonUserDTO managerDTO = new CommonUserDTO();
        if (company.getManagerId() != null) {
            managerDTO.setId(company.getManagerId());
            managerDTO.setFirstName(company.getManager().getFirstName());
            managerDTO.setLastName(company.getManager().getLastName());
            managerDTO.setPatronymic(company.getManager().getPatronymic());
            managerDTO.setPhone(company.getManager().getLogin());
        } else {
            throw new BadRequestException("К вашей компании не прикреплен менеджер", 400);
        }
        return managerDTO;
    }

    public void syncVatIsNullCompanies() {
        int i = 0;
        Page<Company> companies = companyRepository.findAll((Specification<Company>) (root, query, criteriaBuilder) ->
                        criteriaBuilder.and(criteriaBuilder.isNotNull(root.get(Company_.inn)), criteriaBuilder.isNotNull(root.get(Company_.ndsPercent))),
                PageRequest.of(i++, 100, Sort.by("id")));
        while (!companies.isEmpty()) {
            var companyList = new ArrayList<Company>();
            companies.forEach(company -> {
                VATCompanyDate companyVatByDate = facturaClient.getCompanyVatByDate(company.getInn());
                if (companyVatByDate.getData() != null && companyVatByDate.getData().getVatRegCode() == null && company.getNdsPercent() != null) {
                    company.setNdsPercent(null);
                    companyList.add(company);
                }
            });
            if (CollectionUtils.isNotEmpty(companyList)) {
                companyRepository.saveAll(companyList);
            }
            companies = companyRepository.findAll((Specification<Company>) (root, query, criteriaBuilder) -> criteriaBuilder.and(criteriaBuilder.isNotNull(root.get(Company_.inn)), criteriaBuilder.isNotNull(root.get(Company_.ndsPercent))),
                    PageRequest.of(i++, 100, Sort.by("id")));
        }
    }

    public void setManagerData(CreateAgreementDTO dto) {
        companyRepository.findOneByInn(dto.getInn()).ifPresent(company -> {
            if (company.getManager() != null) {
                dto.setFirstName(company.getManager().getFirstName());
                dto.setFirstName(company.getManager().getLastName());
                dto.setPatronymic(company.getManager().getPatronymic());
                dto.setLogin(company.getManager().getLogin());
            }
        });
    }

    public void checkApplicationStatus(String inn) {
        Company company = companyRepository.findOneByInn(inn).orElseThrow(notFound(localizationService.getMessage("company")));
        if (!(company.getApplicationStatus() == ApplicationStatus.APPROVED)) {
            throw new BadRequestException(localizationService.getMessage("company.not.found.in.apay"));
        }
    }

    public TerminalCompanyDto currentCompanyForTerminal() {
        Company company = companyRepository.findById(getCurrentCompanyId())
                .orElseThrow(notFound(localizationService.getMessage("company")));
        return new TerminalCompanyDto(company);
    }

    public TuronInfoDto getTuronInfo() {
        CompanyTuronInfo companyTuronInfo = companyTuronInfoRepository.findById(getCurrentCompanyId()).orElseThrow(() -> new BadRequestException(localizationService.getMessage("turon.token.not.found")));
        return companyMapper.toDto(companyTuronInfo);
    }

    public Company getCompany() {
        return companyRepository.findById(getCurrentCompanyId())
                .orElseThrow(notFound(localizationService.getMessage("company")));
    }

}
