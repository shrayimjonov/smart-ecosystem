package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerBranchStatisticsAdminDTO;
import uz.uzkassa.cts.dto.customerbranch.CustomerBranchDTO;
import uz.uzkassa.cts.dto.customerbranch.admin.CustomerBranchDetailAdminDTO;
import uz.uzkassa.cts.dto.customerbranch.admin.CustomerBranchListAdminDTO;
import uz.uzkassa.cts.dto.customerbranch.cabinet.CustomerBranchDetailCabinetDTO;
import uz.uzkassa.cts.dto.customerbranch.cabinet.CustomerBranchListCabinetDTO;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.mappings.AddressMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqAPayProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.ContactService;
import uz.uzkassa.cts.service.CustomerBranchService;
import uz.uzkassa.cts.service.SocialAddressService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CustomerBranchServiceImpl extends BaseServiceImpl<CustomerBranch, Long> implements CustomerBranchService {

    private final CustomerBranchRepository customerBranchRepository;
    private final SocialAddressService socialAddressService;
    private final ContactService contactService;
    private final ReferenceRepository referenceRepository;
    private final RabbitMqAPayProducer rabbitMqAPayProducer;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final ApayCategoryRepository apayCategoryRepository;
    private final AddressMapper addressMapper;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final RegionalAssignmentRepository regionalAssignmentRepository;
    private final ValidationContext validationContext;

    public CustomerBranchServiceImpl(CustomerBranchRepository customerBranchRepository,
                                     SocialAddressService socialAddressService,
                                     ContactService contactService,
                                     ReferenceRepository referenceRepository,
                                     RabbitMqAPayProducer rabbitMqAPayProducer,
                                     RegionRepository regionRepository,
                                     DistrictRepository districtRepository,
                                     NeighborhoodRepository neighborhoodRepository,
                                     ActivityTypeRepository activityTypeRepository,
                                     ApayCategoryRepository apayCategoryRepository,
                                     AddressMapper addressMapper, CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                     CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository,
                                     FiscalizationRepository fiscalizationRepository, RegionalAssignmentRepository regionalAssignmentRepository, ValidationContext validationContext) {
        super(customerBranchRepository);
        this.customerBranchRepository = customerBranchRepository;
        this.socialAddressService = socialAddressService;
        this.contactService = contactService;
        this.referenceRepository = referenceRepository;
        this.rabbitMqAPayProducer = rabbitMqAPayProducer;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.apayCategoryRepository = apayCategoryRepository;
        this.addressMapper = addressMapper;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.customerBranchFiscalModuleRepository = customerBranchFiscalModuleRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.regionalAssignmentRepository = regionalAssignmentRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(CustomerBranchDTO customerBranchDTO) {
        validationContext.validate(customerBranchDTO);
        String phone = CollectionUtils.isNotEmpty(customerBranchDTO.getPhones()) ? customerBranchDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName() : null;
        CustomerBranch customerBranch = new CustomerBranch();
        customerBranch.setBranchId(Optional.ofNullable(customerBranchDTO.getBranchId()).orElseGet(BaseServiceImpl::getBranchId));
        customerBranch.setCustomerId(customerBranchDTO.getCustomerId());
        customerBranch.setName(customerBranchDTO.getName());
        customerBranch.setBrand(customerBranchDTO.getBrand());
        customerBranch.setPhone(phone);
        customerBranch.setActivityTypeId(customerBranchDTO.getActivityTypeId());
        customerBranch.setAPayCategoryId(customerBranchDTO.getApayCategoryId());
        customerBranch.setCadastreNumber(customerBranchDTO.getCadastreNumber());
        customerBranch.setBaseAddress(addressMapper.toEntity(customerBranchDTO.getAddress()));
        customerBranchRepository.save(customerBranch);

        if (CollectionUtils.isNotEmpty(customerBranchDTO.getPhones())) {
            contactService.createBatch(customerBranch.getEntityType(), customerBranch.getEntityId(), customerBranchDTO.getPhones());
        }
        if (CollectionUtils.isNotEmpty(customerBranchDTO.getSocialAddresses())) {
            socialAddressService.createBatch(customerBranch.getEntityType(), customerBranch.getEntityId(), customerBranchDTO.getSocialAddresses());
        }
        rabbitMqAPayProducer.createCustomerBranch(new Payload(customerBranch.getId()));
        return customerBranch.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CustomerBranchDTO customerBranchDTO) {
        customerBranchDTO.setId(id);
        validationContext.validate(customerBranchDTO);
        return customerBranchRepository.findById(id).map(customerBranch -> {
            String phone = customerBranchDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
            customerBranch.setName(customerBranchDTO.getName());
            customerBranch.setBrand(customerBranchDTO.getBrand());
            BaseAddress baseAddress = addressMapper.merge(customerBranchDTO.getAddress(), Optional.ofNullable(customerBranch.getAddress()).orElseGet(BaseAddress::new));
            if (baseAddress.getRegionId() != null) {
                regionRepository.findById(baseAddress.getRegionId()).ifPresent(region -> {
                    baseAddress.setRegionId(region.getId());
                    baseAddress.setRegion(region);
                });
            }
            if (baseAddress.getDistrictId() != null) {
                districtRepository.findById(baseAddress.getDistrictId()).ifPresent(district -> {
                    baseAddress.setDistrictId(district.getId());
                    baseAddress.setDistrict(district);
                });
            }
            if (baseAddress.getNeighborhoodId() != null) {
                neighborhoodRepository.findById(baseAddress.getNeighborhoodId()).ifPresent(neighborhood -> {
                    baseAddress.setNeighborhoodId(neighborhood.getId());
                    baseAddress.setNeighborhood(neighborhood);
                });
            }
            customerBranch.setBaseAddress(baseAddress);
            if (customerBranchDTO.getActivityTypeId() != null) {
                customerBranch.setActivityType(activityTypeRepository.findById(customerBranchDTO.getActivityTypeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(TYPE)));
            }
            customerBranch.setActivityTypeId(customerBranchDTO.getActivityTypeId());
            if (customerBranchDTO.getApayCategoryId() != null) {
                customerBranch.setAPayCategory(apayCategoryRepository.findById(customerBranchDTO.getApayCategoryId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CATEGORY)));
            }
            customerBranch.setAPayCategoryId(customerBranchDTO.getApayCategoryId());
            customerBranch.setCadastreNumber(customerBranchDTO.getCadastreNumber());
            customerBranch.setPhone(phone);
            customerBranchRepository.save(customerBranch);

            contactService.updateBatch(customerBranch.getEntityType(), customerBranch.getEntityId(), customerBranchDTO.getPhones());
            if (CollectionUtils.isNotEmpty(customerBranchDTO.getSocialAddresses())) {
                socialAddressService.updateBatch(customerBranch.getEntityType(), customerBranch.getEntityId(), customerBranchDTO.getSocialAddresses());
            }
            rabbitMqAPayProducer.updateCustomerBranch(new Payload(customerBranch.getId()));
            return customerBranch.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
    }

    @Override
    public CustomerBranchDetailAdminDTO getForAdmin(Long id) {
        return customerBranchRepository.findById(id).map(customerBranch -> {
            List<Long> phoneTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.PHONE_TYPE);
            CustomerBranchDetailAdminDTO customerBranchDetailDTO = new CustomerBranchDetailAdminDTO();
            customerBranchDetailDTO.setId(customerBranch.getId());
            customerBranchDetailDTO.setName(customerBranch.getName());
            customerBranchDetailDTO.setBrand(customerBranch.getBrand());
            customerBranchDetailDTO.setCustomer(customerBranch.getCustomer().toShortDTO());
            customerBranchDetailDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
            customerBranchDetailDTO.setActivityType(customerBranch.getActivityType().toCommonDTO());
            if (customerBranch.getAPayCategoryId() != null) {
                customerBranchDetailDTO.setApayCategory(customerBranch.getAPayCategory().toCommonDTO());
            }
            customerBranchDetailDTO.setCadastreNumber(customerBranch.getCadastreNumber());
            customerBranchDetailDTO.setPhones(contactService.getList(customerBranch.getEntityType(), customerBranch.getEntityId(), phoneTypeIds));
            customerBranchDetailDTO.setSocialAddresses(socialAddressService.getByEntityTypeAndEntityId(EntityTypeEnum.CUSTOMER_BRANCH, customerBranch.getId()));
            customerBranchDetailDTO.setStatus(customerBranch.getStatus());
            customerBranchDetailDTO.setReason(customerBranch.getReason());
            return customerBranchDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
    }

    @Override
    public CustomerBranchDetailCabinetDTO getForCabinet(Long id) {
        return customerBranchRepository.findById(id).map(customerBranch -> {
            List<Long> phoneTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.PHONE_TYPE);
            CustomerBranchDetailCabinetDTO customerBranchDetailDTO = new CustomerBranchDetailCabinetDTO();
            customerBranchDetailDTO.setId(customerBranch.getId());
            customerBranchDetailDTO.setName(customerBranch.getName());
            customerBranchDetailDTO.setBrand(customerBranch.getBrand());
            customerBranchDetailDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
            customerBranchDetailDTO.setActivityType(customerBranch.getActivityType().toCommonDTO());
            if (customerBranch.getAPayCategoryId() != null) {
                customerBranchDetailDTO.setApayCategory(customerBranch.getAPayCategory().toCommonDTO());
            }
            customerBranchDetailDTO.setCadastreNumber(customerBranch.getCadastreNumber());
            customerBranchDetailDTO.setPhones(contactService.getList(customerBranch.getEntityType(), customerBranch.getEntityId(), phoneTypeIds));
            customerBranchDetailDTO.setSocialAddresses(socialAddressService.getByEntityTypeAndEntityId(EntityTypeEnum.CUSTOMER_BRANCH, customerBranch.getId()));
            customerBranchDetailDTO.setStatus(customerBranch.getStatus());
            customerBranchDetailDTO.setReason(customerBranch.getReason());
            return customerBranchDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
    }

    @Override
    public Page<CustomerBranchListAdminDTO> getListForAdmin(CustomerBranchFilter filter) {
        this.checkForPromoManager(filter);
        ResultList<CustomerBranch> resultList = customerBranchRepository.getResultList(filter);
        List<CustomerBranchListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(customerBranch -> {
                    CustomerBranchListAdminDTO customerBranchListDTO = new CustomerBranchListAdminDTO();
                    customerBranchListDTO.setId(customerBranch.getId());
                    customerBranchListDTO.setName(customerBranch.getName());
                    customerBranchListDTO.setCustomer(customerBranch.getCustomer().toShortDTO());
                    customerBranchListDTO.setAddress(addressMapper.toShortDTO(customerBranch.getAddress()));
                    customerBranchListDTO.setCadastreNumber(customerBranch.getCadastreNumber());
                    if (customerBranch.getActivityTypeId() != null) {
                        customerBranchListDTO.setActivityType(customerBranch.getActivityType().toCommonDTO());
                    }
                    customerBranchListDTO.setPhone(customerBranch.getPhone());
                    customerBranchListDTO.setStatus(customerBranch.getStatus());
                    customerBranchListDTO.setOfdStatus(customerBranch.getOfdStatus());
                    customerBranchListDTO.setCreatedDate(customerBranch.getCreatedDate());
                    customerBranchListDTO.setOfdId(customerBranch.getOfdId());

                    if ((OfdStatusEnum.REJECT == customerBranch.getOfdStatus() && customerBranch.getOfdId() != null)
                            || (customerBranch.getOfdId() != null && customerBranch.isResync())) {
                        customerBranchListDTO.setReSync(true);
                    } else if (OfdStatusEnum.REJECT == customerBranch.getOfdStatus()) {
                        customerBranchListDTO.setSync(true);
                    } else if (customerBranch.getOfdStatus() == null && customerBranch.getOfdId() == null) {
                        customerBranchListDTO.setSync(true);
                    }

                    return customerBranchListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<CustomerBranchListCabinetDTO> getListForCabinet(CustomerBranchFilter filter) {
        this.checkForPromoManager(filter);
        ResultList<CustomerBranch> resultList = customerBranchRepository.getResultList(filter);
        List<CustomerBranchListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(customerBranch -> {
                    CustomerBranchListCabinetDTO customerBranchListDTO = new CustomerBranchListCabinetDTO();
                    customerBranchListDTO.setId(customerBranch.getId());
                    customerBranchListDTO.setName(customerBranch.getName());
                    customerBranchListDTO.setBrand(customerBranch.getBrand());
                    customerBranchListDTO.setAddress(addressMapper.toShortDTO(customerBranch.getAddress()));
                    if (customerBranch.getActivityTypeId() != null) {
                        customerBranchListDTO.setActivityType(customerBranch.getActivityType().toCommonDTO());
                    }
                    if (customerBranch.getAPayCategoryId() != null) {
                        customerBranchListDTO.setApayCategory(customerBranch.getAPayCategory().toCommonDTO());
                    }
                    customerBranchListDTO.setPhone(customerBranch.getPhone());
                    return customerBranchListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(CustomerBranchFilter filter) {
        return customerBranchRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CustomerBranchStatusEnum status, String reason) {
        customerBranchRepository
                .findById(id)
                .map(customerBranch -> {
                    customerBranch.setStatus(status);
                    customerBranch.setReason(reason);
                    customerBranchRepository.save(customerBranch);
                    return customerBranch.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
    }

    @Override
    public CustomerBranchStatisticsAdminDTO getStatistics(Long id) {
        CustomerBranchStatisticsAdminDTO customerBranchStatisticsAdminDTO = new CustomerBranchStatisticsAdminDTO();
        customerBranchStatisticsAdminDTO.setDeviceCount(customerBranchDeviceRepository.countAllByCustomerBranchId(id));
        customerBranchStatisticsAdminDTO.setFiscalModuleCount(customerBranchFiscalModuleRepository.countAllByCustomerBranchId(id));
        customerBranchStatisticsAdminDTO.setFiscalizationCount(fiscalizationRepository.countAllByCustomerBranchId(id));
        return customerBranchStatisticsAdminDTO;
    }

    private void checkForPromoManager(CustomerBranchFilter filter) {
        if (SecurityUtils.isCurrentUserInRole(Role.PROMO_MANAGER.getRoleCode())) {
            List<Long> employeeDistrictIds = regionalAssignmentRepository.getEmployeeDistrictIds(SecurityUtils.getCurrentUserId());
            if (!CollectionUtils.isEmpty(employeeDistrictIds)) {
                filter.setDistrictIds(employeeDistrictIds);
            }
        }
    }
}
