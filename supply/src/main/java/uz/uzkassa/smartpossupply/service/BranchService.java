package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.address.District;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.branch.BranchAddressLookupDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchDetailDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchListDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchLookUpDTO;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.branch.SmartposBranchListDTO;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyBranchDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.enums.ShiftStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.SmartposClient;
import uz.uzkassa.smartpossupply.integration.SoliqClient;
import uz.uzkassa.smartpossupply.mappers.UserMapper;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqBranchSyncProducer;
import uz.uzkassa.smartpossupply.repository.DistrictRepository;
import uz.uzkassa.smartpossupply.repository.RegionRepository;
import uz.uzkassa.smartpossupply.repository.ShiftRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class BranchService extends BaseService {

    UserMapper userMapper;
    CategoryService categoryService;
    ShiftRepository shiftRepository;
    SmartposClient smartposClient;
    SoliqClient soliqClient;
    NumberingContext numberingContext;
    ValidationContext validationContext;
    Environment environment;
    RabbitMqBranchSyncProducer rabbitMqBranchSyncProducer;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;

    @Transactional
    public Branch createDefault(Company company, User user) {
        Branch branch = new Branch();
        branch.setName("Основной филиал");
        branch.setCompanyId(company.getId());
        branch.setOwnerId(user.getId());
        branch.setMain(true);
        branchRepository.save(branch);
        return branch;
    }

    @Transactional
    public Long create(BranchDTO branchDTO) {
        validationContext.validate(branchDTO);
        Long companyId = Optional.ofNullable(branchDTO.getCompanyId()).orElse(getCurrentCompanyId());
        Branch branch = new Branch();
        branch.setName(branchDTO.getName());
        branch.setBranchNumber(branchDTO.getBranchNumber());
        branch.setAddress(addressMapper.toEntity(branchDTO.getAddress()));
        branch.setOwnerId(branchDTO.getOwnerId());
        branch.setCompanyId(companyId);
        branch.setBranchType(branchDTO.getType());
        branch.setOfficialName(branchDTO.getOfficialName());
        if (BranchType.WEB_KASSA == branchDTO.getType()) {
            branch.setRegistrationDate(Optional.ofNullable(branchDTO.getRegistrationDate()).orElse(LocalDateTime.now()));
        }
        branchRepository.save(branch);
        categoryService.createDefault(branch);
        if (BranchType.RETAILER == branchDTO.getType()) {
            Company company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
            if (company.getMarketplaceId() != null) {
                rabbitMqBranchSyncProducer.sync(new SyncPayload(branch.getId(), AppType.MARKETPLACE));
            }
            if (company.getErpId() != null) {
                rabbitMqBranchSyncProducer.sync(new SyncPayload(branch.getId(), AppType.ERP));
            }
        }
        return branch.getId();
    }

    @Transactional
    public Long update(Long id, BranchDTO branchDTO) {
        branchDTO.setId(id);
        validationContext.validate(branchDTO);
        return branchRepository
            .findById(id)
            .map(branch -> {
                branch.setBranchNumber(branchDTO.getBranchNumber());
                branch.setOfficialName(branchDTO.getOfficialName());
                branch.setName(branchDTO.getName());
                if (branchDTO.getOwnerId() != null && !Objects.equals(branchDTO.getOwnerId(), branch.getOwnerId())) {
                    User owner = userRepository.findById(branchDTO.getOwnerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", branchDTO.getOwnerId()));
                    branch.setOwner(owner);
                    branch.setOwnerId(branchDTO.getOwnerId());
                }
                if (branchDTO.getAddress() != null) {
                    Address address = addressMapper.merge(branchDTO.getAddress(), branch.getAddress() != null ? branch.getAddress() : new Address());
                    if (branchDTO.getAddress().getRegionId() != null) {
                        Region region = regionRepository.findById(branchDTO.getAddress().getRegionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Region.class.getSimpleName(), "id", branchDTO.getAddress().getRegionId()));
                        address.setRegion(region);
                    }
                    if (branchDTO.getAddress().getDistrictId() != null) {
                        District district = districtRepository.findById(branchDTO.getAddress().getDistrictId()).orElseThrow(validationService.entityNotFoundExceptionThrow(District.class.getSimpleName(), "id", branchDTO.getAddress().getDistrictId()));
                        address.setDistrict(district);
                    }
                    branch.setAddress(address);
                }
                if (BranchType.WEB_KASSA == branchDTO.getType() && !StringUtils.hasText(branchDTO.getBranchNumber())) {
                    Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.WEB_KASSA, branch.getCompanyId());
                    branch.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
                    branch.setBranchNumber(numberingMap.get(NUMBER));
                    if (branch.getRegistrationDate() == null) {
                        branch.setRegistrationDate(Optional.ofNullable(branchDTO.getRegistrationDate()).orElse(LocalDateTime.now()));
                    }
                }
                if (branchDTO.getType() != null) {
                    branch.setBranchType(branchDTO.getType());
                }
                branch.setMain(branchDTO.isMain());
                branchRepository.save(branch);
                if (branchDTO.isMain()) {
                    branchRepository.updateSetMainFalseByCompanyId(branch.getCompanyId(), id);
                }
                if (BranchType.RETAILER == branch.getBranchType()) {
                    if (branch.getCompany().getMarketplaceId() != null) {
                        rabbitMqBranchSyncProducer.sync(new SyncPayload(branch.getId(), AppType.MARKETPLACE));
                    }
                    if (branch.getCompany().getErpId() != null) {
                        rabbitMqBranchSyncProducer.sync(new SyncPayload(branch.getId(), AppType.ERP));
                    }
                }
                return branch.getId();
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        branchRepository
            .findById(id)
            .ifPresentOrElse(branch -> {
                branch.setStatus(status);
                branchRepository.save(branch);
            }, () -> validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", id).get());
    }

    @Transactional
    public void updateType(Long id, BranchType type) {
        branchRepository
            .findById(id)
            .ifPresentOrElse(branch -> {
                branch.setBranchType(type);
                branchRepository.save(branch);
            }, () -> validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }

    public Page<BranchListDTO> getList(BaseFilter filter) {
        if (filter.getCompanyId() == null && getCurrentCompanyId() == null && StringUtils.hasText(filter.getTin())) {
            companyRepository.findByTinAndDeletedIsFalse(filter.getTin())
                .ifPresentOrElse(company -> filter.setCompanyId(company.getId()), () -> validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", filter.getTin()).get());
        }

        ResultList<Branch> resultList = branchRepository.getResultList(filter);
        List<BranchListDTO> result = resultList
            .getList()
            .stream()
            .map(branch -> {
                BranchListDTO branchListDTO = new BranchListDTO();
                branchListDTO.setId(branch.getId());
                branchListDTO.setName(branch.getName());
                branchListDTO.setStatus(branch.getStatus());
                branchListDTO.setAddress(addressMapper.toDetailDTO(branch.getAddress()));
                branchListDTO.setOwner(userMapper.toOwnerDTO(branch.getOwner()));
                branchListDTO.setMain(branch.isMain());
                branchListDTO.setType(branch.getBranchType());
                branchListDTO.setBranchNumber(branch.getBranchNumber());
                branchListDTO.setOfficialName(branch.getOfficialName());
                if (BranchType.WEB_KASSA == branch.getBranchType()) {
                    branchListDTO.setRegistrationDate(branch.getRegistrationDate());
                    shiftRepository.findFirstByBranchIdAndStatusOrderByCreatedDateDesc(branch.getId(), ShiftStatus.OPEN).ifPresent(shift -> branchListDTO.setCurrentShift(shift.toCommonDTO()));
                }
                return branchListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Long getCountByStatus(String tin) {
        return branchRepository.countAllByCompany_TinAndStatusAndDeletedIsFalse(tin, CommonStatus.ACTIVE);
    }

    public BranchDetailDTO get(Long id) {
        return branchRepository
            .getDetail(id)
            .map(branch -> {
                BranchDetailDTO branchDetailDTO = new BranchDetailDTO();
                branchDetailDTO.setId(branch.getId());
                branchDetailDTO.setName(branch.getName());
                branchDetailDTO.setAddress(addressMapper.toDetailDTO(branch.getAddress()));
                branchDetailDTO.setOwner(userMapper.toOwnerDTO(branch.getOwner()));
                branchDetailDTO.setMain(branch.isMain());
                branchDetailDTO.setType(branch.getBranchType());
                branchDetailDTO.setBranchNumber(branch.getBranchNumber());
                return branchDetailDTO;
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }

    public List<CommonDTO> getListByRegion(Long regionId) {
        return branchRepository
            .findAllByCompanyIdAndAddress_RegionIdAndDeletedIsFalse(getCurrentCompanyId(), regionId)
            .stream()
            .map(Branch::toCommonDTO)
            .toList();
    }

    public List<BranchLookUpDTO> lookUp(BaseFilter filter) {
        return branchRepository.lookUp(filter).stream().map(branch -> {
            BranchLookUpDTO branchLookUpDTO = new BranchLookUpDTO();
            branchLookUpDTO.setId(branch.getId());
            branchLookUpDTO.setName(branch.getName());
            branchLookUpDTO.setMain(branch.isMain());
            branchLookUpDTO.setBranchNumber(branch.getBranchNumber());
            branchLookUpDTO.setOfficialName(branch.getOfficialName());
            return branchLookUpDTO;
        }).toList();
    }

    public List<BranchAddressLookupDTO> lookUpWithAddress(BaseFilter filter) {
        return branchRepository.lookUp(filter).stream().map(branch -> {
            BranchAddressLookupDTO branchAddressLookupDTO = new BranchAddressLookupDTO();
            branchAddressLookupDTO.setId(branch.getId());
            branchAddressLookupDTO.setName(branch.getName());
            if (branch.getAddress() != null) {
                branchAddressLookupDTO.setAddress(addressMapper.toDetailDTO(branch.getAddress()));
            }
            return branchAddressLookupDTO;
        }).toList();
    }

    public CommonStatisticsDTO getStatistics(BaseFilter filter) {
        return branchRepository.getStatistics(filter);
    }


    public Long getActiveBranchesCount(String tin, Long companyId) {
        if (companyId != null) {
            return companyRepository.findByIdAndDeletedIsFalse(companyId)
                .map(company -> branchRepository.countByCompanyIdAndStatusAndDeletedIsFalse(company.getId(), CommonStatus.ACTIVE))
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
        }
        return companyRepository
            .findByTinAndDeletedIsFalse(tin)
            .map(company -> branchRepository.countByCompanyIdAndStatusAndDeletedIsFalse(company.getId(), CommonStatus.ACTIVE))
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", tin));
    }

    public SmartposBranchListDTO getSmartposBranches(String tin) {
        if (!StringUtils.hasText(tin)) {
            tin = getCurrentCompanyTin();
        }
        if (!StringUtils.hasText(tin)) {
            throw validationService.badRequestExceptionThrow("Company tin is required").get();
        }
        return smartposClient.getBranches(tin);
    }

    public List<TaxCompanyBranchDTO> getBranchesInfoGNK(String tin) {
        return Arrays.stream(soliqClient.getBranchesByCompany(tin, environment.matchesProfiles("prod"))).filter(
            taxCompanyBranchDTO -> !branchRepository.existsByCompanyIdAndBranchNumberAndDeletedIsFalse(getCurrentCompanyId(), taxCompanyBranchDTO.getBranchNum())
        ).toList();
    }

    public void batchSync(AppType appType) {
        List<Long> ids = null;
        if (AppType.ERP == appType) {
            ids = branchRepository.getIdsToSyncErp();
        } else if (AppType.MARKETPLACE == appType) {
            ids = branchRepository.getIdsToSyncMarketplace();
        }
        if (!CollectionUtils.isEmpty(ids)) {
            ids.forEach(id -> rabbitMqBranchSyncProducer.sync(new SyncPayload(id, appType)));
        }
    }

    public void sync(Long id, AppType appType) {
        rabbitMqBranchSyncProducer.sync(new SyncPayload(id, appType));
    }
}
