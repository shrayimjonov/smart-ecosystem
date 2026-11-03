package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.apay.APayCategory;
import uz.uzkassa.cts.domain.base.BaseEntity;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.apay.category.APayCategoryDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.changelog.ChangeLogListDTO;
import uz.uzkassa.cts.dto.customer.*;
import uz.uzkassa.cts.dto.customerbranch.rent.CustomerBranchRentDTO;
import uz.uzkassa.cts.dto.payload.FilterPayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.sync.SyncCustomerBranchListDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.ClickhouseFilter;
import uz.uzkassa.cts.filter.CompanyCustomerBranchFilter;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.mappings.AddressMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqAPayProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.ValidationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class CustomerBranchOldServiceImpl extends BaseServiceImpl<CustomerBranch, Long> implements CustomerBranchOldService {

    private final CustomerBranchRepository customerBranchRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final FileUploadService fileUploadService;
    private final ActivityTypeRepository activityTypeRepository;
    private final ApayCategoryRepository apayCategoryRepository;
    private final DistrictRepository districtRepository;
    private final RegionRepository regionRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final OfdApplicationRepository ofdApplicationRepository;
    private final RabbitMqSmartPosProducer rabbitMqSmartPosProducer;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;
    private final RabbitMqAPayProducer rabbitMqAPayProducer;
    private final SocialAddressService socialAddressService;
    private final NeighborhoodRepository neighborhoodRepository;
    private final CustomerBranchScannerRepository scannerRepository;
    private final ChangeLogService changeLogService;
    private final CustomerBranchRentService customerBranchRentService;
    private final AddressMapper addressMapper;


    public CustomerBranchOldServiceImpl(CustomerBranchRepository customerBranchRepository,
                                        CustomerRepository customerRepository,
                                        BranchRepository branchRepository,
                                        UserRepository userRepository,
                                        FileUploadService fileUploadService,
                                        ActivityTypeRepository activityTypeRepository,
                                        ApayCategoryRepository apayCategoryRepository, DistrictRepository districtRepository,
                                        RegionRepository regionRepository,
                                        FiscalizationRepository fiscalizationRepository,
                                        OfdApplicationRepository ofdApplicationRepository,
                                        RabbitMqSmartPosProducer rabbitMqSmartPosProducer,
                                        CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                        CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository,
                                        RabbitMqAPayProducer rabbitMqAPayProducer,
                                        SocialAddressService socialAddressService,
                                        NeighborhoodRepository neighborhoodRepository,
                                        CustomerBranchScannerRepository scannerRepository,
                                        ChangeLogService changeLogService,
                                        CustomerBranchRentService customerBranchRentService,
                                        AddressMapper addressMapper) {
        super(customerBranchRepository);
        this.customerBranchRepository = customerBranchRepository;
        this.customerRepository = customerRepository;
        this.branchRepository = branchRepository;
        this.addressMapper = addressMapper;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
        this.activityTypeRepository = activityTypeRepository;
        this.apayCategoryRepository = apayCategoryRepository;
        this.districtRepository = districtRepository;
        this.regionRepository = regionRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.ofdApplicationRepository = ofdApplicationRepository;
        this.rabbitMqSmartPosProducer = rabbitMqSmartPosProducer;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.customerBranchFiscalModuleRepository = customerBranchFiscalModuleRepository;
        this.rabbitMqAPayProducer = rabbitMqAPayProducer;
        this.socialAddressService = socialAddressService;
        this.neighborhoodRepository = neighborhoodRepository;
        this.scannerRepository = scannerRepository;
        this.changeLogService = changeLogService;
        this.customerBranchRentService = customerBranchRentService;
    }

    @Override
    public CustomerBranchOldDTO create(CustomerBranchOldDTO customerBranchOldDTO) {
        Customer customer = customerRepository.findById(customerBranchOldDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        for (CustomerBranch customerBranch : customer.getCustomerBranches()) {
            if (customerBranch.getName().equalsIgnoreCase(customerBranchOldDTO.getName())) {
                throw validationService.entityAlreadyExistExceptionThrow(BRANCH, NAME, customerBranchOldDTO.getName()).get();
            }
        }
        CustomerBranch customerBranch = new CustomerBranch();
        customerBranch.setBranchId(SecurityUtils.getBranchId());
        customerBranch.setCustomerId(customer.getId());
        customerBranch.setName(customerBranchOldDTO.getName());
        customerBranch.setBrand(customerBranchOldDTO.getBrand());
        customerBranch.setPhone(customerBranchOldDTO.getPhone());
        if (customerBranchOldDTO.getDeviceType() != null) {
            customerBranch.setDeviceType(customerBranchOldDTO.getDeviceType());
        }
        if (customerBranchOldDTO.getActivityTypeId() != null) {
            customerBranch.setActivityTypeId(customerBranchOldDTO.getActivityTypeId());
            customerBranch.setActivityType(activityTypeRepository.findById(customerBranchOldDTO.getActivityTypeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(TYPE)));
        }
        if (customerBranchOldDTO.getApayCategoryId() != null) {
            customerBranch.setAPayCategoryId(customerBranchOldDTO.getApayCategoryId());
            customerBranch.setAPayCategory(apayCategoryRepository.findById(customerBranchOldDTO.getApayCategoryId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CATEGORY)));
        }
        if (customerBranchOldDTO.getDeviceType() != null && EnumSet.of(DeviceType.TERMINAL, DeviceType.SOFTWARE).contains(customerBranchOldDTO.getDeviceType())) {
            customerBranch.setStatus(CustomerBranchStatus.ACTIVE);
        } else {
            customerBranch.setStatus(CustomerBranchStatus.PENDING);
        }
        if (StringUtils.isNotEmpty(customerBranchOldDTO.getCadastreNumber())) {
            customerBranch.setCadastreNumber(customerBranchOldDTO.getCadastreNumber());
        }
        BaseAddress baseAddress = addressMapper.toEntity(customerBranchOldDTO.getAddress());
        if (StringUtils.isEmpty(baseAddress.getApartment())) {
            baseAddress.setApartment("-");
        }
        if (StringUtils.isEmpty(baseAddress.getHouse())) {
            baseAddress.setHouse("-");
        }
        customerBranch.setBaseAddress(baseAddress);

        save(customerBranch);
        if (customerBranchOldDTO.getRent() != null && StringUtils.isNotEmpty(customerBranchOldDTO.getRent().getRentNumber())) {
            CustomerBranchRentDTO customerBranchRentDTO = customerBranchOldDTO.getRent();
            customerBranchRentDTO.setCustomerBranchId(customerBranch.getId());
            customerBranchRentService.create(customerBranchRentDTO);
        }

        customerBranchOldDTO.setId(customerBranch.getId());

        rabbitMqAPayProducer.createCustomerBranch(new Payload(customerBranch.getId()));
        if (CollectionUtils.isNotEmpty(customerBranchOldDTO.getSocialAddresses())) {
            socialAddressService.createBatch(EntityTypeEnum.CUSTOMER_BRANCH, customerBranch.getId(), customerBranchOldDTO.getSocialAddresses());
        }
        return customerBranchOldDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerBranchListDTO> list(CustomerBranchFilter filter) {
        Branch branch = SecurityUtils.getBranchId() != null ? branchRepository.findById(SecurityUtils.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH)) : null;
        List<CustomerBranchListDTO> result = new ArrayList<>();
        ResultList<CustomerBranch> resultList = customerBranchRepository.getList(filter, branch);
        resultList.getList().forEach(customerBranch -> {
            CustomerBranchListDTO customerBranchListDTO = new CustomerBranchListDTO();
            customerBranchListDTO.setId(customerBranch.getId());
            customerBranchListDTO.setName(customerBranch.getName());
            customerBranchListDTO.setBrand(customerBranch.getBrand());
            customerBranchListDTO.setPhone(customerBranch.getPhone());
            customerBranchListDTO.setStatus(customerBranch.getStatus());
            customerBranchListDTO.setOfdStatus(customerBranch.getOfdStatus());
            customerBranchListDTO.setOfdId(customerBranch.getOfdId());
            customerBranchListDTO.setCadastreNumber(customerBranch.getCadastreNumber());
            customerBranchListDTO.setCustomer(customerBranch.getCustomer().toCommonDTO());
            customerBranchListDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
            customerBranchListDTO.setHasAct(customerBranch.isHasAct());
            customerBranchListDTO.setHasInvoicePayment(customerBranch.isHasInvoicePayment());
            customerBranchListDTO.setActivityType(customerBranch.getActivityType().toCommonDTO());
            customerBranchListDTO.setParentActivityType(customerBranch.getActivityType().getParent().toCommonDTO());
            if (customerBranch.getAPayCategoryId() != null) {
                customerBranchListDTO.setApayCategory(customerBranch.getAPayCategory().toCommonDTO());
            }
            customerBranchListDTO.setFiscalModuleTotal(customerBranch.getFiscalModules().size());
            customerBranchListDTO.setDeviceTotal(customerBranch.getDevices().size());
            customerBranchListDTO.setFiles(fileUploadService.list(customerBranch.getId(), EntityTypeEnum.CUSTOMER_BRANCH.name()));
            if (customerBranch.getDeviceType() != null) {
                customerBranchListDTO.setDeviceType(customerBranch.getDeviceType().getName());
            }
            if (StringUtils.isNotEmpty(customerBranch.getAddress().getLatitude()) && StringUtils.isNotEmpty(customerBranch.getAddress().getLongitude()) && !customerBranchListDTO.getFiles().isEmpty()) {
                if (OfdStatus.REJECT.equals(customerBranch.getOfdStatus()) && customerBranch.getOfdId() != null) {
                    customerBranchListDTO.setReSync(true);
                } else if (OfdStatus.REJECT.equals(customerBranch.getOfdStatus())) {
                    customerBranchListDTO.setSync(true);
                } else if (customerBranch.getOfdStatus() == null && customerBranch.getOfdId() == null) {
                    customerBranchListDTO.setSync(true);
                }
            }

            result.add(customerBranchListDTO);
        });
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerBranchListMapDTO> listForMap(CompanyCustomerBranchFilter filter) {
        return customerBranchRepository.listForMap(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminCustomerBranchListDTO> getAll(CompanyCustomerBranchFilter filter) {
        ResultList<CustomerBranch> resultList = customerBranchRepository.getAllList(filter);
        List<AdminCustomerBranchListDTO> result = new ArrayList<>();
        AddressMapper addressMapper = CustomerBranchOldServiceImpl.this.addressMapper;
        resultList.getList().forEach(customerBranch -> {
            AdminCustomerBranchListDTO customerBranchListDTO = new AdminCustomerBranchListDTO();
            if (customerBranch.getBranch() != null) {
                customerBranchListDTO.setBranch(customerBranch.getBranch().toCommonDTO());
            }
            customerBranchListDTO.setCustomer(customerBranch.getCustomer().toCommonDTO());
            customerBranchListDTO.setTin(customerBranch.getCustomer().getTin());
            customerBranchListDTO.setId(customerBranch.getId());
            customerBranchListDTO.setName(customerBranch.getName());
            customerBranchListDTO.setBrand(customerBranch.getBrand());
            customerBranchListDTO.setPhone(customerBranch.getPhone());
            customerBranchListDTO.setStatus(customerBranch.getStatus());
            customerBranchListDTO.setOfdStatus(customerBranch.getOfdStatus());
            customerBranchListDTO.setOfdId(customerBranch.getOfdId());
            customerBranchListDTO.setCadastreNumber(customerBranch.getCadastreNumber());
            customerBranchListDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
            customerBranchListDTO.setHasAct(customerBranch.isHasAct());
            customerBranchListDTO.setHasInvoicePayment(customerBranch.isHasInvoicePayment());
            customerBranchListDTO.setRegistrationDate(customerBranch.getCreatedDate());
            customerBranchListDTO.setRent(customerBranchRentService.getByCustomerBranchId(customerBranch.getId()));
            if (customerBranch.getDeviceType() != null) {
                customerBranchListDTO.setDeviceType(customerBranch.getDeviceType().getName());
            }
            if (StringUtils.isNotEmpty(customerBranch.getAddress().getLatitude()) && StringUtils.isNotEmpty(customerBranch.getAddress().getLongitude())) {
                Long fileTotal = fileUploadService.listTotal(customerBranch.getId(), EntityTypeEnum.CUSTOMER_BRANCH.name());
                if (fileTotal > 0) {
                    if ((OfdStatus.REJECT.equals(customerBranch.getOfdStatus()) && customerBranch.getOfdId() != null)
                            || (customerBranch.getOfdId() != null && customerBranch.isResync())) {
                        customerBranchListDTO.setReSync(true);
                    } else if (OfdStatus.REJECT.equals(customerBranch.getOfdStatus())) {
                        customerBranchListDTO.setSync(true);
                    } else if (customerBranch.getOfdStatus() == null && customerBranch.getOfdId() == null) {
                        customerBranchListDTO.setSync(true);
                    }
                }
            }
            result.add(customerBranchListDTO);
        });

        return new PageImpl<>
                (result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminCustomerBranchListByFiscalDevicesDTO> getAllByFiscalDevices(CompanyCustomerBranchFilter filter) {
        if (filter.getStatus() != null && UserStatus.get(filter.getStatus()) == null) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, filter.getStatus()).get();
        }
        if (filter.getOfdStatus() != null && UserStatus.get(filter.getOfdStatus()) == null) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, filter.getOfdStatus()).get();
        }

        ResultList<AdminCustomerBranchListByFiscalDevicesDTO> resultList = customerBranchRepository.getAllByFiscalDevices(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommonDTO> lookupForOfd(CustomerBranchFilter filter) {
        return customerBranchRepository.getListForOfd(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommonDTO> lookUp(BaseFilter filter) {
        return customerBranchRepository.getLookUp(filter).getList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerBranchLookUpDTO getItems(BaseFilter filter) {
        Customer customer = customerRepository.findByTin(filter.getTin());
        if (customer == null) {
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();
        }

        List<CustomerBranchItemsDTO> branchItemsDTOList = customerBranchRepository.getCustomerBranchByTinOrSerialNumber(filter);
        CustomerBranchLookUpDTO result = new CustomerBranchLookUpDTO();
        result.setCustomerName(customer.getName());
        result.setCustomerBranches(branchItemsDTOList);
        return result;
    }

    @Override
    public CustomerBranchUpdateDTO update(CustomerBranchUpdateDTO customerBranchUpdateDTO) {
        CustomerBranch customerBranch = customerBranchRepository.findById(customerBranchUpdateDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
        List<Fiscalization> fiscalizations = fiscalizationRepository.findAllByCustomerIdAndCustomerBranchIdAndStatus(customerBranch.getCustomerId(), customerBranch.getId(), DeviceStatus.FISCAL_ACTIVE);
        if (CollectionUtils.isNotEmpty(fiscalizations) && SecurityUtils.isCurrentUserInRoles(Role.EMPLOYEE.getRoleCode(), Role.CALL_CENTER.getRoleCode())) {
            fiscalizations.forEach(fiscalization -> {
                if (fiscalization.getOfdApplication() != null) {
                    switch (fiscalization.getOfdApplication().getApplicationType()) {
                        case DEREGISTER, REGISTER -> {
                            if (!OfdStatus.REJECT.equals(fiscalization.getOfdApplication().getOfdStatus())) {
                                throw validationService.badRequestExceptionThrow(CHECK_CUSTOMER_BRANCH).get();
                            }
                        }
                    }
                }
            });
        }
        if (StringUtils.isNotEmpty(customerBranchUpdateDTO.getName())) {
            customerBranch.setName(customerBranchUpdateDTO.getName());
        }
        customerBranch.setBrand(customerBranchUpdateDTO.getBrand());
        if (ValidationUtil.isNotNull(customerBranchUpdateDTO.getPhone())) {
            customerBranch.setPhone(customerBranchUpdateDTO.getPhone());
        }
        if (customerBranchUpdateDTO.getRent() != null) {
            customerBranchUpdateDTO.getRent().setCustomerBranchId(customerBranch.getId());
            customerBranchRentService.createOrUpdate(customerBranchUpdateDTO.getRent());
            customerBranch.setCadastreNumber(customerBranchUpdateDTO.getCadastreNumber());
        }
        if (StringUtils.isNotEmpty(customerBranchUpdateDTO.getCadastreNumber())) {
            CustomerBranchRentDTO rentDTO = customerBranchRentService.getByCustomerBranchId(customerBranchUpdateDTO.getId());
            if (rentDTO != null) {
                customerBranchRentService.updateStatus(CommonStatus.IN_ACTIVE, customerBranchUpdateDTO.getId());
            }
            customerBranch.setCadastreNumber(customerBranchUpdateDTO.getCadastreNumber());
        }
        if (customerBranchUpdateDTO.getActivityTypeId() != null) {
            ActivityType newActivityType = activityTypeRepository.findById(customerBranchUpdateDTO.getActivityTypeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(TYPE));
            customerBranch.setActivityTypeId(customerBranchUpdateDTO.getActivityTypeId());
            customerBranch.setActivityType(newActivityType);
        }
        if (customerBranchUpdateDTO.getApayCategoryId() != null) {
            APayCategory aPayCategory = apayCategoryRepository.findById(customerBranchUpdateDTO.getApayCategoryId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CATEGORY));
            customerBranch.setAPayCategoryId(aPayCategory.getId());
            customerBranch.setAPayCategory(aPayCategory);
        }
        if (customerBranchUpdateDTO.getDeviceType() != null) {
            boolean checkType = customerBranchDeviceRepository.existsByIdAndDeviceTypeIn(customerBranchUpdateDTO.getId(), Arrays.stream(DeviceType.values())
                    .filter(status -> !status.equals(customerBranchUpdateDTO.getDeviceType()))
                    .collect(Collectors.toList()));
            if (checkType) {
                customerBranch.setDeviceType(DeviceType.TERMINAL_FISCAL);
            } else {
                customerBranch.setDeviceType(customerBranchUpdateDTO.getDeviceType());
            }
        }
        if (customerBranchUpdateDTO.getAddress() != null) {
            BaseAddressDTO baseAddressDTO = customerBranchUpdateDTO.getAddress();
            BaseAddress baseAddress = customerBranch.getAddress();
            if (baseAddressDTO.getRegionId() != null) {
                baseAddress.setRegionId(baseAddressDTO.getRegionId());
                regionRepository.findById(baseAddressDTO.getRegionId()).ifPresent(baseAddress::setRegion);
            }
            if (baseAddressDTO.getDistrictId() != null) {
                baseAddress.setDistrictId(baseAddressDTO.getDistrictId());
                districtRepository.findById(baseAddressDTO.getDistrictId()).ifPresent(baseAddress::setDistrict);
            }
            if (baseAddressDTO.getNeighborhoodId() != null) {
                baseAddress.setNeighborhoodId(baseAddressDTO.getNeighborhoodId());
                neighborhoodRepository.findById(baseAddressDTO.getNeighborhoodId()).ifPresent(baseAddress::setNeighborhood);
            }
            if (StringUtils.isNotEmpty(baseAddressDTO.getLatitude())) {
                baseAddress.setLatitude(baseAddressDTO.getLatitude());
            }
            if (StringUtils.isNotEmpty(baseAddressDTO.getLongitude())) {
                baseAddress.setLongitude(baseAddressDTO.getLongitude());
            }
            if (StringUtils.isNotEmpty(baseAddressDTO.getLandmark())) {
                baseAddress.setLandmark(baseAddressDTO.getLandmark());
            }
            if (StringUtils.isNotEmpty(baseAddressDTO.getApartment())) {
                baseAddress.setApartment(baseAddressDTO.getApartment());
            }
            if (StringUtils.isNotEmpty(baseAddressDTO.getHouse())) {
                baseAddress.setHouse(baseAddressDTO.getHouse());
            }
            if (StringUtils.isNotEmpty(baseAddressDTO.getStreet())) {
                baseAddress.setStreet(baseAddressDTO.getStreet());
            }
            customerBranch.setBaseAddress(baseAddress);
        }

        customerBranch.setResync(customerBranch.getOfdId() != null);
        customerBranchRepository.save(customerBranch);

        if (CollectionUtils.isNotEmpty(customerBranchUpdateDTO.getSocialAddresses())) {
            socialAddressService.updateBatch(EntityTypeEnum.CUSTOMER_BRANCH, customerBranch.getId(), customerBranchUpdateDTO.getSocialAddresses());
        }
        rabbitMqAPayProducer.updateCustomerBranch(new Payload(customerBranch.getId()));

        if (CollectionUtils.isNotEmpty(fiscalizations)) {
            fiscalizations.forEach(fiscalization -> {
                OfdApplication ofdApplication = fiscalization.getOfdApplicationId() != null ? ofdApplicationRepository.findById(fiscalization.getOfdApplicationId()).orElse(null) : null;
                if (ofdApplication != null) {
                    if (OfdStatus.ACCEPT.equals(ofdApplication.getOfdStatus()) ||
                            (OfdStatus.REJECT.equals(ofdApplication.getOfdStatus()) && ApplicationType.DEREGISTER.equals(ofdApplication.getApplicationType()))
                        //Bu degani ro'yxatdan chiqarishga ariza yuborilgan, lekin NIC rad etgan. Ro'yxatdan chiqarmagan. Kassa hali faol.
                    ) {
                        rabbitMqSmartPosProducer.updateCustomer(fiscalization.getId());
                    }
                }
            });
        }

        return customerBranchUpdateDTO;
    }

    @Override
    public CustomerBranchConfirmDocDTO confirmDoc(CustomerBranchConfirmDocDTO customerBranchConfirmDocDTO) {
        CustomerBranch customerBranch = customerBranchRepository.findById(customerBranchConfirmDocDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
        customerBranch.setHasAct(customerBranchConfirmDocDTO.isHasAct());
        customerBranch.setHasInvoicePayment(customerBranchConfirmDocDTO.isHasInvoicePayment());
        customerBranchRepository.save(customerBranch);

        return customerBranchConfirmDocDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerBranchDetailDTO get(Long id) {
        CustomerBranch customerBranch = customerBranchRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        CustomerBranchDetailDTO customerBranchDetailDTO = new CustomerBranchDetailDTO();
        customerBranchDetailDTO.setId(customerBranch.getId());
        customerBranchDetailDTO.setName(customerBranch.getName());
        customerBranchDetailDTO.setBrand(customerBranch.getBrand());
        customerBranchDetailDTO.setActivityType(customerBranch.getActivityType().toCommonDTO());
        customerBranchDetailDTO.setParentActivityType(customerBranch.getActivityType().getParent().toCommonDTO());
        if (customerBranch.getAPayCategoryId() != null) {
            customerBranchDetailDTO.setApayCategory(new APayCategoryDTO(customerBranch.getAPayCategory().getId(),
                    customerBranch.getAPayCategory().getName(),
                    customerBranch.getAPayCategory().getApayId(),
                    customerBranch.getAPayCategory().getParentId()
            ));
            apayCategoryRepository.findFirstByApayId(customerBranch.getAPayCategory().getParentId()).ifPresent(parentApayCategory -> customerBranchDetailDTO.setParentApayCategory(new APayCategoryDTO(parentApayCategory.getId(),
                    parentApayCategory.getName(),
                    parentApayCategory.getApayId(),
                    parentApayCategory.getParentId()
            )));
        }

        customerBranchDetailDTO.setPhone(customerBranch.getPhone());
        customerBranchDetailDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
        customerBranchDetailDTO.setDeviceTotal(customerBranchDeviceRepository.totalByCustomerBranchId(id).intValue());
        customerBranchDetailDTO.setFiscalModuleTotal(customerBranchFiscalModuleRepository.totalByCustomerBranchId(id).intValue());
        customerBranchDetailDTO.setFiscalModuleTotal(customerBranchFiscalModuleRepository.totalByCustomerBranchId(id).intValue());
        customerBranchDetailDTO.setCustomer(new CustomerShortDTO(customerBranch.getCustomer().getId(), customerBranch.getCustomer().getName(), customerBranch.getCustomer().getTin()));
        customerBranchDetailDTO.setOfdId(customerBranch.getOfdId());
        customerBranchDetailDTO.setOfdStatus(customerBranch.getOfdStatus());
        customerBranchDetailDTO.setCadastreNumber(customerBranch.getCadastreNumber());

        if (StringUtils.isNotEmpty(customerBranch.getAddress().getLatitude()) && StringUtils.isNotEmpty(customerBranch.getAddress().getLongitude())) {
            Long fileTotal = fileUploadService.listTotal(customerBranch.getId(), EntityTypeEnum.CUSTOMER_BRANCH.name());
            if (fileTotal > 0) {
                if ((OfdStatus.REJECT.equals(customerBranch.getOfdStatus()) && customerBranch.getOfdId() != null)
                        || (customerBranch.getOfdId() != null && customerBranch.isResync())) {
                    customerBranchDetailDTO.setReSync(true);
                } else if (OfdStatus.REJECT.equals(customerBranch.getOfdStatus())) {
                    customerBranchDetailDTO.setSync(true);
                } else if (customerBranch.getOfdStatus() == null && customerBranch.getOfdId() == null) {
                    customerBranchDetailDTO.setSync(true);
                }
            }
        }
        customerBranchDetailDTO.setSocialAddresses(socialAddressService.getByEntityTypeAndEntityId(EntityTypeEnum.CUSTOMER_BRANCH, id));
        customerBranchDetailDTO.setRent(customerBranchRentService.getByCustomerBranchId(customerBranch.getId()));
        return customerBranchDetailDTO;
    }

    @Override
    public void activate(Long id, String documentType, MultipartFile file) {

        if (!(DocumentType.CADASTRE.getCode().equals(documentType) || DocumentType.LEASE_CONTRACT.getCode().equals(documentType))) {
            throw validationService.badRequestExceptionThrow(INCORRECT_INPUT, documentType).get();
        }

        CustomerBranch customerBranch = customerBranchRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
        fileUploadService.upload(file, id, EntityTypeEnum.CUSTOMER_BRANCH.name(), documentType);

        customerBranch.setStatus(CustomerBranchStatus.ACTIVE);
        customerBranchRepository.save(customerBranch);
    }

    @Override
    public void uploadDocument(Long id, String documentType, MultipartFile file) {
        if (!(DocumentType.CADASTRE.getCode().equals(documentType) || DocumentType.LEASE_CONTRACT.getCode().equals(documentType))) {
            throw validationService.badRequestExceptionThrow(INCORRECT_INPUT, documentType).get();
        }

        CustomerBranch customerBranch = customerBranchRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
        fileUploadService.upload(file, id, EntityTypeEnum.CUSTOMER_BRANCH.name(), documentType);

        customerBranch.setResync(customerBranch.getOfdId() != null);
        customerBranch.setStatus(CustomerBranchStatus.ACTIVE);
        customerBranchRepository.save(customerBranch);
    }

    @Override
    public void deleteDocument(Long id, Long fileId) {
        CustomerBranch customerBranch = customerBranchRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
        FileUpload fileUpload = fileUploadService.findById(fileId);
        fileUpload.setDeleted(true);
        fileUploadService.save(fileUpload);

        customerBranch.setResync(true);
        customerBranchRepository.save(customerBranch);
    }

    @Override
    public Page<ChangeLogListDTO> getHistories(CustomerBranchFilter filter) {
        Customer customer = customerRepository.findById(filter.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        ClickhouseFilter clickhouseFilter = new ClickhouseFilter();
        clickhouseFilter.setEntityList(customer.getCustomerBranches().stream().map(BaseEntity::getId).collect(Collectors.toList()));
        clickhouseFilter.setEntityType(EntityTypeEnum.CUSTOMER_BRANCH);
        return changeLogService.getList(clickhouseFilter);
    }

    @Override
    public Page<CustomerBranchShortListDTO> listByEmployee(BaseFilter filter) {
        ResultList<CustomerBranchShortListDTO> resultList = customerBranchRepository.getListByEmployee(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Transactional(readOnly = true)
    public Long getListCountToSync() {
        if (!isSuperAdmin() && getCompanyId() != null)
            return customerBranchRepository.countByCompanyId(getCompanyId());
        else
            return customerBranchRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<SyncCustomerBranchListDTO> getListToSync(BaseFilter filter) {
        return new PageImpl<>
                (customerBranchRepository.getListToSync(filter),
                        filter.getOrderedPageable(), 0L);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommonDTO> getEmptyCustomerBranches(Long customerId) {
        List<CommonDTO> result = new ArrayList<>();
        List<CustomerBranch> customerBranches = customerBranchRepository.findEmptyList(customerId);
        customerBranches.forEach(customerBranch -> result.add(customerBranch.toCommonDTO()));
        return result;
    }

    @Override
    public void syncWithSmartPos(Long id) {
        rabbitMqSmartPosProducer.syncCustomerBranch(id);
    }

    @Override
    public void syncCustomerBranchListAPay() {
        long count = customerBranchRepository.countCustomerBranches().longValue();
        int limit = 500;
        int page = (int) Math.ceil((double) count / limit);
        for (int i = 0; i <= page; i++) {
            FilterPayload filter = new FilterPayload();
            filter.setPage(i);
            filter.setSize(limit);
            rabbitMqAPayProducer.sendCustomerBranches(filter);
        }
    }

    @Override
    public Long connectScanner(Long customerBranchId, Long scannerId, MultipartFile file) {
        CustomerBranch customerBranch = findById(customerBranchId);
        if (file != null) {
            UploadFileResponse response =
                    fileUploadService.upload(file, customerBranchId, EntityTypeEnum.CUSTOMER_BRANCH_SCANNER.name(), DocumentType.CUSTOMER_BRANCH_SCANNER.name());
            customerBranch.setScannerFileName(response.getFileName());
            customerBranch.setScannerFileUri(response.getFileDownloadUri());
        }
        CustomerBranchScanner scanner = scannerRepository.findById(scannerId).orElseThrow(validationService.entityNotFoundExceptionThrow(SCANNER));
        customerBranch.setScannerId(scannerId);
        customerBranch.setCustomerBranchScanner(scanner);
        save(customerBranch);
        return customerBranch.getCustomerId();
    }

    @Override
    public Long removeScanner(Long id) {
        CustomerBranch customerBranch = findById(id);
        customerBranch.setScannerId(null);
        customerBranch.setScannerFileUri(null);
        customerBranch.setScannerFileName(null);
        save(customerBranch);
        return customerBranch.getCustomerId();
    }
}
