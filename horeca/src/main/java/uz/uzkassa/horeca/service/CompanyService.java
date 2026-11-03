package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.address.Address;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.company.CompanyBank;
import uz.uzkassa.horeca.domain.company.CompanyDetail;
import uz.uzkassa.horeca.dto.activityttype.ActivityTypeDTO;
import uz.uzkassa.horeca.dto.address.AddressDTO;
import uz.uzkassa.horeca.dto.address.AddressDetailDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyDTO;
import uz.uzkassa.horeca.dto.company.admin.CompanyDetailAdminDTO;
import uz.uzkassa.horeca.dto.company.admin.CompanyListAdminDTO;
import uz.uzkassa.horeca.dto.company.cabinet.CompanyDetailCabinetDTO;
import uz.uzkassa.horeca.dto.companybank.CompanyBankDTO;
import uz.uzkassa.horeca.dto.employee.OwnerDTO;
import uz.uzkassa.horeca.dto.terminal.ApaySubmitApplicationResponseDTO;
import uz.uzkassa.horeca.enums.ApayMerchantReferenceType;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.sync.AddressSyncDTO;
import uz.uzkassa.horeca.dto.sync.BankSyncDTO;
import uz.uzkassa.horeca.dto.sync.CompanySyncDTO;
import uz.uzkassa.horeca.dto.terminal.ApayBranchRegisterDTO;
import uz.uzkassa.horeca.dto.terminal.ApayCompanyRegisterDTO;
import uz.uzkassa.horeca.dto.terminal.CheckApayCompanyDTO;
import uz.uzkassa.horeca.dto.user.UserDTO;
import uz.uzkassa.horeca.enums.BusinessType;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.Vat;
import uz.uzkassa.horeca.filters.CompanyFilter;
import uz.uzkassa.horeca.integration.ApayClient;
import uz.uzkassa.horeca.mappers.FileUploadMapper;
import uz.uzkassa.horeca.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CompanyService extends BaseService {

    CompanyDetailRepository companyDetailRepository;
    ActivityTypeRepository activityTypeRepository;
    FileUploadMapper fileUploadMapper;
    AmazonS3Service amazonS3Service;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    NeighborhoodRepository neighborhoodRepository;
    CompanyBankService companyBankService;
    BankRepository bankRepository;
    UserService userService;
    CompanyBankRepository companyBankRepository;
    ApayClient apayClient;

    @Transactional
    public Long update(Long id, CompanyDTO companyDTO) {
        if (id == null) {
            id = getCurrentCompanyId();
        }
        return companyRepository
                .findById(id)
                .map(company -> {
                    company.setBusinessType(BusinessType.getByCode(companyDTO.getBusinessType()));
                    company.setActivityTypeId(companyDTO.getActivityTypeId());
                    company.setOked(companyDTO.getOked());
                    company.setWifiPassword(companyDTO.getWifiPassword());
                    companyRepository.save(company);
                    return company.getId();
                })
                .orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    public CompanyDetailAdminDTO getForAdmin(Long id) {
        return companyRepository
                .findById(id)
                .map(company -> {
                    CompanyDetail companyDetail = companyDetailRepository.findFirstByCompanyId(id).orElseThrow(notFoundExceptionThrow(CompanyDetail.class.getSimpleName(), "id", id));
                    CompanyDetailAdminDTO companyDetailDTO = new CompanyDetailAdminDTO();
                    companyDetailDTO.setId(company.getId());
                    companyDetailDTO.setName(company.getName());
                    companyDetailDTO.setTin(company.getTin());
                    companyDetailDTO.setBusinessType(company.getBusinessType());
                    companyDetailDTO.setWifiPassword(company.getWifiPassword());
                    if (company.getActivityTypeId() != null) {
                        companyDetailDTO.setActivityType(
                                new ActivityTypeDTO(
                                        company.getActivityType().getId(),
                                        company.getActivityType().getName(),
                                        company.getActivityType().getParentId()
                                )
                        );
                    }
                    if (company.getOwnerId() != null) {
                        companyDetailDTO.setOwner(company.getOwner().toCommonDTO());
                    }
                    companyDetailDTO.setPhone(company.getContactPhone());
                    if (companyDetail.getDirectorId() != null) {
                        companyDetailDTO.setDirector(companyDetail.getDirector().toCommonDTO());
                    }
                    return companyDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    public CompanyDetailCabinetDTO getForCabinet() {
        return companyRepository
                .findById(getCurrentCompanyId())
                .map(company -> {
                    CompanyDetail companyDetail = companyDetailRepository.findFirstByCompanyId(getCurrentCompanyId()).orElseThrow(notFoundExceptionThrow(CompanyDetail.class.getSimpleName(), "id", getCurrentCompanyId()));
                    CompanyDetailCabinetDTO companyDetailDTO = new CompanyDetailCabinetDTO();
                    companyDetailDTO.setId(company.getId());
                    companyDetailDTO.setName(company.getName());
                    companyDetailDTO.setTin(company.getTin());
                    companyDetailDTO.setBrand(company.getBrand());
                    companyDetailDTO.setBusinessType(company.getBusinessType());
                    companyDetailDTO.setOked(company.getOked());
                    companyDetailDTO.setWifiPassword(company.getWifiPassword());
                    if (company.getActivityType() != null) {
                        companyDetailDTO.setActivityType(
                                new ActivityTypeDTO(
                                        company.getActivityType().getId(),
                                        company.getActivityType().getName(),
                                        company.getActivityType().getParentId()
                                )
                        );
                    }
                    if (company.getOwnerId() != null) {
                        companyDetailDTO.setOwner(company.getOwner().toCommonDTO());
                    }
                    if (companyDetail.getManagerId() != null) {
                        companyDetailDTO.setManager(companyDetail.getManager().toCommonDTO());
                    }

                    companyDetailDTO.setPhone(company.getContactPhone());
                    if (companyDetail.getDirectorId() != null) {
                        companyDetailDTO.setDirector(companyDetail.getDirector().toCommonDTO());
                    }
                    if (companyDetail.getLogo() != null) {
                        companyDetailDTO.setLogo(fileUploadMapper.toFileDTO(companyDetail.getLogo()));
                    }
                    if (companyDetail.getBackgroundPhoto() != null) {
                        companyDetailDTO.setBackgroundPhoto(fileUploadMapper.toFileDTO(companyDetail.getBackgroundPhoto()));
                    }
                    return companyDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", getCurrentCompanyId()));
    }

    public Page<CompanyListAdminDTO> getListForAdmin(CompanyFilter filter) {
        ResultList<Company> resultList = companyRepository.getResultList(filter);
        List<CompanyListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(company -> {
                    CompanyListAdminDTO companyListDTO = new CompanyListAdminDTO();
                    companyListDTO.setId(company.getId());
                    companyListDTO.setName(company.getName());
                    companyListDTO.setTin(company.getTin());
                    companyListDTO.setRegistrationDate(company.getRegistrationDate());
                    companyListDTO.setOwner(company.getOwner().toCommonDTO());
                    companyListDTO.setStatus(company.getStatus());
                    companyListDTO.setPhone(company.getContactPhone());
                    return companyListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        companyRepository
                .findById(id)
                .map(company -> {
                    company.setStatus(status);
                    companyRepository.save(company);
                    cacheService.evictCache(Company.class.getName(), String.valueOf(company.getId()));
                    return company;
                })
                .orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    public List<CompanyCommonDTO> lookUp(CompanyFilter filter) {
        return companyRepository.getResultList(filter).getList().stream().map(this::toCompanyBaseDTO).collect(Collectors.toList());
    }

    @Transactional
    public FileDTO uploadLogo(MultipartFile file) {
        return companyDetailRepository.findFirstByCompanyId(getCurrentCompanyId()).map(companyDetail -> {
            if (companyDetail.getLogoId() != null) {
                amazonS3Service.delete(companyDetail.getLogoId());
            }
            FileUpload logo = amazonS3Service.uploadCompanyLogo(file, companyDetail.getId());
            companyDetail.setLogoId(logo.getId());
            companyDetail.setLogo(logo);
            companyDetailRepository.save(companyDetail);
            return fileUploadMapper.toFileDTO(logo);
        }).orElseThrow(notFoundExceptionThrow(CompanyDetail.class.getSimpleName(), "id", getCurrentCompanyId()));

    }

    @Transactional
    public void removeLogo() {
        companyDetailRepository.findFirstByCompanyId(getCurrentCompanyId()).map(companyDetail -> {
            FileUpload logo = companyDetail.getLogo();
            if (logo != null) {
                companyDetail.setLogo(null);
                companyDetail.setLogoId(null);
                companyDetailRepository.save(companyDetail);
                amazonS3Service.delete(logo.getId());
            }
            return companyDetail;
        }).orElseThrow(notFoundExceptionThrow(CompanyDetail.class.getSimpleName(), "id", getCurrentCompanyId()));

    }

    @Transactional
    public FileDTO uploadBackgroundPhoto(MultipartFile file) {
        return companyDetailRepository.findFirstByCompanyId(getCurrentCompanyId()).map(companyDetail -> {
            if (companyDetail.getBackgroundPhotoId() != null) {
                amazonS3Service.delete(companyDetail.getBackgroundPhotoId());
            }
            FileUpload backgroundPhoto = amazonS3Service.uploadCompanyBackgroundPhoto(file, companyDetail.getId());
            companyDetail.setBackgroundPhotoId(backgroundPhoto.getId());
            companyDetail.setBackgroundPhoto(backgroundPhoto);
            companyDetailRepository.save(companyDetail);
            return fileUploadMapper.toFileDTO(backgroundPhoto);
        }).orElseThrow(notFoundExceptionThrow(CompanyDetail.class.getSimpleName(), "id", getCurrentCompanyId()));
    }

    @Transactional
    public void removeBackgroundPhoto() {
        companyDetailRepository.findFirstByCompanyId(getCurrentCompanyId()).map(companyDetail -> {
            FileUpload backgroundImage = companyDetail.getBackgroundPhoto();
            if (backgroundImage != null) {
                companyDetail.setBackgroundPhoto(null);
                companyDetail.setBackgroundPhotoId(null);
                companyDetailRepository.save(companyDetail);
                amazonS3Service.delete(backgroundImage.getId());
            }
            return companyDetail;
        }).orElseThrow(notFoundExceptionThrow(CompanyDetail.class.getSimpleName(), "id", getCurrentCompanyId()));
    }

    public AddressDetailDTO getAddress() {
        return addressMapper.toDetailDTO(getCurrentCompany().getAddress());
    }

    @Transactional
    public void createAddress(AddressDTO addressDTO) {
        Company company = getCurrentCompany();
        company.setAddress(addressMapper.toEntity(addressDTO));
        companyRepository.save(company);
    }

    @Transactional
    public void updateAddress(AddressDTO addressDTO) {
        Company company = getCurrentCompany();
        company.setAddress(addressMapper.merge(addressDTO, Optional.ofNullable(company.getAddress()).orElse(new Address())));
        companyRepository.save(company);
    }

    @Transactional
    public Long sync(CompanySyncDTO companySyncDTO) {
        try {
            log.info("Company sync dto: {}", objectMapper.writeValueAsString(companySyncDTO));
        } catch (JsonProcessingException e) {
            log.info(" Writing object error: {}", e.getMessage());
        }
        Company company = companyRepository.findByTin(companySyncDTO.getTin()).orElseGet(Company::new);

        company.setTin(companySyncDTO.getTin());
        company.setCtsId(companySyncDTO.getId());
        company.setName(companySyncDTO.getCompanyName());
        company.setBrand(companySyncDTO.getName());
        company.setContactPhone(companySyncDTO.getPhone());
        company.setOked(companySyncDTO.getOked());
        company.setBusinessType(BusinessType.getByCode(companySyncDTO.getBusinessType()));
        company.setVat(Vat.getByAmount(companySyncDTO.getVatRate()));
        if (companySyncDTO.getActivityType() != null && companySyncDTO.getActivityType().getCode() != null && companySyncDTO.getActivityType().getParentCode() != null) {
            activityTypeRepository.findFirstByCodeAndParent_Code(companySyncDTO.getActivityType().getCode(), companySyncDTO.getActivityType().getParentCode()).ifPresent(activityType -> company.setActivityTypeId(activityType.getId()));
        }
        if (companySyncDTO.getAddress() != null) {
            Address address = new Address();
            if (companySyncDTO.getAddress().getRegionCode() != null) {
                regionRepository.findFirstByCode(companySyncDTO.getAddress().getRegionCode().intValue()).ifPresent(region -> address.setRegionId(region.getId()));
            }
            if (companySyncDTO.getAddress().getRegionCode() != null && companySyncDTO.getAddress().getDistrictCode() != null) {
                districtRepository.findFirstByRegion_CodeAndCode(companySyncDTO.getAddress().getRegionCode().intValue(), companySyncDTO.getAddress().getDistrictCode().intValue()).ifPresent(district -> address.setDistrictId(district.getId()));
            }
            if (companySyncDTO.getAddress().getDistrictCode() != null && companySyncDTO.getAddress().getNeighborhoodCode() != null) {
                neighborhoodRepository.findFirstByDistrict_CodeAndCode(companySyncDTO.getAddress().getDistrictCode().intValue(), companySyncDTO.getAddress().getNeighborhoodCode()).ifPresent(neighborhood -> address.setNeighborhoodId(neighborhood.getId()));
            }
            address.setLandmark(companySyncDTO.getAddress().getLandmark());
            address.setStreet(companySyncDTO.getAddress().getStreet());
            address.setHouse(companySyncDTO.getAddress().getHouse());
            address.setApartment(companySyncDTO.getAddress().getApartment());
            if (companySyncDTO.getAddress().getLongitude() != null) {
                address.setLongitude(Double.valueOf(companySyncDTO.getAddress().getLongitude()));
            }
            if (companySyncDTO.getAddress().getLatitude() != null) {
                address.setLatitude(Double.valueOf(companySyncDTO.getAddress().getLatitude()));
            }
            company.setAddress(address);
        }
        companyRepository.save(company);

        if (companySyncDTO.getDirector() != null) {
            UserDTO ownerDTO = new UserDTO();
            ownerDTO.setFirstName(companySyncDTO.getDirector().getFirstName());
            ownerDTO.setLastName(companySyncDTO.getDirector().getLastName());
            ownerDTO.setPatronymic(companySyncDTO.getDirector().getPatronymic());
            ownerDTO.setRole(Role.ROLE_OWNER.getCode());
            ownerDTO.setLogin(companySyncDTO.getDirector().getUsername());
            ownerDTO.setPhone(companySyncDTO.getDirector().getPhone());
            ownerDTO.setCompanyId(company.getId());
            company.setOwnerId(userService.create(ownerDTO));
        }

        CompanyDetail companyDetail = companyDetailRepository.findFirstByCompanyId(company.getId()).orElseGet(CompanyDetail::new);
        if (companyDetail.getId() == null) {
            companyDetail.setCompanyId(company.getId());
        }

        if (companySyncDTO.getManager() != null) {
            UserDTO managerDTO = new UserDTO();
            managerDTO.setFirstName(companySyncDTO.getManager().getFirstName());
            managerDTO.setLastName(companySyncDTO.getManager().getLastName());
            managerDTO.setPatronymic(companySyncDTO.getManager().getPatronymic());
            managerDTO.setRole(Role.ROLE_OPERATOR.getCode());
            managerDTO.setLogin(companySyncDTO.getManager().getUsername());
            managerDTO.setPhone(companySyncDTO.getManager().getPhone());
            managerDTO.setCompanyId(company.getId());
            companyDetail.setManagerId(userService.create(managerDTO));
        }

        if (companySyncDTO.getBank() != null) {
            CompanyBankDTO companyBankDTO = new CompanyBankDTO();
            companyBankDTO.setCompanyId(company.getId());
            companyBankDTO.setAccountNumber(companySyncDTO.getBank().getAccountNumber());
            bankRepository.findFirstByMfo(companySyncDTO.getBank().getMfo()).ifPresent(bank -> companyBankDTO.setBankId(bank.getId()));
            companyBankService.create(companyBankDTO);
        }

        companyDetailRepository.save(companyDetail);
        return company.getId();
    }

    @Transactional
    public void apaySync(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
        if (company.getBusinessType() == null) {
            throw badRequestExceptionThrow("Business type is required").get();
        }
        if (company.getActivityType() == null) {
            throw badRequestExceptionThrow("Activity type is required").get();
        }
        if (company.getAddress() == null) {
            throw badRequestExceptionThrow("Address is required").get();
        }
        if (company.getAddress().getRegionId() == null) {
            throw badRequestExceptionThrow("Region is required").get();
        }
        if (company.getAddress().getDistrictId() == null) {
            throw badRequestExceptionThrow("District is required").get();
        }
        if (company.getOwner() == null) {
            throw badRequestExceptionThrow("Owner is required").get();
        }
        CompanyBank companyBank = companyBankRepository.findFirstByCompanyIdAndDeletedIsFalse(companyId).map(bank -> {
            if (bank.getBankId() == null || StringUtils.isBlank(bank.getBank().getMfo())) {
                throw badRequestExceptionThrow("Bank mfo is required").get();
            }
            return bank;
        }).orElseThrow(notFoundExceptionThrow(CompanyBank.class.getSimpleName(), "id", companyId));

        List<Branch> branches = company.getBranches();

        if (CollectionUtils.isEmpty(branches)) {
            throw badRequestExceptionThrow("Branch is required").get();
        }
        branches.forEach(branch -> {
            if (branch.getAddress() == null || branch.getAddress().getRegionId() == null) {
                throw badRequestExceptionThrow("Branch region is required").get();
            }
            if (branch.getAddress().getDistrictId() == null) {
                throw badRequestExceptionThrow("Branch district is required").get();
            }
        });
        boolean exists = false;
        try {
            ResponseEntity<CheckApayCompanyDTO> checkCompanyResponse = apayClient.checkCompanyExist(company.getTin());
            if (checkCompanyResponse.getStatusCode().is2xxSuccessful() && checkCompanyResponse.getBody() != null) {
                CheckApayCompanyDTO companyDTO = checkCompanyResponse.getBody();
                company.setApayId(companyDTO.getApayCompanyId());
                company.setApayTariffId(companyDTO.getTariff() != null ? companyDTO.getTariff().getId() : null);
                exists = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        ApayCompanyRegisterDTO dto = new ApayCompanyRegisterDTO();
        dto.setName(company.getName());
        dto.setBrandName(company.getBrand());
        dto.setTin(company.getTin());
        if (company.getOwner() != null) {
            User owner = company.getOwner();
            dto.setPhone(owner.getLogin());
            OwnerDTO ownerDTO = new OwnerDTO();
            ownerDTO.setId(owner.getId());
            ownerDTO.setFirstName(owner.getFirstName());
            ownerDTO.setLastName(owner.getLastName());
            ownerDTO.setPatronymic(owner.getPatronymic());
            ownerDTO.setLogin(owner.getLogin());
            ownerDTO.setPhone(owner.getPhone());
            ownerDTO.setPinfl(owner.getPinfl());
            ownerDTO.setTin(owner.getTin());
            dto.setOwner(ownerDTO);
        }
        dto.setReferenceType(ApayMerchantReferenceType.HORECA);
        if (company.getActivityTypeId() != null) {
            dto.setActivityType(
                    new ActivityTypeDTO(
                            company.getActivityType().getId(),
                            company.getActivityType().getName(),
                            company.getActivityType().getParentId()
                    )
            );
        }
//        dto.setCadastreRentFile(new FileDTO(getCadastreFileId()));
//        dto.setApplicationFile(new FileDTO(getApplicationFileId()));
        if (company.getBusinessType() != null) {
            dto.setBusinessType(BusinessType.valueOf(company.getBusinessType().getCode()));
        }
        AddressSyncDTO addressDTO = new AddressSyncDTO();
        if (company.getAddress().getRegion() != null && company.getAddress().getRegion().getCode() != null) {
            addressDTO.setRegionCode(Long.valueOf(company.getAddress().getRegion().getCode()));
        }
        if (company.getAddress().getDistrictId() != null && company.getAddress().getDistrict().getCode() != null) {
            addressDTO.setDistrictCode(Long.valueOf(company.getAddress().getDistrict().getCode()));
        }
        addressDTO.setStreet(company.getAddress().getFullAddress());

        BankSyncDTO bankDTO = new BankSyncDTO();
        bankDTO.setAccountNumber(companyBank.getAccountNumber());
        bankDTO.setMfo(companyBank.getBank().getMfo());
//        bankDTO.setOked(getOked());
//        bankDTO.setTin(getInn());
//        addressDTO.setVatRate(getNdsPercent());
        dto.setTariffId(company.getApayTariffId());
        dto.setBranchList(branches.stream().map(branch -> {
            ApayBranchRegisterDTO branchDTO = new ApayBranchRegisterDTO();
            branchDTO.setReferenceId(branch.getCtsId().toString());
            branchDTO.setName(branch.getName());
            AddressSyncDTO branchAddressDTO = new AddressSyncDTO();
            if (branch.getAddress().getRegion() != null && branch.getAddress().getRegion().getCode() != null) {
                branchAddressDTO.setRegionCode(Long.valueOf(branch.getAddress().getRegion().getCode()));
            }
            if (branch.getAddress().getDistrictId() != null && branch.getAddress().getDistrict().getCode() != null) {
                branchAddressDTO.setDistrictCode(Long.valueOf(branch.getAddress().getDistrict().getCode()));
            }
            branchAddressDTO.setStreet(branch.getAddress().getFullAddress());
            branchDTO.setAddress(branchAddressDTO);
            return branchDTO;
        }).toList());
        try {
            log.info("Apay quick register request: {}", objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException ignored) {
        }
        if (!exists) {
            ResponseEntity<ApaySubmitApplicationResponseDTO> response = apayClient.registerCompany(dto);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Apay quick register response: {}", response.getBody());
                company.setApayId(response.getBody().getId());
                company.setApayApplicationStatus(response.getBody().getStatus());
            }
        } else {
            apayClient.registerBranches(dto);
        }
        companyRepository.save(company);
        branchRepository.saveAll(branches);
    }
}
