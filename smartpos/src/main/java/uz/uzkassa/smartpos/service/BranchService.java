package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Neighborhood;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.domain.postgres.company.*;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.Authority;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.*;
import uz.uzkassa.smartpos.dto.application.OfdStatusEnum;
import uz.uzkassa.smartpos.dto.branch.BranchDTO;
import uz.uzkassa.smartpos.dto.branch.BranchListDTO;
import uz.uzkassa.smartpos.dto.branch.BranchListMapDTO;
import uz.uzkassa.smartpos.dto.branch.CabinetBranchCreateDto;
import uz.uzkassa.smartpos.dto.customerreceipts.BranchReceiptsDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.BranchReceiptsStatDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CompanyReceiptsStatDTO;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.CompanyFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.enums.CompanyType;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityAlreadyExistException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.filter.BranchFilter;
import uz.uzkassa.smartpos.integration.cto.*;
import uz.uzkassa.smartpos.integration.ofd.OfdCustomerBranchAddUpdateDTO;
import uz.uzkassa.smartpos.integration.ofd.OfdCustomerBranchResponseDTO;
import uz.uzkassa.smartpos.integration.ofd.OfdFileDTO;
import uz.uzkassa.smartpos.integration.supply.SupplierBranchWithAddressDTO;
import uz.uzkassa.smartpos.mappers.BranchMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptReportByBranch;
import uz.uzkassa.smartpos.repository.postgres.FileUploadRepository;
import uz.uzkassa.smartpos.repository.postgres.common.ActivityTypeRepository;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.common.NeighborhoodRepository;
import uz.uzkassa.smartpos.repository.postgres.common.RegionRepository;
import uz.uzkassa.smartpos.repository.postgres.company.BranchGroupRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.repository.postgres.user.AuthorityRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.utils.RandomUtil;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService extends SmartposService {
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final BranchGroupRepository branchGroupRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final FileUploadRepository fileUploadRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final ClickhouseReceiptReportByBranch clickhouseReceiptReportByBranch;
    private final RabbitMqProducer rabbitMqProducer;
    private final EtcSmsService smsService;
    private final BranchMapper branchMapper;
    private final NeighborhoodRepository neighborhoodRepository;
    private final Environment env;
    private final DeviceRepository deviceRepository;
    private final CtoClient ctoClient;
    private final TokenStoreService tokenStoreService;

    @Transactional
    public Long createNew(final CabinetBranchCreateDto dto) {
        final Long companyId = SecurityUtils.getCurrentCompanyId();
        final Region region = regionRepository.findById(dto.region().id()).orElseThrow(notFound(localizationService.getMessage("region")));
        final City city = cityRepository.findById(dto.city().id()).orElseThrow(notFound(localizationService.getMessage("city")));
        final Neighborhood neighborhood = neighborhoodRepository.findById(dto.neighborhood().id()).orElseThrow(notFound(localizationService.getMessage("neighborhood")));
        final ActivityType activityType = activityTypeRepository.findById(dto.activityTypeDTO().id()).orElseThrow(notFound(localizationService.getMessage("activityType")));
        final Company company = companyRepository.findById(companyId).orElseThrow(notFound(localizationService.getMessage("company")));
        final FileUpload file;
        if (dto.rentFileId() != null) {
            file = fileUploadRepository.findById(dto.rentFileId()).orElseThrow(notFound(localizationService.getMessage("file")));
        } else {
            file = fileUploadRepository.findById(dto.cadastreFileId()).orElseThrow(notFound(localizationService.getMessage("file")));
        }

        if (isAdminCompany(company)) {
            throw new BadRequestException(localizationService.getMessage("admin.company.branch.creation.not.allowed"));
        }
        branchRepository.findByNameEqualsIgnoreCaseAndCompanyIdAndDeletedIsFalse(dto.name(), companyId).ifPresent((branch) -> {
            throw new EntityAlreadyExistException(localizationService.getMessage("branch.exists.in.company"));
        });
        Branch branch = branchMapper.toEntity(dto);
        branch.setCompanyId(companyId);
        branch.setCompany(company);
        branch.setRegionId(dto.region().id());
        branch.setRegion(region);
        branch.setCityId(dto.city().id());
        branch.setCity(city);
        branch.setNeighborhoodId(dto.neighborhood().id());
        branch.setNeighborhood(neighborhood);
        branch.setActivityTypeId(dto.activityTypeDTO().id());
        branch.setActivityType(activityType);
        branch.setRentFileId(dto.rentFileId());
        branch.setRentFile(file);
        branch.setCadastreFileId(dto.cadastreFileId());
        branch.setCadastreFile(file);
        if (Arrays.stream(env.getActiveProfiles()).noneMatch(s -> s.equals("dev"))) {
            sendCustomerBranch(branch, false);
        }
        branch = branchRepository.save(branch);
        rabbitMqProducer.sendWithDelayTime(
                new CompanyRegisterBranchesDTO(companyId, Collections.singletonList(branch.getId())),
                MqConstants.BRANCH_REGISTER_WITH_APAY, 3000L);
        if (company.getCompanyType() != null) {
            if (CompanyType.HOME_SERVICE == company.getCompanyType()) {
                rabbitMqProducer.sendWithDelayTime(branch.getId(),
                        MqConstants.CREATE_HOME_SERVICE_PRODUCT, 3000L);
            } else if (CompanyType.FASHION == company.getCompanyType()) {
                rabbitMqProducer.sendWithDelayTime(branch.getId(),
                        MqConstants.CREATE_FASHION_CATALOG, 3000L);
            }
        }
        FileUpload fileUpload;
        if (branch.getRentFile() != null) {
            fileUpload = fileUploadRepository.getReferenceById(branch.getRentFileId());
        } else {
            fileUpload = fileUploadRepository.getReferenceById(branch.getCadastreFileId());
        }
        fileUpload.setEntityType(EntityType.BRANCH);
        fileUpload.setEntityId(branch.getId());
        fileUploadRepository.save(fileUpload);
        return branch.getId();
    }

    @Transactional
    public void update(CabinetBranchCreateDto dto) throws EntityNotFound {
        Branch branch = branchRepository.findById(dto.id())
                .orElseThrow(notFound(localizationService.getMessage("branch"), dto.id()));
        if (branch.isFiscal()) {
            throw badRequest(localizationService.getMessage("fiscal.branch.edit")).get();
        }
        Company company = companyRepository.findById(branch.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if (isAdminCompany(company)) {
            throw new BadRequestException(localizationService.getMessage("admin.company.branch.creation.not.allowed"));
        }
        branchMapper.partialUpdate(dto, branch);
        branchRepository.save(branch);
        rabbitMqProducer.send(new CompanyRegisterBranchesDTO(branch.getCompanyId(), Collections.singletonList(branch.getId())), MqConstants.BRANCH_REGISTER_WITH_APAY);

    }


    public List<BranchListMapDTO> getBranchesForMap(CompanyFilter filter) {
        return branchRepository.getBranchesForMap(filter);
    }

    @Transactional
    public void deleteBranch(BranchDTO branchDTO) throws EntityNotFound {
        Long id = branchDTO.getId();
        User user = userRepository.getReferenceById(getCurrentUserId());
        String currentEncryptedPassword = user.getPassword();
        if (!passwordEncoder.matches(branchDTO.getPassword(), currentEncryptedPassword)) {
            throw badRequest(localizationService.getMessage("error.password")).get();
        }
        Branch branch = branchRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("branch"), id));
        if (!getCurrentCompanyId().equals(branch.getCompanyId())) {
            throw badRequest(localizationService.getMessage("error.authorized")).get();
        }
        if (branch.isFiscal()) {
            throw badRequest(localizationService.getMessage("fiscal.branch.delete")).get();
        }
        Company company = companyRepository.findById(branch.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if (isAdminCompany(company)) {
            throw new BadRequestException(localizationService.getMessage("admin.company.branch.deleted.not.allowed"));
        }
        branch.setDeleted(true);
        branchRepository.save(branch);
    }

    @Transactional
    public void delete(Long id) {
        Branch branch = branchRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("branch"), id));
        if (!getCurrentCompanyId().equals(branch.getCompanyId())) {
            throw badRequest(localizationService.getMessage("error.authorized")).get();
        }
        if (branch.isFiscal()) {
            throw badRequest(localizationService.getMessage("fiscal.branch.delete")).get();
        }
        Company company = companyRepository.findById(branch.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if (isAdminCompany(company)) {
            throw new BadRequestException(localizationService.getMessage("admin.company.branch.deleted.not.allowed"));
        }
        branch.setDeleted(true);
        branch.setGroups(null);
        branchRepository.save(branch);
    }

    @Transactional
    public void forceDelete(Long id) {
        Branch branch = branchRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("branch"), id));
        branch.setDeleted(true);
        branch.setFiscal(false);
        branch.setSerialNumber(null);
        branchRepository.save(branch);
    }

    @Transactional
    public void deleteWebkassa(Long branchId) {
        Branch branch = branchRepository.findById(branchId).orElseThrow(notFound(localizationService.getMessage("branch"), branchId));
        branch.setSerialNumber(null);
        branch.setCashboxCreatedDate(null);
        branchRepository.save(branch);
    }

    public BranchDTO getBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("branch"), id));
        return branchMapper.toDto(branch);
    }

    public Page<BranchListDTO> findAll(BranchFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (!isOwner()) {
            if (isBranchAdmin()) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(List.of(groupId)),
                                () -> filter.setBranchIds(List.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(List.of(SecurityUtils.getCurrentBranchId()));
            }
        }
        return branchRepository.findAllByFilter(filter);
    }

    public List<BranchSelectItem> search(BranchFilter filter) {
        if (!filter.isForAdmin()) {
            filter.setCompanyId(getCurrentCompanyId());
            if (isBranchAdmin()) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(List.of(groupId)),
                                () -> filter.setBranchIds(List.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(List.of(getCurrentBranchId()));
            }
        }
        return branchRepository.lookup(filter);
    }


    public List<BranchSelectItem> searchForTransfer(BranchFilter filter) {
        return branchRepository.lookup(filter);
    }

    public Page<BranchReceiptsDTO> getReceiptsAmountByBranch(CustomerReceiptsFilter filter) {
        Page<BranchReceiptsDTO> result = clickhouseReceiptReportByBranch.getReceiptsAmountByBranch(filter);
        if (result != null && !result.getContent().isEmpty()) {
            Map<Long, BranchReceiptsDTO> map = result.getContent().stream().collect(Collectors.toMap(BranchReceiptsDTO::getId, Function.identity()));
            for (Branch branch : branchRepository.findAllById(map.keySet())) {
                if (map.containsKey(branch.getId())) {
                    map.get(branch.getId()).setActivityType(branch.getActivityType() != null ? branch.getActivityType().getNameRu() : null);
                    map.get(branch.getId()).setCompanyName(branch.getCompanyId() != null ? branch.getCompany().getName() : null);
                    map.get(branch.getId()).setRegionName(branch.getRegionId() != null ? branch.getRegion().getNameRu() : null);
                    map.get(branch.getId()).setCityName(branch.getCityId() != null ? branch.getCity().getNameRu() : null);
                    map.get(branch.getId()).setBranchName(branch.getName());
                }
            }
        }
        return result;
    }

    public BranchReceiptsStatDTO getReceiptsAmountByBranchStat(CustomerReceiptsFilter filter) {
        return clickhouseReceiptReportByBranch.getReceiptsAmountByBranchStat(filter);
    }

    public Page<AdminBranchDTO> getAdminBranches(CompanyFilter filter) {
        return branchRepository.findAdminBranches(filter, false);
    }

    public BranchDTO getCurrentBranch() {
        final String serial = getCurrentTerminalSnByHeader();
        if (StringUtils.isNotEmpty(serial)) {
            BranchDTO branchDTO = deviceRepository.findFirstBySerialNumberAndType(getCurrentTerminalSnByHeader(), DeviceType.FISCAL)
                    .map(Device::getBranch)
                    .filter(branch -> branch.getCompanyId().equals(getCurrentCompanyId()))
                    .map(branchMapper::toDto)
                    .orElseThrow(branchNotFound());
            if (!isOwner() && !Objects.equals(branchDTO.getId(), SecurityUtils.getCurrentBranchId())) {
                throw new BadRequestException(localizationService.getMessage("cashier.using.another.terminal"));
            }
            return branchDTO;
        } else {
            return branchRepository.findById(getCurrentBranchId())
                    .filter(branch -> branch.getCompanyId().equals(getCurrentCompanyId()))
                    .map(branchMapper::toDto)
                    .orElseThrow(branchNotFound());
        }
    }

    @Transactional
    public void registerBranches(CompanyRegisterBranchesDTO registerDTO) {
        final Long companyId = registerDTO.getCompanyId() != null ? registerDTO.getCompanyId() : getCurrentCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(notFound(localizationService.getMessage("company"), companyId));
        if (!company.getPermissions().getOrDefault(ServiceType.RECEIPT_FROM_INVOICE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) {
            throw badRequest(localizationService.getMessage("feature.not.allowed")).get();
        }
        List<Branch> branches = branchRepository.findAllByIdInAndCompanyId(registerDTO.getBranchIds(), companyId);
        if (CollectionUtils.isEmpty(branches)) {
            throw badRequest(localizationService.getMessage("branch.missing")).get();
        }
        companyService.sendToApay(new CompanyRegisterBranchesDTO(companyId, registerDTO.getBranchIds()));
        branches.forEach(branch -> {
            branch.setFiscal(true);
            if (StringUtils.isBlank(branch.getSerialNumber())) {
                branch.setSerialNumber(ServerUtils.generateVirtualSerialNumber(branchRepository.nextVal()));
                branch.setCashboxCreatedDate(LocalDateTime.now());
            }
        });
        branchRepository.saveAll(branches);
    }

    @Transactional
    public void registerCashbox(Long branchId) {
        registerBranches(new CompanyRegisterBranchesDTO(getCurrentCompanyId(), Collections.singletonList(branchId)));
    }

    @Transactional
    public BranchGroupDTO createBranchGroup(BranchGroupDTO dto) {
        BranchGroup entity = new BranchGroup();
        entity.setName(dto.getName());
        entity.setCompanyId(getCurrentCompanyId());
        branchGroupRepository.save(entity);
        if (CollectionUtils.isNotEmpty(dto.getBranches())) {
            List<Branch> branchList = branchRepository.findAllById(
                    dto.getBranches()
                            .stream()
                            .map(SelectItem::getId)
                            .collect(Collectors.toList()));
            entity.setBranches(new HashSet<>(branchList));
        }
        branchGroupRepository.save(entity);
        return new BranchGroupDTO(entity);
    }

    @Transactional
    public BranchGroupDTO updateBranchGroup(BranchGroupDTO dto) {
        BranchGroup entity = branchGroupRepository.getReferenceById(dto.getId());
        final List<Branch> branches = branchRepository.findAllById(dto.getBranches().stream().map(SelectItem::getId).toList());
        entity.setBranches(new HashSet<>(branches));
        entity.setName(dto.getName());
        branchGroupRepository.save(entity);
        return new BranchGroupDTO(entity);
    }

    public List<SelectItem> selectGroup(Long companyId, String searchKey) {
        BranchFilter filter = new BranchFilter();
        filter.setCompanyId(companyId != null ? companyId : getCurrentCompanyId());
        filter.setSearch(searchKey);
        if (!isOwner()) {
            Optional<Long> currentBranchGroupId = SecurityUtils.getCurrentBranchGroupId();
            if (currentBranchGroupId.isEmpty()) {
                return List.of();
            } else {
                filter.setBranchGroupIds(List.of(currentBranchGroupId.get()));
            }
        }
        return branchGroupRepository.findAll((Specification<BranchGroup>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get(BranchGroup_.companyId), filter.getCompanyId()));
            if (CollectionUtils.isNotEmpty(filter.getBranchGroupIds())) {
                predicates.add(root.get(BranchGroup_.id).in(filter.getBranchGroupIds()));
            }
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(BranchGroup_.name
                )), filter.getSearchForQuery()));
            }
            query.orderBy(criteriaBuilder.asc(root.get(BranchGroup_.name)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }).stream().map(branchGroup -> new SelectItem(branchGroup.getId(), branchGroup.getName())).collect(Collectors.toList());
    }

    public void deleteGroup(Long groupId) {
        BranchGroup entity = branchGroupRepository.findById(groupId).orElseThrow(notFound(localizationService.getMessage("group.branches")));
        branchGroupRepository.delete(entity);
    }


    public BranchGroupDTO findGroupById(Long groupId) {
        BranchGroup entity = branchGroupRepository.findById(groupId).orElseThrow(notFound(localizationService.getMessage("group.branches")));
        return new BranchGroupDTO(entity);
    }

    public void sendCustomerBranch(Branch customerBranch, boolean resend) {
        final Company customer = customerBranch.getCompany();
        final Region region = customerBranch.getRegion();
        final City city = customerBranch.getCity();
        final Neighborhood neighborhood = customerBranch.getNeighborhood();
        final ActivityType activityType = customerBranch.getActivityType();

        if (customerBranch.getOfdId() != null && !resend) {
            throw badRequest(localizationService.getMessage("branch.already.registration")).get();
        }
        if (StringUtils.isEmpty(customerBranch.getLongitude())) {
            throw badRequest(localizationService.getMessage(localizationService.getMessage("select.store.on.map"))).get();
        }
        if (StringUtils.isEmpty(customerBranch.getLatitude())) {
            throw badRequest(localizationService.getMessage("select.store.on.map")).get();
        }

        final List<OfdFileDTO> files = new ArrayList<>();

        if (customerBranch.getCadastreFileId() != null) {
            final FileUpload file = fileUploadRepository.getReferenceById(customerBranch.getCadastreFileId());
            try {
                files.add(new OfdFileDTO(file.getObjectName(), ServerUtils.fileToBase64(file.getUrl())));
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("Cadastre file parse error:{}", e.getMessage());
                String error = " Customer: " + customer.getInn() +
                        " TT: " + customerBranch.getName() +
                        " File: " + file.getObjectName();
                throw badRequest(error).get();
            }
        }
        if (customerBranch.getRentFileId() != null) {
            final FileUpload file = fileUploadRepository.getReferenceById(customerBranch.getRentFileId());
            try {
                files.add(new OfdFileDTO(file.getObjectName(), ServerUtils.fileToBase64(file.getUrl())));
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("Lease Contract file parse error:{}", e.getMessage());
                String error = " Customer: " + customer.getInn() +
                        " TT: " + customerBranch.getName() +
                        " File: " + file.getObjectName();
                throw badRequest(error).get();
            }
        }

        if (files.isEmpty()) {
            throw badRequest(localizationService.getMessage("files.not.found")).get();
        }

        OfdCustomerBranchAddUpdateDTO ofdCustomerBranchAddUpdateDTO = new OfdCustomerBranchAddUpdateDTO();
        ofdCustomerBranchAddUpdateDTO.setId(customerBranch.getOfdId());
        if (customer.getInn().length() == 9) {
            ofdCustomerBranchAddUpdateDTO.setTin(customer.getInn());
        } else {
            ofdCustomerBranchAddUpdateDTO.setPinfl(customer.getInn());
        }
        ofdCustomerBranchAddUpdateDTO.setName(customerBranch.getName());
        ofdCustomerBranchAddUpdateDTO.setAddress(customerBranch.getAddress());
        ofdCustomerBranchAddUpdateDTO.setRegionCode(region.getCode());
        ofdCustomerBranchAddUpdateDTO.setDistrictCode(city.getCode());
        ofdCustomerBranchAddUpdateDTO.setNeighborhoodCode(neighborhood.getCode());
        if (activityType.getParent() != null) {
            ofdCustomerBranchAddUpdateDTO.setActivityGroup(activityType.getParent().getCode());
        } else {
            ofdCustomerBranchAddUpdateDTO.setActivityGroup(activityType.getCode());
        }
        ofdCustomerBranchAddUpdateDTO.setActivityType(activityType.getCode());
        ofdCustomerBranchAddUpdateDTO.setLongitude(customerBranch.getLongitude());
        ofdCustomerBranchAddUpdateDTO.setLatitude(customerBranch.getLatitude());
//        ofdCustomerBranchAddUpdateDTO.setCadastreNumber(customerBranch.getCadastreNumber());
        ofdCustomerBranchAddUpdateDTO.setFiles(files);

        log.info("-------------------------------- Подтвердить ТТ --------------------------");
        log.info("Id: {}", ofdCustomerBranchAddUpdateDTO.getId());
        log.info("Activity Group: {}", ofdCustomerBranchAddUpdateDTO.getActivityGroup());
        log.info("Activity Type: {}", ofdCustomerBranchAddUpdateDTO.getActivityType());
        log.info("Address: {}", ofdCustomerBranchAddUpdateDTO.getAddress());
        log.info("Cadastre Number: {}", ofdCustomerBranchAddUpdateDTO.getCadastreNumber());
        log.info("Latitude: {}", ofdCustomerBranchAddUpdateDTO.getLatitude());
        log.info("Longitude: {}", ofdCustomerBranchAddUpdateDTO.getLongitude());
        log.info("Name: {}", ofdCustomerBranchAddUpdateDTO.getName());
        log.info("Tin: {}", ofdCustomerBranchAddUpdateDTO.getTin());
        log.info("District Code: {}", ofdCustomerBranchAddUpdateDTO.getDistrictCode());
        log.info("Region Code: {}", ofdCustomerBranchAddUpdateDTO.getRegionCode());
        log.info("File Size: {}", files.size());

        try {
            ResponseEntity<OfdCustomerBranchResponseDTO> response = ofdClient.addBranch(getOfdToken(), ofdCustomerBranchAddUpdateDTO);

            OfdCustomerBranchResponseDTO result = response.getBody();
            if (result != null && result.isSuccess() && result.getData() != null && result.getData().getId() != null) {
                customerBranch.setFiscal(true);
                customerBranch.setOfdId(result.getData().getId());
                customerBranch.setOfdStatus(result.getData().getStatus() == 1 ? OfdStatusEnum.ACTIVE : OfdStatusEnum.PENDING);
                log.info(">>>>>>>>>> End Send Customer Branch <<<<<<<<<<<<");
            } else {
                StringBuilder errorBuilder = new StringBuilder();
                if (result != null && result.getErrors() != null && !result.getErrors().isEmpty()) {
                    result.getErrors().forEach(errorDTO -> errorBuilder.append(errorDTO.getField()).append(" : ").append(errorDTO.getMessage()).append("\n"));
                } else {
                    if (result != null && StringUtils.isNotEmpty(result.getText())) {
                        errorBuilder.append(result.getText());
                    } else {
                        errorBuilder.append(localizationService.getMessage("validation.failed"));
                    }
                }
                throw new BadRequestException(localizationService.getMessage("branch.creation.OFD") + errorBuilder);
            }
        } catch (Exception e) {
            if (e instanceof BadRequestException) {
                throw new BadRequestException(e.getMessage(), ((BadRequestException) e).getErrorCode());
            }
            throw new BadRequestException(localizationService.getMessage("branch.creation.OFD"));
        }
    }

    public CompanyReceiptsStatDTO getBranchCountAndTerminalCountFromCompanyId(Long companyId) {
        List<Branch> branches = branchRepository.findByCompanyId(companyId);
        return new CompanyReceiptsStatDTO(CollectionUtils.size(branches),
                deviceRepository.companyTerminalsCount(companyId));
    }

    public void ctsWebhook(CtoBranchSyncDTO dto) {
        Company company = companyRepository.findFirstByCtoId(dto.getCustomer().getId()).orElseGet(() ->
                companyRepository.findOneByInn(dto.getCustomer().getTin()).orElseGet(Company::new)
        );
        CtoCompanyDTO customer = dto.getCustomer();
        company.setBillingType(customer.getAppType());
        company.setCtoId(customer.getId());
        company.setName(customer.getName());
        company.setInn(customer.getTin());
        company.setFiscal(true);
        company.setSyncDate(LocalDateTime.now());
        if (customer.getBusinessType() != null) {
            company.setBusinessType(customer.getBusinessType());
        }
        if (customer.getActivityTypeCode() != null && customer.getActivityTypeParentCode() != null) {
            company.getActivityTypes().clear();
            activityTypeRepository.findOneByCodeAndParent(customer.getActivityTypeCode(), customer.getActivityTypeParentCode())
                    .ifPresent(activityType -> company.getActivityTypes().add(activityType));
        }
        if (customer.getVat() != null) {
            company.setNdsPercent(customer.getVat());
        } else {
            company.setNdsPercent(null);
        }
        if (customer.getAddress() != null) {
            CtoAddressDTO address = customer.getAddress();
            if (address.getRegionCode() != null) {
                Optional<Region> region = regionRepository.findOneByCode(address.getRegionCode());
                if (region.isPresent()) {
                    company.setRegionId(region.get().getId());
                    if (address.getDistrictCode() != null) {
                        Optional<City> city = cityRepository.findOneByCodeAndRegionCode(address.getDistrictCode(), address.getRegionCode());
                        city.ifPresent(value -> company.setCityId(value.getId()));
                    }
                }
            }
            StringBuilder addressString = new StringBuilder();
            if (StringUtils.isNotBlank(address.getStreet())) {
                addressString.append(address.getStreet());
            }
            if (StringUtils.isNotBlank(address.getHouse())) {
                addressString.append(", ").append(address.getHouse());
            }
            if (StringUtils.isNotBlank(address.getApartment())) {
                addressString.append(", ").append(address.getApartment());
            }
            company.setAddress(addressString.toString());
        }


        // manager
        var managerDTO = dto.getCustomer().getManager();
        if (managerDTO != null) {
            User manager = userRepository.findOneByLogin(managerDTO.getLogin()).orElseGet(() -> {
                User user = new User();
                user.setLogin(managerDTO.getLogin());
                user.setPatronymic(managerDTO.getPatronymic());
                user.setFirstName(managerDTO.getFirstName());
                user.setLastName(managerDTO.getLastName());
                user.setBranchId(getMainBranchIdForAdminCompany());
                user.setCompanyId(getAdminCompanyId());
                user.setActivated(true);
                user.setDeleted(false);
                user.getAuthorities().addAll(Set.of(
                        authorityRepository.getReferenceById(AuthoritiesConstants.SALES_MANAGER),
                        authorityRepository.getReferenceById(AuthoritiesConstants.SUPPORT),
                        authorityRepository.getReferenceById(AuthoritiesConstants.SUPPORT_MANAGER))
                );
                final String s = RandomUtil.generateNumericKey();
                user.setPassword(passwordEncoder.encode(s));
                clearLoginCaches(managerDTO.getLogin());
                userRepository.save(user);
                return user;
            });
            company.setManagerId(manager.getId());
        }

        companyRepository.save(company);
        Optional<Branch> optionalBranch = branchRepository.findFirstByCtoId(dto.getId());
        if (optionalBranch.isEmpty() && dto.getOfdId() != null) {
            optionalBranch = branchRepository.findFirstByOfdId(dto.getOfdId());
        }
        if (optionalBranch.isEmpty()) {
            optionalBranch = branchRepository.findFirstByCompanyIdAndName(company.getId(), dto.getName());
        }
        Branch branch = optionalBranch.orElseGet(Branch::new);
        branch.setCtoId(dto.getId());
        branch.setCompanyId(company.getId());
        branch.setDeleted(false);
        branch.setFiscal(true);
        branch.setName(dto.getName());
        if (StringUtils.isNotEmpty(dto.getLongitude())) {
            branch.setLongitude(dto.getLongitude());
        }
        if (StringUtils.isNotEmpty(dto.getLatitude())) {
            branch.setLatitude(dto.getLatitude());
        }
        if (dto.getOfdId() != null) {
            branch.setOfdId(dto.getOfdId());
        }
        if (dto.getActivityTypeCode() != null && dto.getActivityTypeParentCode() != null) {
            activityTypeRepository.findOneByCodeAndParent(dto.getActivityTypeCode(), dto.getActivityTypeParentCode()).ifPresent(activityType -> branch.setActivityTypeId(activityType.getId()));
        }
        if (dto.getAddress() != null) {
            CtoAddressDTO address = dto.getAddress();
            if (address.getRegionCode() != null) {
                Optional<Region> region = regionRepository.findOneByCode(address.getRegionCode());
                region.ifPresent(value -> branch.setRegionId(value.getId()));
                if (address.getDistrictCode() != null) {
                    Optional<City> city = cityRepository.findOneByCodeAndRegionCode(address.getDistrictCode(), address.getRegionCode());
                    city.ifPresent(value -> branch.setCityId(value.getId()));
                }
            }
            StringBuilder addressString = new StringBuilder();
            if (StringUtils.isNotBlank(address.getStreet())) {
                addressString.append(address.getStreet());
            }
            if (StringUtils.isNotBlank(address.getHouse())) {
                addressString.append(", ").append(address.getHouse());
            }
            if (StringUtils.isNotBlank(address.getApartment())) {
                addressString.append(", ").append(address.getApartment());
            }
            branch.setAddress(addressString.toString());
        }
        branchRepository.save(branch);
        if (customer.getOwner() != null && StringUtils.isNotEmpty(customer.getOwner().getPhone())) {
            User user = userRepository.findOneWithAuthoritiesByLogin(customer.getOwner().getPhone()).orElseGet(User::new);
            if (company.getOwner() != null) {
                user = company.getOwner();
            }
            FullNameDTO owner = customer.getOwner();
            user.setLogin(owner.getPhone());
            if (user.getId() == null) {
                final String key = RandomUtil.generateNumericKey();
                user.setPassword(passwordEncoder.encode(key));
                Set<Authority> authorities = new HashSet<>(authorityRepository.findAllById(Collections.singletonList(AuthoritiesConstants.OWNER)));
                user.setAuthorities(authorities);
                smsService.sendSms(new EtcRequestDto(user.getLogin(), StringUtils.join(Constants.COMMON_SMS, key)));
            }
            user.getAuthorities().clear();
            user.getAuthorities().addAll(authorityRepository.findAllById(Collections.singletonList(AuthoritiesConstants.OWNER)));
            user.setActivated(true);
            user.setDeleted(false);
            user.setOwner(true);
            user.setFirstName(owner.getFirstName());
            user.setLastName(owner.getLastName());
            user.setPatronymic(owner.getPatronymic());
            user.setCompanyId(company.getId());
            user.setBranchId(branch.getId());
            clearLoginCaches(user.getLogin());
            userRepository.save(user);
            company.setOwnerId(user.getId());
            companyRepository.save(company);
        }
    }

    public CompanyCommonDTO getBranchesUsingCompanyTin(String tin) {
        Company company = companyRepository.findOneByInn(tin).orElseThrow(notFound(localizationService.getMessage("company")));
        List<Branch> branches = company.getBranches()
                .stream()
                .filter(branch -> branch.getDevices().stream().anyMatch(device -> device.getType() == DeviceType.FISCAL))
                .toList();
        CompanyCommonDTO dto = new CompanyCommonDTO();
        dto.setTin(tin);
        dto.setName(company.getName());
        dto.setBranches(branches.stream()
                .map(SupplierBranchWithAddressDTO::new)
                .toList());

        return dto;
    }

    public List<SelectItem> getBranchLookUp(final BaseFilter filter) {
        filter.setCompanyId(getAdminCompanyId());
        return branchRepository.findAll((Specification<Branch>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isFalse(root.get(Branch_.deleted)));
            predicates.add(criteriaBuilder.equal(root.get(Branch_.companyId), filter.getCompanyId()));
            if (filter.getBranchId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Branch_.id), filter.getBranchId()));
            }
            if (filter.getCtoId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Branch_.company).get(Company_.ctoId), filter.getCtoId()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Branch_.name)), filter.getSearchForQuery()));
            }
            query.orderBy(criteriaBuilder.asc(root.get(Branch_.name)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }).stream().map(branch -> new SelectItem(branch.getId(), branch.getName())).collect(Collectors.toList());
    }

    public Integer getActiveInstance(Long companyId) {
        Company company = companyRepository.findById(Optional.ofNullable(companyId).orElseGet(SecurityUtils::getCurrentCompanyId)).orElseThrow(notFound(localizationService.getMessage("company")));
        List<ActiveDeviceResponseDto> activeDevices = ctoClient.getActiveDevices(tokenStoreService.getToken(TokenType.CTS), company.getInn()).getBody();
        int count = 0;
        if (CollectionUtils.isNotEmpty(activeDevices)) {
            for (ActiveDeviceResponseDto activeDevice : activeDevices) {
                if (!Objects.equals(activeDevice.getDeviceType(), "TERMINAL")) {
                    count += activeDevice.getCount();
                }
            }
        }
        return count;
    }

}
