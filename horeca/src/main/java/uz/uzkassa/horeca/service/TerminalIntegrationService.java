package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.KafkaConstants;
import uz.uzkassa.horeca.domain.ActivityType;
import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.Terminal;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.auth.UserAuth;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.receipt.Receipt;
import uz.uzkassa.horeca.domain.receipt.ReceiptDetail;
import uz.uzkassa.horeca.domain.redis.AccountBlockList;
import uz.uzkassa.horeca.domain.terminal.TerminalLog;
import uz.uzkassa.horeca.domain.terminal.TerminalReceiptTemplate;
import uz.uzkassa.horeca.domain.terminal.TerminalShiftReport;
import uz.uzkassa.horeca.dto.auth.UserAuthDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.cts.CtsFiscalCheckDTO;
import uz.uzkassa.horeca.dto.cts.CtsFiscalizationCheckDeviceDTO;
import uz.uzkassa.horeca.dto.sms.SmsRequestDTO;
import uz.uzkassa.horeca.dto.terminal.*;
import uz.uzkassa.horeca.enums.*;
import uz.uzkassa.horeca.enums.terminal.TerminalApplicationStatus;
import uz.uzkassa.horeca.enums.terminal.TerminalBusinessType;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.filters.ProductFilter;
import uz.uzkassa.horeca.filters.ReceiptFilter;
import uz.uzkassa.horeca.filters.UserFilter;
import uz.uzkassa.horeca.filters.terminal.TerminalUserFilter;
import uz.uzkassa.horeca.integration.CtsClient;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqTerminalReceiptProducer;
import uz.uzkassa.horeca.repository.*;
import uz.uzkassa.horeca.repository.redis.AccountBlockListRepository;
import uz.uzkassa.horeca.repository.terminal.CardTypeRepository;
import uz.uzkassa.horeca.repository.terminal.TerminalReceiptTemplateRepository;
import uz.uzkassa.horeca.repository.terminal.TerminalShiftReportRepository;
import uz.uzkassa.horeca.security.jwt.TokenProvider;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static uz.uzkassa.horeca.utils.HeaderUtils.isBusinessOwner;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TerminalIntegrationService extends BaseService {

    PasswordEncoder passwordEncoder;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    TokenProvider tokenProvider;
    SmsService smsService;
    CompanyBankRepository companyBankRepository;
    ActivityTypeRepository activityTypeRepository;
    DistrictRepository districtRepository;
    RegionRepository regionRepository;
    CtsClient ctsClient;
    UserAuthService userAuthService;
    UserAuthRepository userAuthRepository;
    TerminalRepository terminalRepository;
    CardTypeRepository cardTypeRepository;
    CategoryRepository categoryRepository;
    TerminalLogRepository terminalLogRepository;
    AmazonS3Service amazonS3Service;
    KafkaTemplate<String, String> kafkaTemplate;
    ProductService productService;
    ProductRepository productRepository;
    TerminalReceiptTemplateRepository terminalReceiptTemplateRepository;
    ReceiptRepository receiptRepository;
    RabbitMqTerminalReceiptProducer rabbitMqTerminalReceiptProducer;
    TerminalShiftReportRepository terminalShiftReportRepository;
    ReceiptDetailRepository receiptDetailRepository;
    String SMS_TEXT_RECOVERY =
            "smartpos.uz Vosstanovlenie\n" +
                    "Vnimanie, ne soobshayte kod podtverzhdeniya tret'im licam.\n" +
                    "Tasdiqlash kodini uchinchi shaxslarga oshkor qilmang.\n " +
                    "Kod: ";
    private final AccountBlockListRepository accountBlockListRepository;

    public List<TerminalUnitDTO> getUnits() {
        return unitRepository
                .findAll()
                .stream()
                .map(unit -> {
                    TerminalUnitDTO terminalUnitDTO = new TerminalUnitDTO();
                    terminalUnitDTO.setId(unit.getId());
                    terminalUnitDTO.setName(unit.getName());
                    terminalUnitDTO.setNameRu(unit.getNameRu());
                    terminalUnitDTO.setNameUz(unit.getNameUz());
                    terminalUnitDTO.setDescription(unit.getDescription());
                    terminalUnitDTO.setCode(Long.valueOf(unit.getCode()));
                    terminalUnitDTO.setCountable(unit.isCountable());
                    return terminalUnitDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        HeaderUtils
                .getCurrentUserLogin()
                .flatMap(userRepository::findFirstByLoginAndDeletedIsFalse)
                .ifPresent(user -> {
                    String currentEncryptedPassword = user.getPassword();
                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw badRequestExceptionThrow("Incorrect password").get();
                    }
                    if (passwordEncoder.matches(newPassword, currentEncryptedPassword)) {
                        throw badRequestExceptionThrow("New password should be same as old one").get();
                    }
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    cacheService.clearUserCache(user);
                });
    }

    public Page<TerminalUserDTO> getUsers(TerminalUserFilter filter) {
        UserFilter userFilter = new UserFilter();
        userFilter.setRoles(Role.getCabinetRoles());
        if (!CollectionUtils.isEmpty(filter.getBranchIds())) {
            userFilter.setBranchId(filter.getBranchIds().stream().findFirst().get());
        } else {
            userFilter.setBranchId(getCurrentBranchId(filter.getBranchId(), false));
            if (userFilter.getBranchId() == null) {
                terminalRepository.findFirstBySerialNumberAndDeletedIsFalse(HeaderUtils.getCurrentTerminalSnByHeader()).ifPresent(terminal -> userFilter.setBranchId(terminal.getBranchId()));
            }
        }
        userFilter.setCompanyId(getCurrentCompanyId(filter.getCompanyId()));
        userFilter.setPage(filter.getPage());
        userFilter.setSize(filter.getSize());
        userFilter.setSearch(filter.getSearch());
        userFilter.setOrderBy(filter.getOrderBy());
        userFilter.setSortOrder(filter.getSortOrder());
        if (!CollectionUtils.isEmpty(filter.getRoles())) {
            userFilter.setRoles(filter.getRoles().stream().map(Role::valueOf).collect(Collectors.toList()));
        }
        userFilter.setFrom(filter.getFrom());
        userFilter.setTo(filter.getTo());

        ResultList<User> resultList = userRepository.getResultList(userFilter);
        List<TerminalUserDTO> result = new ArrayList<>(resultList.getList().stream().map(this::getUserDTO).toList());
        if (getCurrentCompany().getOwnerId() != null) {
            userRepository.findById(getCurrentCompany().getOwnerId()).ifPresent(owner -> result.add(this.getUserDTO(owner)));
        }
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public TerminalUserDTO createUser(TerminalUserDTO userDTO) {
        final String password = SecurityUtils.generateActivationCode();
        if (userRepository.findFirstByLoginAndDeletedIsFalse(userDTO.getLogin()).isPresent()) {
            throw badRequestExceptionThrow("User already exists").get();
        }
        Long companyId = getCurrentCompanyId(userDTO.getCompanyId());
        Company company = companyRepository
                .findById(companyId)
                .orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));

        Long branchId = Optional.ofNullable(userDTO.getBranchId()).orElse(getCurrentBranchId());
        Branch branch = branchRepository
                .findById(branchId)
                .orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", companyId));

        User user = new User();
        user.setCompanyId(company.getId());
        user.setCompany(company);
        user.setBranchId(branch.getId());
        user.setBranch(branch);
        user.setLogin(userDTO.getLogin());
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(UserStatus.ACTIVE);

        user.setFirstName(userDTO.getFullName().getFirstName());
        user.setLastName(userDTO.getFullName().getLastName());
        user.setPatronymic(userDTO.getFullName().getPatronymic());

        user.setLanguage(LanguageEnum.get(userDTO.getLangKey()));
        user.setRole(Role.getByCode(userDTO.getAuthorities().stream().findFirst().get()));

        userRepository.save(user);
        cacheService.clearUserCache(user);
        smsService.send(new SmsRequestDTO(userDTO.getLogin(), password));

        return getUserDTO(user);
    }

    private TerminalUserDTO getUserDTO(User user) {
        TerminalUserDTO userDTO = new TerminalUserDTO();
        userDTO.setId(user.getId());
        userDTO.setCreatedDate(user.getCreatedDate());
        if (user.getCreator() != null) {
            userDTO.setCreatedBy(user.getCreator().getName());
        }
        userDTO.setLastModifiedDate(user.getUpdatedDate());
        if (user.getUpdater() != null) {
            userDTO.setLastModifiedBy(user.getUpdater().getName());
        }
        userDTO.setLogin(user.getLogin());

        userDTO.setActivated(UserStatus.ACTIVE == user.getStatus());
        if (LanguageEnum.ru == user.getLanguage() || LanguageEnum.uz == user.getLanguage()) {
            userDTO.setLangKey(user.getLanguage().getCode());
        } else {
            userDTO.setLangKey(LanguageEnum.getDefaultLanguage().getCode());
        }
        userDTO.setAuthorities(Set.of(user.getRole().getCode()));

        TerminalFullNameDTO fullNameDTO = new TerminalFullNameDTO();
        fullNameDTO.setPhone(user.getPhone());
        fullNameDTO.setFirstName(user.getFirstName());
        fullNameDTO.setLastName(user.getLastName());
        fullNameDTO.setPatronymic(user.getPatronymic());
        userDTO.setFullName(fullNameDTO);

        userDTO.setOwner(Role.ROLE_OWNER == user.getRole());
        if (user.getBranch() != null) {
            userDTO.setBranchId(user.getBranchId());
            userDTO.setBranchName(user.getBranch().getName());
        }
        userDTO.setCompanyId(user.getCompanyId());
        userDTO.setCompanyName(user.getCompany().getName());
        userDTO.setCompanyInn(user.getCompany().getTin());

        return userDTO;
    }

    public TerminalUserDTO getUser(Long id) {
        return userRepository.findById(id).map(this::getUserDTO).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    @Transactional
    public TerminalUserDTO updateUser(TerminalUserDTO userDTO) {
        return userRepository
                .findById(userDTO.getId())
                .map(user -> {
                    if (userRepository.existsUserByLoginAndDeletedIsFalseAndIdNot(userDTO.getLogin(), user.getId())) {
                        throw badRequestExceptionThrow("User already exists").get();
                    }
                    user.setLogin(userDTO.getLogin());
                    user.setFirstName(userDTO.getFullName().getFirstName());
                    user.setLastName(userDTO.getFullName().getLastName());
                    user.setPatronymic(userDTO.getFullName().getPatronymic());
                    user.setLanguage(LanguageEnum.get(userDTO.getLangKey()));

                    userRepository.save(user);
                    cacheService.clearUserCache(user);
                    return getUserDTO(user);
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", userDTO.getId()));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository
                .findById(id)
                .map(user -> {
                    if (Role.ROLE_OWNER == user.getRole()) {
                        throw badRequestExceptionThrow("You cannot delete owner").get();
                    }
                    user.setDeleted(true);
                    userRepository.save(user);
                    cacheService.clearUserCache(user);
                    AccountBlockList accountBlockList = accountBlockListRepository.findById(id).orElse(new AccountBlockList(id));
                    accountBlockList.setTtl(getTokenTTL());
                    accountBlockListRepository.save(accountBlockList);
                    return user;
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public TerminalUserDTO getAccount() {
        return HeaderUtils
                .getCurrentUserLogin()
                .flatMap(userRepository::findFirstByLoginAndDeletedIsFalse)
                .map(user -> {
                    TerminalUserDTO terminalUserDTO = getUserDTO(user);
                    terminalUserDTO.setAuthorities(Set.of(Role.ROLE_ADMIN.getCode(), Role.ROLE_USER.getCode(), Role.ROLE_ANONYMOUS.getCode()));
                    return terminalUserDTO;
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName()));
    }

    @Transactional
    public void resetPasswordInit(String login) {
        userRepository
                .findFirstByLoginAndDeletedIsFalse(login)
                .map(user -> {
                    final String activationCode = SecurityUtils.generateActivationCode();
                    userAuthService.create(UserAuthDTO.builder().login(login).activationCode(activationCode).build());
                    userRepository.save(user);
                    cacheService.clearUserCache(user);
                    smsService.send(new SmsRequestDTO(login, StringUtils.join(SMS_TEXT_RECOVERY, activationCode)));
                    return user;
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "login", login));
    }

    @Transactional
    public void resetPasswordFinish(TerminalResetPasswordDTO terminalResetPasswordDTO) {
        User user = userRepository
                .findFirstByLoginAndDeletedIsFalse(terminalResetPasswordDTO.getLogin())
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName()));
        UserAuth userAuth = userAuthService.getUserAuth(terminalResetPasswordDTO.getLogin(), terminalResetPasswordDTO.getKey());
        if (userAuth == null) {
            throw badRequestExceptionThrow("Incorrect reset key").get();
        }
        if (userAuth.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw badRequestExceptionThrow("Activation code is expired").get();
        }
        user.setPassword(passwordEncoder.encode(terminalResetPasswordDTO.getNewPassword()));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        cacheService.clearUserCache(user);
    }

    public boolean checkResetKey(TerminalResetPasswordDTO keyAndPasswordVM) {
        return userAuthRepository.findFirstByLoginAndActivationCodeOrderByIdDesc(keyAndPasswordVM.getLogin(), keyAndPasswordVM.getKey()).isPresent();
    }

    public String getToken(TerminalLoginDTO terminalLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                terminalLoginDTO.getUsername(),
                terminalLoginDTO.getPassword()
        );

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return tokenProvider.createToken(authentication, true);
        } catch (AuthenticationException e) {
            throw badRequestExceptionThrow(localizationService.localize("incorrect.username.or.password")).get();
        }
    }

    public List<String> getRolesByLogin(String login) {
        return userRepository.findFirstByLoginAndDeletedIsFalse(login).map(user -> List.of(user.getRole().getCode())).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "login", login));
    }

    @Transactional
    public void resetPin(String phone) {
        Optional
                .ofNullable(phone)
                .flatMap(userRepository::findFirstByLoginAndDeletedIsFalse)
                .filter(user -> Role.ROLE_CASHIER == user.getRole())
                .map(user -> {
                    final String newPin = SecurityUtils.generateActivationCode();
                    userAuthService.create(UserAuthDTO.builder().login(phone).activationCode(newPin).build());
                    user.setPassword(passwordEncoder.encode(newPin));
                    userRepository.save(user);
                    cacheService.clearUserCache(user);
                    smsService.send(new SmsRequestDTO(phone, newPin));
                    return user;
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "login", phone));
    }

    public List<TerminalVatDTO> getVatList() {
        return List.of(
                new TerminalVatDTO(1L, Vat.VAT_0.getName(), Vat.VAT_0.getAmount()),
                new TerminalVatDTO(2L, Vat.VAT_12.getName(), Vat.VAT_12.getAmount())
        );
    }

    public TerminalCompanyDTO getCompany(Long companyId) {
        Long id = Optional.ofNullable(companyId).orElse(getCurrentCompanyId());
        if (id == null || id == 0L) {
            throw notFoundExceptionThrow("Company not found").get();
        }
        return companyRepository
                .findById(id)
                .map(company -> {
                    TerminalCompanyDTO terminalCompanyDTO = new TerminalCompanyDTO();
                    terminalCompanyDTO.setId(company.getId());
                    terminalCompanyDTO.setCreatedBy(company.getCreator() != null ? company.getCreator().getName() : null);
                    terminalCompanyDTO.setCreatedDate(company.getCreatedDate());
                    terminalCompanyDTO.setLastModifiedBy(company.getUpdater() != null ? company.getUpdater().getName() : null);
                    terminalCompanyDTO.setLastModifiedDate(company.getUpdatedDate());
                    terminalCompanyDTO.setCtoId(null);
                    terminalCompanyDTO.setInn(company.getTin());
                    terminalCompanyDTO.setName(company.getName());
                    terminalCompanyDTO.setBrand(company.getBrand());
                    companyBankRepository
                            .findFirstByCompanyIdAndDeletedIsFalse(company.getId())
                            .ifPresent(companyBank -> {
                                terminalCompanyDTO.setOked(company.getOked());
                                terminalCompanyDTO.setAccountNumber(companyBank.getAccountNumber());
                                terminalCompanyDTO.setMfo(companyBank.getBank().getMfo());
                                terminalCompanyDTO.setBankId(companyBank.getBankId().toString());
                                terminalCompanyDTO.setBankName(companyBank.getBank().getName());
                            });

                    terminalCompanyDTO.setFiscal(false);
                    terminalCompanyDTO.setBusinessType(
                            company.getBusinessType() != null ? TerminalBusinessType.getByCode(company.getBusinessType().getCode()) : null
                    );
                    terminalCompanyDTO.setPaysNds(false);
                    terminalCompanyDTO.setNdsPercent(company.getVat() != null ? company.getVat().getAmount() : null);
                    terminalCompanyDTO.setWarehouseEnabled(false);
                    terminalCompanyDTO.setSyncDate(null);
                    if (company.getOwner() != null) {
                        terminalCompanyDTO.setOwnerId(company.getOwner().getId());
                        TerminalFullNameDTO contactFullName = new TerminalFullNameDTO();
                        contactFullName.setFirstName(company.getOwner().getFirstName());
                        contactFullName.setLastName(company.getOwner().getLastName());
                        contactFullName.setPatronymic(company.getOwner().getPatronymic());
                        contactFullName.setPhone(company.getOwner().getLogin());
                        terminalCompanyDTO.setContactFullName(contactFullName);
                        terminalCompanyDTO.setPhone(company.getOwner().getLogin());
                    }
                    if (company.getAddress() != null) {
                        terminalCompanyDTO.setAddress(company.getAddress().getStreet());
                        if (company.getAddress().getRegion() != null) {
                            TerminalRegionDTO terminalRegionDTO = new TerminalRegionDTO();
                            terminalRegionDTO.setId(company.getAddress().getRegion().getId());
                            terminalRegionDTO.setCode(Long.valueOf(company.getAddress().getRegion().getCode()));
                            terminalRegionDTO.setNameUz(company.getAddress().getRegion().getNameUz());
                            terminalRegionDTO.setNameRu(company.getAddress().getRegion().getNameRu());
                            terminalCompanyDTO.setRegion(terminalRegionDTO);
                        }
                        if (company.getAddress().getDistrict() != null) {
                            TerminalCityDTO terminalCityDTO = new TerminalCityDTO();
                            terminalCityDTO.setId(company.getAddress().getDistrict().getId());
                            terminalCityDTO.setCode(Long.valueOf(company.getAddress().getDistrict().getCode()));
                            terminalCityDTO.setNameUz(company.getAddress().getDistrict().getNameUz());
                            terminalCityDTO.setNameRu(company.getAddress().getDistrict().getNameRu());
                            terminalCompanyDTO.setCity(terminalCityDTO);
                        }
                    }
                    if (company.getActivityType() != null) {
                        TerminalActivityTypeDTO activityTypeDTO = new TerminalActivityTypeDTO();
                        activityTypeDTO.setId(company.getActivityType().getId());
                        activityTypeDTO.setName(company.getActivityType().getName());
                        activityTypeDTO.setParentId(company.getActivityType().getParentId());
                        terminalCompanyDTO.setTypes(List.of(activityTypeDTO));
                    }
                    terminalCompanyDTO.setAutoPrintEnabled(true);
                    terminalCompanyDTO.setTrueToken(null);
                    terminalCompanyDTO.setTokenDate(null);
                    terminalCompanyDTO.setCadastreFileId(null);
                    terminalCompanyDTO.setApplicationFileId(null);
                    terminalCompanyDTO.setApplicationStatus(TerminalApplicationStatus.APPROVED);
                    terminalCompanyDTO.setCommitent(false);
                    terminalCompanyDTO.setPermissions(null);
                    terminalCompanyDTO.setBalances(null);
                    terminalCompanyDTO.setFreeTrial(false);
                    terminalCompanyDTO.setTrialDate(null);
                    terminalCompanyDTO.setApayTariffId(null);
                    return terminalCompanyDTO;
                })
                .orElseThrow(notFoundExceptionThrow("Company", "id", id));
    }

    public List<TerminalActivityTypeDTO> getActivityTypes(Long parentId) {
        List<ActivityType> activityTypes = parentId != null
                ? activityTypeRepository.findAllByParentIdAndDeletedIsFalse(parentId)
                : activityTypeRepository.findAll();
        return activityTypes
                .stream()
                .map(activityType -> new TerminalActivityTypeDTO(activityType.getId(), activityType.getName(), parentId))
                .collect(Collectors.toList());
    }

    public List<TerminalCityDTO> getCityList(Long regionId) {
        return districtRepository
                .findAllByRegionId(regionId)
                .stream()
                .map(district ->
                        new TerminalCityDTO(
                                district.getId(),
                                Long.valueOf(district.getCode()),
                                district.getRegion().getId(),
                                district.getNameRu(),
                                district.getNameUz()
                        )
                )
                .collect(Collectors.toList());
    }

    public List<TerminalRegionDTO> getRegions() {
        return regionRepository
                .findAll()
                .stream()
                .map(region -> new TerminalRegionDTO(region.getId(), Long.valueOf(region.getCode()), region.getNameRu(), region.getNameUz()))
                .collect(Collectors.toList());
    }

    public TerminalBranchDTO getBranch() {
        String serialNumber = getCurrentTerminalSerialNumber();
        Terminal terminal = terminalRepository.findFirstBySerialNumberAndDeletedIsFalse(serialNumber).orElseThrow(notFoundExceptionThrow(Terminal.class.getSimpleName(), "serial number", serialNumber));
        if (terminal.getBranch() == null) {
            localizationService.localize("terminal.not.registered");
        }
        if (HeaderUtils.isBusinessOwner()) {
            if (!Objects.equals(HeaderUtils.getCurrentCompanyId(), terminal.getBranch().getCompanyId())) {
                throw badRequestExceptionThrow(localizationService.localize("company.not.owner.kkm")).get();
            }
        }
        if (HeaderUtils.isCashier()) {
            if (!Objects.equals(HeaderUtils.getCurrentBranchId(), terminal.getBranchId())) {
                throw badRequestExceptionThrow(localizationService.localize("branch.not.owner.kkm")).get();
            }
        }

        return this.getBranch(terminal.getBranch());
    }

    public TerminalBranchDTO getBranch(Long id) {
        return branchRepository.findById(id).map(this::getBranch).orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }

    public TerminalBranchDTO getBranch(Branch branch) {

        TerminalBranchDTO branchDTO = new TerminalBranchDTO();
        branchDTO.setId(branch.getId());
        branchDTO.setCompanyId(branch.getCompanyId());
        branchDTO.setCompanyName(branch.getCompany().getName());
        branchDTO.setName(branch.getName());
        if (branch.getAddress() != null) {
            branchDTO.setAddress(branch.getAddress().getStreet());
            if (branch.getAddress().getRegion() != null) {
                TerminalRegionDTO terminalRegionDTO = new TerminalRegionDTO();
                terminalRegionDTO.setId(branch.getAddress().getRegion().getId());
                terminalRegionDTO.setCode(Long.valueOf(branch.getAddress().getRegion().getCode()));
                terminalRegionDTO.setNameUz(branch.getAddress().getRegion().getNameUz());
                terminalRegionDTO.setNameRu(branch.getAddress().getRegion().getNameRu());
                branchDTO.setRegion(terminalRegionDTO);
            }
            if (branch.getAddress().getDistrict() != null) {
                TerminalCityDTO terminalCityDTO = new TerminalCityDTO();
                terminalCityDTO.setId(branch.getAddress().getDistrict().getId());
                terminalCityDTO.setCode(Long.valueOf(branch.getAddress().getDistrict().getCode()));
                terminalCityDTO.setNameUz(branch.getAddress().getDistrict().getNameUz());
                terminalCityDTO.setNameRu(branch.getAddress().getDistrict().getNameRu());
                branchDTO.setCity(terminalCityDTO);
            }
            branchDTO.setLongitude(branch.getAddress().getLongitude() != null ? branch.getAddress().getLongitude().toString() : null);
            branchDTO.setLatitude(branch.getAddress().getLatitude() != null ? branch.getAddress().getLatitude().toString() : null);
        }
        branchDTO.setDeleted(false);
        branchDTO.setReasonForDeletion(null);
        branchDTO.setDescription(null);
        branchDTO.setFiscal(false);
        branchDTO.setUsersCount(1);
        /*if (CollectionUtils.isNotEmpty(getTerminals())) {
            branchDTO.setTerminalSerialNumbers(getTerminals().stream().map(Terminal::getSerialNumber).collect(Collectors.toList()));
        }*/
        //branchDTO.setSerialNumber(getSerialNumber());
        branchDTO.setCashboxCreatedDate(null);
        //branchDTO.setOfdId(getOfdId());
        //branchDTO.setOfdStatus(getOfdStatus());
        //if (getOfdStatus() != null) {
        //    branchDTO.setOfdStatusName(getOfdStatus().getNameRu());
        //}
        //branchDTO.setCadastreFileId(getCadastreFileId());
        //if (getCadastreFile() != null) {
        //     branchDTO.setCadastreFileUrl(getCadastreFile().getUrl());
        //}
        //branchDTO.setRentFileId(getRentFileId());
        //if (getRentFile() != null) {
        //    branchDTO.setRentFileUrl(getRentFile().getUrl());
        //}
        //branchDTO.setApayStatus(getApayStatus());
        return branchDTO;
    }

    public boolean checkTerminal(TerminalDTO dto) {
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName()));
        return false;
    }

    public CtsFiscalizationCheckDeviceDTO fiscalCheck(CtsFiscalCheckDTO ctsFiscalCheckDTO) {
        return ctsClient.fiscalCheck(ctsFiscalCheckDTO);
    }

    public List<TerminalCardTypeDto> getCardTypes() {
        return cardTypeRepository.findAll()
                .stream().map(cardType -> new TerminalCardTypeDto(cardType.getPrefix()))
                .collect(Collectors.toList());
    }

    public List<TerminalCategoryDTO> getEnabledCategories(Long branchId) {
        BaseFilter filter = new BaseFilter();
        filter.setBranchId(branchId != null ? branchId : getCurrentBranchId());
        ResultList<Category> resultList = categoryRepository.getResultList(filter);
        return convertToTree(resultList.getList());
    }

    private List<TerminalCategoryDTO> convertToTree(List<Category> categories) {
        Map<Long, TerminalCategoryDTO> map = new LinkedHashMap<>();
        List<TerminalCategoryDTO> result = new ArrayList<>();

        for (Category category : categories) {
            map.put(category.getId(), this.toTerminalCategoryDTO(category));
        }

        categories.forEach(category -> {
            TerminalCategoryDTO parentCategory = map.get(category.getParentId());
            if (parentCategory == null) {
                result.add(this.toTerminalCategoryDTO(category));
                return;
            }
            parentCategory.getChildren().add(this.toTerminalCategoryDTO(category));
        });

        result.forEach(category -> sumProductCount(category, category.getChildren()));
        return result;
    }

    private TerminalCategoryDTO toTerminalCategoryDTO(Category category) {
        TerminalCategoryDTO terminalCategoryDTO = new TerminalCategoryDTO();
        terminalCategoryDTO.setId(category.getId());
        terminalCategoryDTO.setName(category.getName());
        terminalCategoryDTO.setParentId(category.getParentId());
        terminalCategoryDTO.setEnabled(true);
        terminalCategoryDTO.setBranchId(category.getBranchId());
        return terminalCategoryDTO;
    }

    private Long sumProductCount(TerminalCategoryDTO category, List<TerminalCategoryDTO> children) {
        Long sum = category.getProductCount();
        for (TerminalCategoryDTO child : children) {
            sum = sum + sumProductCount(child, child.getChildren());
        }
        category.setProductCount(sum);
        return sum;
    }

    public boolean checkTerminalLogRequired(String serialNumber) {
        return terminalLogRepository.existsBySerialNumber(serialNumber);
    }

    public void updateTerminalLog(String serialNumber, MultipartFile dbFile) {
        TerminalLog terminalLog = terminalLogRepository.findFirstBySerialNumber(serialNumber).orElseThrow(notFoundExceptionThrow(TerminalLog.class.getSimpleName(), "serialNumber", serialNumber));
        if (dbFile == null) {
            throw notFoundExceptionThrow("Terminal dataBase file not found").get();
        }
        FileUpload oldFile = terminalLog.getDbFile();
        FileUpload db = amazonS3Service.publicUpload(dbFile);
        terminalLog.setDbFileId(db.getId());
        terminalLogRepository.save(terminalLog);
        if (oldFile != null) {
            amazonS3Service.delete(oldFile.getId());
        }
    }

    public TxkmResponseDto getTxkmInfoByDevice(Integer pageNo, Integer pageSize, String terminalSerialNumber) {
        return ctsClient.getTxkmInfoByDevice(pageNo, pageSize, terminalSerialNumber);
    }

    public void sendLogsToKafka(String terminalSN, List<TerminalLogToKafkaDto> terminalLogs) {
        terminalLogs.forEach(terminalLogDto -> {
            terminalLogDto.setSerialNumber(terminalSN);
            try {
                kafkaTemplate.send(KafkaConstants.TERMINAL_LOG_TOPIC, objectMapper.writeValueAsString(terminalLogDto));
            } catch (JsonProcessingException e) {
                log.error("Error occurred sending terminal log to kafka");
                log.error(e.getMessage());
            }
        });
    }

    public void addToFavorites(Long id, Long branchId) {
        productService.favorite(id);
    }

    public Page<TerminalProductDTO> getProductList(ProductFilter filter) {
        ResultList<Product> resultList = productRepository.getResultList(filter);
        return new PageImpl<>(convertToTerminalProductDTO(resultList.getList()), filter.getSortedPageable(), resultList.getCount());
    }

    public List<TerminalProductDTO> convertToTerminalProductDTO(List<Product> products) {
        return products.stream().map(product -> {
            TerminalProductDTO terminalProductDTO = new TerminalProductDTO();
            terminalProductDTO.setId(product.getId());
            if (product.getCreator() != null) {
                terminalProductDTO.setCreatedBy(product.getCreator().getName());
            }
            terminalProductDTO.setCreatedDate(product.getCreatedDate());
            if (product.getUpdater() != null) {
                terminalProductDTO.setLastModifiedBy(product.getUpdater().getName());
            }
            terminalProductDTO.setLastModifiedDate(product.getUpdatedDate());
            terminalProductDTO.setName(product.getName());
            terminalProductDTO.setBarcode(product.getBarcode());
            terminalProductDTO.setVatBarcode(product.getCatalogCode());
            if (product.getUnit() != null) {
                terminalProductDTO.setMeasurement(product.getUnit().getName());
            }
            terminalProductDTO.setDescription(product.getDescription());
            if (product.getVat() != null) {
                terminalProductDTO.setVatRate(product.getVat().getAmount());
            }
            terminalProductDTO.setHasMark(product.isHasMark());
            terminalProductDTO.setFavorite(product.isFavourite());
            terminalProductDTO.setNoVat(!product.hasVat());
            if (product.getUnit() != null) {
                TerminalUnitDTO terminalUnitDTO = new TerminalUnitDTO();
                terminalUnitDTO.setId(product.getUnitId());
                terminalUnitDTO.setName(product.getUnit().getName());
                terminalUnitDTO.setNameRu(product.getUnit().getNameRu());
                terminalUnitDTO.setNameUz(product.getUnit().getNameUz());
                terminalUnitDTO.setDescription(product.getUnit().getDescription());
                terminalUnitDTO.setCode(Long.valueOf(product.getUnit().getCode()));
                terminalUnitDTO.setCountable(product.getUnit().isCountable());
                terminalProductDTO.setUnit(terminalUnitDTO);
            }
            if (product.getCategory() != null) {
                TerminalCategoryDTO terminalCategoryDTO = new TerminalCategoryDTO();
                terminalCategoryDTO.setId(product.getCategoryId());
                terminalCategoryDTO.setName(product.getCategory().getName());
                terminalCategoryDTO.setParentId(product.getCategory().getParentId());
                terminalProductDTO.setCategoryDTO(terminalCategoryDTO);
            }
            terminalProductDTO.setSalesPrice(product.getPrice());
            terminalProductDTO.setBranchId(product.getBranchId());
            terminalProductDTO.setBranchId(product.getBranchId());
            return terminalProductDTO;
        }).collect(Collectors.toList());
    }

    public TerminalReceiptTemplateDTO getTerminalReceiptTemplate() {
        return Optional.ofNullable(getCurrentCompanyId())
                .flatMap(terminalReceiptTemplateRepository::findDistinctTopByCompanyId)
                .map(terminalReceiptTemplate -> {
                        TerminalReceiptTemplateDTO receiptTemplateDTO = new TerminalReceiptTemplateDTO();
                        receiptTemplateDTO.setId(terminalReceiptTemplate.getId());
                        receiptTemplateDTO.setCompanyId(terminalReceiptTemplate.getCompanyId());
                        if (terminalReceiptTemplate.getCompanyId() != null) {
                            receiptTemplateDTO.setCompanyName(terminalReceiptTemplate.getCompany().getName());
                        }

                        receiptTemplateDTO.setHeaderText(terminalReceiptTemplate.getHeaderText());
                        receiptTemplateDTO.setHeaderImage(terminalReceiptTemplate.getHeaderImage());
                        receiptTemplateDTO.setHeaderImageContentType(terminalReceiptTemplate.getHeaderImageContentType());
                        receiptTemplateDTO.setHeaderAlignment(terminalReceiptTemplate.getHeaderAlignment());

                        receiptTemplateDTO.setFooterText(terminalReceiptTemplate.getFooterText());
                        receiptTemplateDTO.setFooterImage(terminalReceiptTemplate.getFooterImage());
                        receiptTemplateDTO.setFooterImageContentType(terminalReceiptTemplate.getFooterImageContentType());
                        receiptTemplateDTO.setFooterAlignment(terminalReceiptTemplate.getFooterAlignment());
                        receiptTemplateDTO.setFooterId(terminalReceiptTemplate.getFooterId());
                        if (terminalReceiptTemplate.getFooterFile() != null) {
                            receiptTemplateDTO.setFooterImageUrl(terminalReceiptTemplate.getFooterFile().getUrl());
                        }
                        receiptTemplateDTO.setHeaderId(terminalReceiptTemplate.getHeaderId());
                        if (terminalReceiptTemplate.getHeaderFile() != null) {
                            receiptTemplateDTO.setHeaderImageUrl(terminalReceiptTemplate.getHeaderFile().getUrl());
                        }

                        return receiptTemplateDTO;
                })
                .orElseThrow(notFoundExceptionThrow(TerminalReceiptTemplate.class.getSimpleName(), "companyId", getCurrentCompanyId()));
    }

    public void saveReceiptList(List<TerminalReceiptDTO> receiptList) {
        if (!CollectionUtils.isEmpty(receiptList)) {
            try {
                log.info(" RECEIPT LIST FROM TERMINAL: \n{}", objectMapper.writeValueAsString(receiptList));
            } catch (JsonProcessingException e) {
                log.info("Writing object error: {}", e.getMessage());
            }
            final Long currentBranchId = getCurrentBranchId();
            final Long currentCompanyId = getCurrentCompanyId();
            final String terminalSN = HeaderUtils.getCurrentTerminalSnByHeader();
            Set<String> uidList = receiptList.stream().map(TerminalReceiptDTO::getUid).collect(Collectors.toSet());
            uidList = receiptRepository.getAlreadyUsedUidList(uidList);
            for (TerminalReceiptDTO receiptDTO : receiptList) {
                if (uidList.contains(receiptDTO.getUid())) {
                    continue;
                }
                receiptDTO.setCompanyId(currentCompanyId);
                if (receiptDTO.getBranchId() == null) {
                    receiptDTO.setBranchId(currentBranchId);
                }
                if (StringUtils.isEmpty(receiptDTO.getTerminalSN())) {
                    receiptDTO.setTerminalSN(terminalSN);
                }
                receiptDTO.setFromBatch(true);

                receiptDTO.setUserId(receiptDTO.getUser() == null ?
                        receiptDTO.getUserId() == null ?
                                getCurrentUserId() : receiptDTO.getUserId() : receiptDTO.getUser().getId());

                rabbitMqTerminalReceiptProducer.sendToQueue(receiptDTO);
            }
        }
    }


    public List<TerminalReceiptDTO> getDraftReceipts() {
        String serialNumber = getCurrentTerminalSerialNumber();
        Terminal terminal = terminalRepository.findFirstBySerialNumberAndDeletedIsFalse(serialNumber)
                .orElseThrow(notFoundExceptionThrow(Terminal.class.getSimpleName(), "serialNumber", serialNumber));
        if (terminal.getBranch() == null || !terminal.getBranch().getCompanyId().equals(getCurrentCompanyId())) {
            return new ArrayList<>();
        }
        ReceiptFilter filter = new ReceiptFilter();
        filter.setTerminalId(terminal.getId());
        filter.setStatus(ReceiptStatus.DRAFT.getCode());
        /*return receiptRepository.getResultList(filter).getList().stream().map(Receipt::getDTO).collect(Collectors.toList());*/
        return null;
    }

    public TerminalReceiptDTO getReceiptByFiscalUrl(String fiscalUrl) {
        return receiptRepository.findFirstByFiscalUrlAndDeletedIsFalse(fiscalUrl)
                .map(receipt -> receiptDetailRepository.findFirstByReceiptId(receipt.getId()).map(receiptDetail -> getTerminalReceiptDTO(receipt,false, receiptDetail))
                        .orElseThrow(notFoundExceptionThrow(ReceiptDetail.class.getSimpleName(), "receiptId", receipt.getId()))).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "fiscalUrl", fiscalUrl));
    }

    public TerminalReceiptDTO getReceiptByUID(String uid) {
        return receiptRepository.findFirstByUidAndDeletedIsFalse(uid)
                .map(receipt -> receiptDetailRepository.findFirstByReceiptId(receipt.getId()).map(receiptDetail -> getTerminalReceiptDTO(receipt,false, receiptDetail))
                        .orElseThrow(notFoundExceptionThrow(ReceiptDetail.class.getSimpleName(), "receiptId", receipt.getId()))).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "uid", uid));
    }

    public TerminalReceiptDTO getByOrder(Long orderId) {
        return receiptRepository.findFirstByOrderIdAndDeletedIsFalse(orderId)
                .map(receipt -> getTerminalReceiptDTO(receipt, false, receiptDetailRepository.findFirstByReceiptId(receipt.getId()).orElseThrow(notFoundExceptionThrow(ReceiptDetail.class.getSimpleName(), "receiptId", receipt.getId()))))
                .orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "orderId", orderId));
    }

    public void deleteReceiptByUID(String uid) {
        receiptRepository.findFirstByUidAndDeletedIsFalse(uid)
                .map(receipt -> {
                    if (!ReceiptStatus.DRAFT.equals(receipt.getStatus())) {
                        throw badRequestExceptionThrow("Receipt with status DRAFT can be deleted").get();
                    }
                    receipt.setDeleted(true);
                    receiptRepository.save(receipt);
                    return receipt;
                })
                .orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "uid", uid));
    }

    public Page<TerminalReceiptDTO> getReceiptList(ReceiptFilter receiptFilter) {
        String serialNumber = getCurrentTerminalSerialNumber();
        Terminal terminal = terminalRepository.findFirstBySerialNumberAndDeletedIsFalse(serialNumber)
                .orElseThrow(notFoundExceptionThrow(Terminal.class.getSimpleName(), "serialNumber", serialNumber));
        if (terminal.getBranch() == null || !terminal.getBranch().getCompanyId().equals(getCurrentCompanyId())) {
            return new PageImpl<>(new ArrayList<>(), receiptFilter.getSortedPageable(), 0L);
        }
        if (!isBusinessOwner()) {
            receiptFilter.setBranchId(getCurrentBranchId());
        }
//        Page<Receipt> resultList = receiptRepository.getResultList(receiptFilter);
        /*return new PageImpl<>(resultList.getList().stream().map(Receipt::getDTO).collect(Collectors.toList()), receiptFilter.getSortedPageable(), resultList.getCount());*/
        return null;
    }

    public void sendReceiptDetailViaSms(TerminalReceiptSmsRequestDTO request) {
        Receipt receipt;
        if (request.getReceiptId() != null) {
            receipt = receiptRepository.findById(request.getReceiptId()).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "id", request.getReceiptId()));
        } else if (StringUtils.isNotBlank(request.getUid())) {
            receipt = receiptRepository.findFirstByUidAndDeletedIsFalse(request.getUid()).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "uid", request.getUid()));
        } else if (StringUtils.isNotBlank(request.getFiscalUrl())) {
            receipt = receiptRepository.findFirstByFiscalUrlAndDeletedIsFalse(request.getFiscalUrl()).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "fislcalUrl", request.getFiscalUrl()));
        } else {
            throw badRequestExceptionThrow("Идентификатор или UID является обязательным").get();
        }
        if (StringUtils.isBlank(receipt.getFiscalUrl())) {
            throw badRequestExceptionThrow("Чек не фискальный").get();
        }
        Company company = receipt.getCompany();
        if (company == null) {
            throw notFoundExceptionThrow(Company.class.getSimpleName(), "receiptId", receipt.getId()).get();
        }
        sendSms(receipt, company, request.getPhone(), request.getLanguage());
    }


    private void sendSms(Receipt receipt, final Company company, final String phone, final String language) {

        StringBuilder content = new StringBuilder();
        if (LanguageEnum.uz.getCode().equals(language)) {
            if (receipt.getStatus().equals(ReceiptStatus.PAID)) {
                content.append(company.getName()).append(" \nTo'lov: ").append(receipt.getTotalPaid().setScale(2)).append(" so'm \n");
            }
            content.append("https://internal.example.com/").append(Base64.getEncoder().encodeToString(receipt.getUid().getBytes())).append(" \n \n50 000 sovg'aga! ").append("\nA-Pay ilovasini yuklab oling  https://getapp.a-pay.uz");
        } else {
            if (receipt.getStatus().equals(ReceiptStatus.PAID)) {
                content.append(company.getName()).append(" \nOplata: ").append(receipt.getTotalPaid().setScale(2)).append(" sum \n");
            }
            content.append("https://internal.example.com/").append(Base64.getEncoder().encodeToString(receipt.getUid().getBytes())).append(" \n \nDarim 50 000! ").append("\nSkachayte prilojeniye A-Pay https://getapp.a-pay.uz");
        }
        if (StringUtils.isNotBlank(phone)) {
            smsService.send(SmsRequestDTO.builder().phone(phone).code(content.toString()).build());
        }
    }

    public void createShift(TerminalShiftReportDTO shiftReportDTO) {
        Terminal terminal = terminalRepository.findFirstBySerialNumberAndDeletedIsFalse(getCurrentTerminalSerialNumber())
                .orElseThrow(notFoundExceptionThrow(Terminal.class.getSimpleName(), "serialNumber", getCurrentTerminalSerialNumber()));
        TerminalShiftReport report = terminalShiftReportRepository.findFirstByTerminalIdAndNumberAndEndDateTime(terminal.getId(),
                shiftReportDTO.getFiscalNumber(), shiftReportDTO.getEndDateTime()).orElseGet(TerminalShiftReport::new);
        if (report.getId() != null) {
            return;
        }
        report.setNumber(shiftReportDTO.getFiscalNumber());
        report.setStartDateTime(shiftReportDTO.getStartDateTime());
        report.setEndDateTime(shiftReportDTO.getEndDateTime());
        report.setReceipts(shiftReportDTO.getReceipts());
        report.setReceiptsReturned(shiftReportDTO.getReceiptsReturned());
        report.setCard(shiftReportDTO.getTotalCard());
        report.setCardReturned(shiftReportDTO.getTotalCardReturned());
        report.setCash(shiftReportDTO.getTotalCash());
        report.setCashReturned(shiftReportDTO.getTotalCashReturned());
        report.setNDS(shiftReportDTO.getNDS());
        report.setNdsReturned(shiftReportDTO.getNdsReturned());
        report.setUserId(shiftReportDTO.getUserId());
        report.setTerminal(terminal);
        report.setTerminalId(terminal.getId());
        report.setBranch(terminal.getBranch());
        report.setBranchId(terminal.getBranchId());
        terminalShiftReportRepository.save(report);
    }
}
