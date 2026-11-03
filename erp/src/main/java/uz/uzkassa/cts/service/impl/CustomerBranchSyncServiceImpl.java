package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.Relation;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.customerbranch.CustomerBranchDTO;
import uz.uzkassa.cts.dto.sync.ActivityTypeSyncDTO;
import uz.uzkassa.cts.dto.sync.AddressSyncDTO;
import uz.uzkassa.cts.dto.sync.customerbranch.CustomerBranchSyncDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.enums.ReferenceTypeValue;
import uz.uzkassa.cts.enums.RelationType;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.integrations.ApayClient;
import uz.uzkassa.cts.integrations.HorecaClient;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.CustomerBranchService;
import uz.uzkassa.cts.service.CustomerBranchSyncService;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.RelationService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CustomerBranchSyncServiceImpl extends BaseServiceImpl<CustomerBranch, Long> implements CustomerBranchSyncService {

    private final CustomerBranchRepository customerBranchRepository;
    private final CustomerRepository customerRepository;
    private final ReferenceRepository referenceRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final HorecaClient horecaClient;
    private final ApayClient apayClient;
    private final CustomerBranchService customerBranchService;
    private final RelationService relationService;
    private final FileUploadService fileUploadService;

    public CustomerBranchSyncServiceImpl(CustomerBranchRepository customerBranchRepository, CustomerRepository customerRepository, ReferenceRepository referenceRepository, ActivityTypeRepository activityTypeRepository, RegionRepository regionRepository, DistrictRepository districtRepository, NeighborhoodRepository neighborhoodRepository, HorecaClient horecaClient, ApayClient apayClient, CustomerBranchService customerBranchService, RelationService relationService, FileUploadService fileUploadService) {
        super(customerBranchRepository);
        this.customerBranchRepository = customerBranchRepository;
        this.customerRepository = customerRepository;
        this.referenceRepository = referenceRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.horecaClient = horecaClient;
        this.apayClient = apayClient;
        this.customerBranchService = customerBranchService;
        this.relationService = relationService;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public Page<CustomerBranchSyncDTO> getList(CustomerBranchFilter filter) {
        ResultList<CustomerBranch> resultList = customerBranchRepository.getResultList(filter);
        List<CustomerBranchSyncDTO> result = resultList
                .getList()
                .stream()
                .map(this::wrapToSyncDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    public CustomerBranchSyncDTO get(Long id) {
        return this.wrapToSyncDTO(customerBranchRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH)));
    }

    @Override
    @Transactional
    public Long sync(CustomerBranchSyncDTO customerBranchSyncDTO) {
        Customer customer = customerRepository.findByTin(customerBranchSyncDTO.getCompany().getTin());
        CustomerBranchDTO customerBranchDTO = new CustomerBranchDTO();
        customerBranchDTO.setFromQuickRegister(true);
        customerBranchDTO.setCustomerId(customer.getId());
        customerBranchDTO.setName(customerBranchSyncDTO.getName());
        customerBranchDTO.setBrand(customerBranchSyncDTO.getBrand());
        customerBranchDTO.setCadastreNumber(customerBranchSyncDTO.getCadastreNumber());
        if (customerBranchSyncDTO.getActivityType() != null && customerBranchSyncDTO.getActivityType().getCode() != null && customerBranchSyncDTO.getActivityType().getParentCode() != null) {
            activityTypeRepository.findByCodeAndParent_Code(customerBranchSyncDTO.getActivityType().getCode(), customerBranchSyncDTO.getActivityType().getParentCode()).ifPresent(activityType -> customerBranchDTO.setActivityTypeId(activityType.getId()));
        }
        if (customerBranchSyncDTO.getPhone() != null) {
            Reference phoneType = referenceRepository.findFirstByTypeAndCodeAndDeletedIsFalse(ReferenceType.PHONE_TYPE, ReferenceTypeValue.WORK.name());
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setName(customerBranchSyncDTO.getPhone());
            contactDTO.setTypeId(phoneType.getId());
            contactDTO.setPrimary(true);
            customerBranchDTO.setPhones(List.of(contactDTO));
        }
        if (customerBranchSyncDTO.getAddress() != null) {
            BaseAddressDTO baseAddressDTO = new BaseAddressDTO();
            Region region = regionRepository.findByCode(customerBranchSyncDTO.getAddress().getRegionCode());
            baseAddressDTO.setRegionId(region.getId());

            if (customerBranchSyncDTO.getAddress().getDistrictCode() != null) {
                districtRepository.findFirstByRegionIdAndCode(region.getId(), customerBranchSyncDTO.getAddress().getDistrictCode()).ifPresent(district -> baseAddressDTO.setDistrictId(district.getId()));
            }
            if (customerBranchSyncDTO.getAddress().getNeighborhoodCode() != null) {
                neighborhoodRepository.findByCode(customerBranchSyncDTO.getAddress().getNeighborhoodCode()).ifPresent(neighborhood -> baseAddressDTO.setNeighborhoodId(neighborhood.getId()));
            }
            baseAddressDTO.setLandmark(customerBranchSyncDTO.getAddress().getLandmark());
            baseAddressDTO.setStreet(customerBranchSyncDTO.getAddress().getStreet());
            baseAddressDTO.setHouse(customerBranchSyncDTO.getAddress().getHouse());
            baseAddressDTO.setApartment(customerBranchSyncDTO.getAddress().getApartment());
            baseAddressDTO.setLongitude(customerBranchSyncDTO.getAddress().getLongitude());
            baseAddressDTO.setLatitude(customerBranchSyncDTO.getAddress().getLatitude());
            customerBranchDTO.setAddress(baseAddressDTO);
        }

        Relation relation = relationService.get(customerBranchSyncDTO.getRelationId(), customerBranchSyncDTO.getRelationType(), EntityTypeEnum.CUSTOMER_BRANCH);
        CustomerBranch customerBranch = null;
        if (relation != null) {
            customerBranch = customerBranchRepository.findById(relation.getFromId()).orElse(null);
        }
        if (customerBranch == null) {
            return customerBranchService.create(customerBranchDTO);
        } else {
            return customerBranchService.update(customerBranch.getId(), customerBranchDTO);
        }
    }

    @Override
    @Transactional
    public Long sync(Long id, RelationType relationType) {
        CustomerBranch customerBranch = customerBranchRepository.getReferenceById(id);
        CustomerBranchSyncDTO customerBranchSyncDTO = this.wrapToSyncDTO(customerBranch);
        if (RelationType.HORECA == relationType) {
            String toId = horecaClient.syncBranch(customerBranchSyncDTO);
            relationService.create(customerBranch.getId(), RelationType.CTS, toId, relationType, customerBranch.getEntityType());
        } else if (RelationType.A_PAY == relationType) {
            String toId = apayClient.syncBranch(customerBranchSyncDTO);
            relationService.create(customerBranch.getId(), RelationType.CTS, toId, relationType, customerBranch.getEntityType());
        }
        return id;
    }

    private CustomerBranchSyncDTO wrapToSyncDTO(CustomerBranch customerBranch) {
        CustomerBranchSyncDTO customerBranchSyncDTO = new CustomerBranchSyncDTO();
        customerBranchSyncDTO.setId(customerBranch.getId());
        customerBranchSyncDTO.setName(customerBranch.getName());
        customerBranchSyncDTO.setBrand(customerBranch.getBrand());
        customerBranchSyncDTO.setPhone(customerBranch.getPhone());
        customerBranchSyncDTO.setCadastreNumber(customerBranch.getCadastreNumber());
        customerBranchSyncDTO.setCompany(customerBranch.getCustomer().toShortDTO());
        if (customerBranch.getActivityTypeId() != null) {
            customerBranchSyncDTO.setActivityType(new ActivityTypeSyncDTO(customerBranch.getActivityType().getCode(), customerBranch.getActivityType().getParent().getCode()));
        }
        AddressSyncDTO addressSyncDTO = new AddressSyncDTO();
        addressSyncDTO.setRegionCode(customerBranch.getAddress().getRegion().getCode());
        addressSyncDTO.setDistrictCode(customerBranch.getAddress().getDistrict().getCode());
        if (customerBranch.getAddress().getNeighborhoodId() != null) {
            addressSyncDTO.setNeighborhoodCode(customerBranch.getAddress().getNeighborhood().getCode());
        }
        addressSyncDTO.setLandmark(customerBranch.getAddress().getLandmark());
        addressSyncDTO.setStreet(customerBranch.getAddress().getStreet());
        addressSyncDTO.setHouse(customerBranch.getAddress().getHouse());
        addressSyncDTO.setApartment(customerBranch.getAddress().getApartment());
        addressSyncDTO.setLongitude(customerBranch.getAddress().getLongitude());
        addressSyncDTO.setLatitude(customerBranch.getAddress().getLatitude());
        customerBranchSyncDTO.setAddress(addressSyncDTO);

        customerBranchSyncDTO.setDocuments(fileUploadService.getList(customerBranch.getEntityType(), customerBranch.getId()));
        return customerBranchSyncDTO;
    }
}
