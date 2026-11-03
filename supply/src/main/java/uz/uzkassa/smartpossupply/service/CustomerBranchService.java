package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.constants.MessageConstants;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.equipment.Equipment;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.address.yandex.YandexLocationNameRequest;
import uz.uzkassa.smartpossupply.dto.address.yandex.YandexLocationNameResponse;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.branch.BranchAddressLookupDTO;
import uz.uzkassa.smartpossupply.dto.branch.SmartposBranchListDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchDetailDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchListDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchLookUpDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.MobileCustomerBranchDetailDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.MobileCustomerBranchListDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDetailDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentShortDetailDTO;
import uz.uzkassa.smartpossupply.dto.payload.SyncCustomerBranchPayload;
import uz.uzkassa.smartpossupply.dto.payload.UpdateCompanyEquipmentCountPayload;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyBranchDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.integration.SmartposClient;
import uz.uzkassa.smartpossupply.integration.SoliqClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCompanyProducer;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCustomerBranchProducer;
import uz.uzkassa.smartpossupply.repository.CustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.DistrictRepository;
import uz.uzkassa.smartpossupply.repository.EquipmentRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.RegionRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerBranchService extends BaseService {

    CustomerBranchRepository customerBranchRepository;
    EquipmentRepository equipmentRepository;
    SoliqClient soliqClient;
    RabbitMqCustomerBranchProducer rabbitMqCustomerBranchProducer;
    RabbitMqCompanyProducer rabbitMqCompanyProducer;
    SmartposClient smartposClient;
    YandexService yandexService;
    PartnerRepository partnerRepository;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    ValidationContext validationContext;
    WarehouseRepository warehouseRepository;

    @Transactional
    public Long create(CustomerBranchDTO customerBranchDTO) {
        validationContext.validate(customerBranchDTO);
        this.validate(customerBranchDTO);
        return this.createAndReturn(customerBranchDTO).getId();
    }

    @Transactional
    public CustomerBranch createAndReturn(CustomerBranchDTO customerBranchDTO) {
        this.validate(customerBranchDTO);
        return this.createEntity(customerBranchDTO);
    }

    @Transactional
    public CustomerBranch createEntity(CustomerBranchDTO customerBranchDTO) {
        CustomerBranch customerBranch = new CustomerBranch();
        customerBranch.setName(customerBranchDTO.getName());
        customerBranch.setAddress(addressMapper.toEntity(customerBranchDTO.getAddress()));
        customerBranch.setCustomerId(customerBranchDTO.getCustomerId());
        customerBranch.setBranchId(customerBranchDTO.getBranchId());
        customerBranch.setWarehouseId(customerBranchDTO.getWarehouseId());
        customerBranch.setPhone(customerBranchDTO.getPhone());
        customerBranch.setMain(customerBranchDTO.isMain());
        customerBranch.setMarketCode(customerBranchDTO.getMarketCode());
        customerBranch.setMobileId(customerBranchDTO.getMobileId());
        customerBranch.setExternalId(customerBranchDTO.getExternalId());
        customerBranchRepository.saveAndFlush(customerBranch);
        if (customerBranchDTO.isMain()) {
            customerBranchRepository.updateSetMainFalseByCompanyId(customerBranch.getCustomerId(), customerBranch.getId());
        }
        if (!CollectionUtils.isEmpty(customerBranchDTO.getEquipments())) {
            equipmentRepository.saveAll(customerBranchDTO.getEquipments().stream().map(equipmentDTO -> {
                Equipment equipment = new Equipment();
                equipment.setName(equipmentDTO.getName());
                equipment.setBrand(equipmentDTO.getBrand());
                equipment.setInventoryNumber(equipmentDTO.getInventoryNumber());
                equipment.setType(equipmentDTO.getType());
                equipment.setCustomerBranchId(customerBranch.getId());
                return equipment;
            }).toList());
            rabbitMqCompanyProducer.updateEquipmentCount(new UpdateCompanyEquipmentCountPayload(customerBranchDTO.getCustomerId(), customerBranchDTO.getEquipments().size()));
        }
        return customerBranch;
    }

    @Transactional
    public CustomerBranch createOrGet(CustomerBranchDTO customerBranchDTO) {
        CustomerBranch customerBranch = null;
        if (StringUtils.isNotEmpty(customerBranchDTO.getBranchCode())) {
            customerBranch = customerBranchRepository.findFirstByCustomerIdAndCustomerBranchNumber(customerBranchDTO.getCustomerId(), customerBranchDTO.getBranchCode()).orElse(null);
        }
        if (customerBranch == null && StringUtils.isNotEmpty(customerBranchDTO.getName())) {
            customerBranch = customerBranchRepository.findFirstByCustomerIdAndNameIgnoreCase(customerBranchDTO.getCustomerId(), customerBranchDTO.getName()).orElse(null);
        }
        if (customerBranch == null) {
            customerBranch = createAndReturn(customerBranchDTO);
        }
        return customerBranch;
    }

    private void validate(CustomerBranchDTO customerBranchDTO) {
        validateBranchAndWarehouseOwnership(customerBranchDTO);
        companyRepository.findById(customerBranchDTO.getCustomerId()).ifPresentOrElse(customer -> {
            if (customerBranchRepository.existsByCustomerIdAndDeletedIsFalseAndNameIgnoreCase(
                    customer.getId(),
                    customerBranchDTO.getName()
            )) {
                throw validationService.conflictExceptionThrow("Клиент филиал с таким названием " + customerBranchDTO.getName() + " уже существует").get();
            }
            if (StringUtils.isNotEmpty(customerBranchDTO.getBranchCode())) {
                if (customerBranchRepository.existsByCustomerIdAndCustomerBranchNumber(customer.getId(), customerBranchDTO.getBranchCode())) {
                    throw validationService.conflictExceptionThrow("Клиент филиал с таким код " + customerBranchDTO.getBranchCode() + " уже существует").get();
                }
            }
            if (StringUtils.isNotEmpty(customerBranchDTO.getMarketCode()) && customerBranchRepository.existsByCustomerIdAndMarketCode(customerBranchDTO.getCustomerId(), customerBranchDTO.getMarketCode())) {
                throw validationService.conflictExceptionThrow("Клиент филиал с таким маркет код " + customerBranchDTO.getBranchCode() + " уже существует").get();
            }
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", customerBranchDTO.getCustomerId()).get();
        });
    }

    @Transactional
    public Long update(Long id, CustomerBranchDTO customerBranchDTO) {
        validationContext.validate(customerBranchDTO);
        validateBranchAndWarehouseOwnership(customerBranchDTO);
        return customerBranchRepository
                .findById(id).map(customerBranch -> {
                    if (customerBranchRepository.existsByCustomerIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(
                            customerBranch.getCustomerId(), customerBranch.getName(), customerBranch.getId()
                    )) {
                        throw validationService.conflictExceptionThrow("Клиент филиал с таким названием " + customerBranchDTO.getName() + " уже существует").get();
                    }
                    if (StringUtils.isNotEmpty(customerBranchDTO.getMarketCode()) && customerBranchRepository.existsByCustomerIdAndMarketCodeAndIdNot(customerBranchDTO.getCustomerId(), customerBranchDTO.getMarketCode(), id)) {
                        throw validationService.conflictExceptionThrow("Клиент филиал с таким маркет код " + customerBranchDTO.getBranchCode() + " уже существует").get();
                    }
                    customerBranch.setBranchId(customerBranchDTO.getBranchId());
                    customerBranch.setWarehouseId(customerBranchDTO.getWarehouseId());
                    customerBranch.setName(customerBranchDTO.getName());
                    customerBranch.setExternalId(customerBranchDTO.getExternalId());
                    if (customerBranch.getAddress() != null) {
                        customerBranch.setAddress(addressMapper.merge(customerBranchDTO.getAddress(), customerBranch.getAddress()));
                    } else {
                        customerBranch.setAddress(addressMapper.toEntity(customerBranchDTO.getAddress()));
                    }
                    customerBranch.setOrderingBlocked(customerBranchDTO.isOrderingBlocked());
                    customerBranch.setPhone(customerBranchDTO.getPhone());
                    customerBranch.setMain(customerBranchDTO.isMain());
                    customerBranch.setMarketCode(customerBranchDTO.getMarketCode());
                    boolean exists = equipmentRepository.existsByCustomerBranchIdAndDeletedIsFalse(customerBranch.getId());
                    equipmentRepository.deleteAllByCustomerBranchId(customerBranch.getId());
                    if (!CollectionUtils.isEmpty(customerBranchDTO.getEquipments())) {
                        equipmentRepository.saveAll(customerBranchDTO.getEquipments().stream().map(equipmentDTO -> {
                            Equipment equipment = new Equipment();
                            equipment.setName(equipmentDTO.getName());
                            equipment.setBrand(equipmentDTO.getBrand());
                            equipment.setInventoryNumber(equipmentDTO.getInventoryNumber());
                            equipment.setType(equipmentDTO.getType());
                            equipment.setCustomerBranchId(customerBranch.getId());
                            return equipment;
                        }).toList());
                        if (!exists) {
                            rabbitMqCompanyProducer.updateEquipmentCount(new UpdateCompanyEquipmentCountPayload(customerBranchDTO.getCustomerId(), customerBranchDTO.getEquipments().size()));
                        }
                    } else {
                        if (exists) {
                            rabbitMqCompanyProducer.updateEquipmentCount(new UpdateCompanyEquipmentCountPayload(customerBranchDTO.getCustomerId(), -1));
                        }
                    }
                    customerBranchRepository.save(customerBranch);
                    if (customerBranchDTO.isMain()) {
                        customerBranchRepository.updateSetMainFalseByCompanyId(customerBranch.getCustomerId(), id);
                    }
                    return customerBranch.getId();
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerBranch.class.getSimpleName(), "id", id));
    }

    public CustomerBranchDetailDTO get(Long id) {
        return customerBranchRepository.findById(id).map(customerBranch -> {
            CustomerBranchDetailDTO customerBranchDetailDTO = new CustomerBranchDetailDTO();
            customerBranchDetailDTO.setId(customerBranch.getId());
            customerBranchDetailDTO.setExternalId(customerBranch.getExternalId());
            customerBranchDetailDTO.setName(customerBranch.getName());
            customerBranchDetailDTO.setCustomerBranchNumber(customerBranch.getCustomerBranchNumber());
            customerBranchDetailDTO.setMarketCode(customerBranch.getMarketCode());
            customerBranchDetailDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
            customerBranchDetailDTO.setCustomer(customerBranch.getCustomer().toCommonDTO());
            customerBranchDetailDTO.setBranch(Optional.ofNullable(customerBranch.getBranch()).map(Branch::toCommonDTO).orElse(null));
            customerBranchDetailDTO.setWarehouse(Optional.ofNullable(customerBranch.getWarehouse()).map(Warehouse::toCommonDTO).orElse(null));
            customerBranchDetailDTO.setStatus(customerBranch.getStatus());
            customerBranchDetailDTO.setPhone(customerBranch.getPhone());
            customerBranchDetailDTO.setCurrentBalance(customerBranch.getCurrentBalance());
            customerBranchDetailDTO.setMain(customerBranch.isMain());
            customerBranchDetailDTO.setOrderingBlocked(customerBranch.isOrderingBlocked());
            customerBranchDetailDTO.setEquipments(customerBranch.getEquipments().stream().map(equipment -> {
                EquipmentDetailDTO equipmentDetailDTO = new EquipmentDetailDTO();
                equipmentDetailDTO.setId(equipment.getId());
                equipmentDetailDTO.setName(equipment.getName());
                equipmentDetailDTO.setInventoryNumber(equipment.getInventoryNumber());
                equipmentDetailDTO.setBrand(equipment.getBrand());
                equipmentDetailDTO.setType(equipment.getType());
                equipmentDetailDTO.setCustomerBranch(equipment.getCustomerBranch().toCommonDTO());
                return equipmentDetailDTO;
            }).toList());
            return customerBranchDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerBranch.class.getSimpleName(), "id", id));
    }

    public MobileCustomerBranchDetailDTO getForMobile(Long id) {
        return customerBranchRepository.findById(id).map(customerBranch -> {
            MobileCustomerBranchDetailDTO customerBranchDetailDTO = new MobileCustomerBranchDetailDTO();
            customerBranchDetailDTO.setId(customerBranch.getId());
            customerBranchDetailDTO.setName(customerBranch.getName());
            customerBranchDetailDTO.setCustomerBranchNumber(customerBranch.getCustomerBranchNumber());
            customerBranchDetailDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
            customerBranchDetailDTO.setCustomer(customerBranch.getCustomer().toCommonDTO());
            customerBranchDetailDTO.setBranch(Optional.ofNullable(customerBranch.getBranch()).map(Branch::toCommonDTO).orElse(null));
            customerBranchDetailDTO.setWarehouse(Optional.ofNullable(customerBranch.getWarehouse()).map(Warehouse::toCommonDTO).orElse(null));
            customerBranchDetailDTO.setStatus(customerBranch.getStatus());
            customerBranchDetailDTO.setPhone(customerBranch.getPhone());
            customerBranchDetailDTO.setOrderingBlocked(customerBranch.isOrderingBlocked());
            customerBranchDetailDTO.setEquipments(customerBranch.getEquipments().stream().map(equipment -> {
                EquipmentDetailDTO equipmentDetailDTO = new EquipmentDetailDTO();
                equipmentDetailDTO.setId(equipment.getId());
                equipmentDetailDTO.setName(equipment.getName());
                equipmentDetailDTO.setInventoryNumber(equipment.getInventoryNumber());
                equipmentDetailDTO.setBrand(equipment.getBrand());
                equipmentDetailDTO.setType(equipment.getType());
                equipmentDetailDTO.setCustomerBranch(equipment.getCustomerBranch().toCommonDTO());
                return equipmentDetailDTO;
            }).toList());
            return customerBranchDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerBranch.class.getSimpleName(), "id", id));
    }

    public Page<CustomerBranchListDTO> getList(CustomerBranchFilter filter) {
        ResultList<CustomerBranch> resultList = customerBranchRepository.getResultList(filter);
        List<CustomerBranchListDTO> result = resultList.getList().stream().map(customerBranch -> {
            CustomerBranchListDTO customerBranchListDTO = new CustomerBranchListDTO();
            customerBranchListDTO.setId(customerBranch.getId());
            customerBranchListDTO.setExternalId(customerBranch.getExternalId());
            customerBranchListDTO.setName(customerBranch.getName());
            customerBranchListDTO.setCustomerBranchNumber(customerBranch.getCustomerBranchNumber());
            customerBranchListDTO.setMarketCode(customerBranch.getMarketCode());
            customerBranchListDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
            customerBranchListDTO.setBranch(Optional.ofNullable(customerBranch.getBranch()).map(Branch::toCommonDTO).orElse(null));
            customerBranchListDTO.setWarehouse(Optional.ofNullable(customerBranch.getWarehouse()).map(Warehouse::toCommonDTO).orElse(null));
            customerBranchListDTO.setStatus(customerBranch.getStatus());
            customerBranchListDTO.setPhone(customerBranch.getPhone());
            customerBranchListDTO.setCurrentBalance(customerBranch.getCurrentBalance());
            customerBranchListDTO.setMain(customerBranch.isMain());
            customerBranchListDTO.setOrderingBlocked(customerBranch.isOrderingBlocked());
            if (!CollectionUtils.isEmpty(customerBranch.getEquipments())) {
                List<EquipmentShortDetailDTO> equipments = customerBranch.getEquipments().stream().map(equipment -> {
                    EquipmentShortDetailDTO equipmentDetailDTO = new EquipmentShortDetailDTO();
                    equipmentDetailDTO.setType(equipment.getType());
                    return equipmentDetailDTO;
                }).toList();
                customerBranchListDTO.setEquipments(equipments);
            }
            return customerBranchListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<MobileCustomerBranchDetailDTO> getListForOffline(CustomerBranchFilter filter) {
        ResultList<CustomerBranch> resultList = customerBranchRepository.getResultList(filter);
        List<MobileCustomerBranchDetailDTO> result = resultList
                .getList()
                .stream()
                .map(customerBranch -> {
                    MobileCustomerBranchDetailDTO customerBranchDetailDTO = new MobileCustomerBranchDetailDTO();
                    customerBranchDetailDTO.setId(customerBranch.getId());
                    customerBranchDetailDTO.setName(customerBranch.getName());
                    customerBranchDetailDTO.setCustomerBranchNumber(customerBranch.getCustomerBranchNumber());
                    customerBranchDetailDTO.setCreatedDate(customerBranch.getCreatedDate());
                    customerBranchDetailDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
                    customerBranchDetailDTO.setCustomer(customerBranch.getCustomer().toCommonDTO());
                    customerBranchDetailDTO.setBranch(Optional.ofNullable(customerBranch.getBranch()).map(Branch::toCommonDTO).orElse(null));
                    customerBranchDetailDTO.setWarehouse(Optional.ofNullable(customerBranch.getWarehouse()).map(Warehouse::toCommonDTO).orElse(null));
                    customerBranchDetailDTO.setStatus(customerBranch.getStatus());
                    customerBranchDetailDTO.setPhone(customerBranch.getPhone());
                    customerBranchDetailDTO.setOrderingBlocked(customerBranch.isOrderingBlocked());
                    customerBranchDetailDTO.setEquipments(customerBranch.getEquipments().stream().map(equipment -> {
                        EquipmentDetailDTO equipmentDetailDTO = new EquipmentDetailDTO();
                        equipmentDetailDTO.setId(equipment.getId());
                        equipmentDetailDTO.setName(equipment.getName());
                        equipmentDetailDTO.setInventoryNumber(equipment.getInventoryNumber());
                        equipmentDetailDTO.setBrand(equipment.getBrand());
                        equipmentDetailDTO.setType(equipment.getType());
                        equipmentDetailDTO.setCustomerBranch(equipment.getCustomerBranch().toCommonDTO());
                        return equipmentDetailDTO;
                    }).toList());
                    return customerBranchDetailDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }


    public Page<MobileCustomerBranchListDTO> getListForMobile(CustomerBranchFilter filter) {
        ResultList<CustomerBranch> resultList = customerBranchRepository.getResultList(filter);
        List<MobileCustomerBranchListDTO> result = resultList.getList().stream().map(customerBranch -> {
            MobileCustomerBranchListDTO customerBranchListDTO = new MobileCustomerBranchListDTO();
            customerBranchListDTO.setId(customerBranch.getId());
            customerBranchListDTO.setName(customerBranch.getName());
            customerBranchListDTO.setCustomerBranchNumber(customerBranch.getCustomerBranchNumber());
            customerBranchListDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
            customerBranchListDTO.setBranch(Optional.ofNullable(customerBranch.getBranch()).map(Branch::toCommonDTO).orElse(null));
            customerBranchListDTO.setWarehouse(Optional.ofNullable(customerBranch.getWarehouse()).map(Warehouse::toCommonDTO).orElse(null));
            customerBranchListDTO.setStatus(customerBranch.getStatus());
            customerBranchListDTO.setPhone(customerBranch.getPhone());
            customerBranchListDTO.setCurrentBalance(customerBranch.getCurrentBalance());
            customerBranchListDTO.setOrderingBlocked(customerBranch.isOrderingBlocked());
            return customerBranchListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<CommonCodeDTO> lookUp(CustomerBranchFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        filter.setCompanyId(getCurrentCompanyId());
        return customerBranchRepository.lookUp(filter).stream().map(CustomerBranch::toCommonCodeDTO).toList();
    }

    public List<BranchAddressLookupDTO> lookUpWithAddress(CustomerBranchFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        filter.setCompanyId(getCurrentCompanyId());
        if (filter.getCustomerId() == null && !StringUtils.isEmpty(filter.getTin())) {
            companyRepository.findFirstByTinAndDeletedIsFalse(filter.getTin())
                    .ifPresent(customer -> filter.setCustomerId(customer.getId()));
        }
        return customerBranchRepository.lookUp(filter).stream().map(customerBranch -> {
            BranchAddressLookupDTO branchAddressLookupDTO = new BranchAddressLookupDTO();
            branchAddressLookupDTO.setId(customerBranch.getId());
            branchAddressLookupDTO.setName(customerBranch.getName());
            branchAddressLookupDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
            branchAddressLookupDTO.setOfficialName(customerBranch.getOfficialName());
            branchAddressLookupDTO.setBranchNumber(customerBranch.getCustomerBranchNumber());
            branchAddressLookupDTO.setMain(customerBranch.isMain());
            return branchAddressLookupDTO;
        }).toList();
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        customerBranchRepository.findById(id).map(customerBranch -> {
            customerBranch.setStatus(status);
            customerBranchRepository.save(customerBranch);
            return customerBranch;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerBranch.class.getSimpleName(), "id", id));
    }

    public List<CustomerBranchLookUpDTO> lookUpForPlanRoute(CustomerBranchFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        return customerBranchRepository.lookUp(filter)
                .stream()
                .map(customerBranch -> {
                    CustomerBranchLookUpDTO customerBranchLookUpDTO = new CustomerBranchLookUpDTO();
                    customerBranchLookUpDTO.setId(customerBranch.getId());
                    customerBranchLookUpDTO.setName(customerBranch.getName());
                    customerBranchLookUpDTO.setAddress(addressMapper.toDetailDTO(customerBranch.getAddress()));
                    return customerBranchLookUpDTO;
                }).toList();
    }

    @Transactional
    public void updateNumber(Long id, String number) {
        CustomerBranch customerBranch = customerBranchRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerBranch.class.getSimpleName(), "id", id));
        if (customerBranchRepository.existsByCustomerIdAndCustomerBranchNumberAndIdNot(customerBranch.getCustomerId(), number, id)) {
            throw validationService.badRequestExceptionThrow("В системе есть торговый точка с номером " + number).get();
        }
        customerBranch.setCustomerBranchNumber(number);
        customerBranchRepository.save(customerBranch);
    }

    @Transactional
    public void syncQueue(SyncCustomerBranchPayload payload) {
        TaxCompanyBranchDTO[] customerBranchList = soliqClient.getBranchesByCompany(payload.getTin(), false);
        if (customerBranchList != null) {
            List<CustomerBranch> customerBranches = new ArrayList<>();
            Arrays.stream(customerBranchList)
                    .forEach(customerBranchDTO -> {
                        if (customerBranchDTO.getNs10Code() != null && customerBranchDTO.getNs11Code() != null && StringUtils.isNotEmpty(customerBranchDTO.getLongitude()) && StringUtils.isNotEmpty(customerBranchDTO.getLatitude())) {
                            CustomerBranch customerBranch = customerBranchRepository.findFirstByCustomerIdAndCustomerBranchNumber(payload.getCustomerId(), customerBranchDTO.getBranchNum()).orElseGet(CustomerBranch::new);
                            customerBranch.setOfficialName(customerBranchDTO.getName());
                            customerBranch.setCustomerBranchNumber(customerBranchDTO.getBranchNum());
                            customerBranch.setCustomerId(payload.getCustomerId());
                            Address address = Optional.ofNullable(customerBranch.getAddress()).orElseGet(Address::new);
                            regionRepository.findByCode(customerBranchDTO.getNs10Code()).ifPresent(region -> address.setRegionId(region.getId()));
                            districtRepository.findByRegionCodeAndCode(customerBranchDTO.getNs10Code(), customerBranchDTO.getNs11Code())
                                    .ifPresent(district -> address.setDistrictId(district.getId()));
                            address.setLongitude(customerBranchDTO.getLongitude());
                            address.setLatitude(customerBranchDTO.getLatitude());
                            if (StringUtils.isNotEmpty(customerBranchDTO.getLongitude()) && StringUtils.isNotEmpty(customerBranchDTO.getLatitude())) {
                                YandexLocationNameResponse addressName = yandexService.getLocationName(new YandexLocationNameRequest(Double.valueOf(customerBranchDTO.getLongitude()), Double.valueOf(customerBranchDTO.getLatitude())));
                                address.setHouse(addressName.getName());
                            }
                            customerBranches.add(customerBranch);
                        }
                    });
            if (!CollectionUtils.isEmpty(customerBranches)) {
                customerBranchRepository.saveAll(customerBranches);
            }
        }
    }

    @Transactional
    public void syncTradeQueue(SyncCustomerBranchPayload payload) {
        SmartposBranchListDTO customerBranchList = smartposClient.getBranches(payload.getTin());
        if (customerBranchList != null) {
            List<CustomerBranch> customerBranches = new ArrayList<>();
            customerBranchList.getBranches()
                    .forEach(customerBranchDTO -> {
                        CustomerBranch customerBranch = customerBranchRepository.findFirstByCustomerIdAndName(payload.getCustomerId(), customerBranchDTO.getName()).orElseGet(CustomerBranch::new);
                        customerBranch.setName(customerBranchDTO.getName());
                        customerBranch.setCustomerId(payload.getCustomerId());
                        customerBranch.setTrade(true);
                        customerBranch.setCommittent(customerBranchList.isCommitent());
                        if (customerBranchDTO.getAddress() != null) {
                            Address address = Optional.ofNullable(customerBranch.getAddress()).orElseGet(Address::new);
                            if (customerBranchDTO.getAddress().getRegion() != null && StringUtils.isNotEmpty(customerBranchDTO.getAddress().getRegion().getCode())) {
                                regionRepository.findByCode(Long.valueOf(customerBranchDTO.getAddress().getRegion().getCode())).ifPresent(region -> address.setRegionId(region.getId()));
                            }
                            if (customerBranchDTO.getAddress().getDistrict() != null && StringUtils.isNotEmpty(customerBranchDTO.getAddress().getDistrict().getCode())) {
                                districtRepository.findByRegionCodeAndCode(Long.valueOf(customerBranchDTO.getAddress().getRegion().getCode()), Long.valueOf(customerBranchDTO.getAddress().getDistrict().getCode()))
                                        .ifPresent(district -> address.setDistrictId(district.getId()));
                            }
                            address.setLongitude(customerBranchDTO.getAddress().getLongitude());
                            address.setLatitude(customerBranchDTO.getAddress().getLatitude());
                            address.setApartment(customerBranchDTO.getAddress().getApartment());
                            address.setHouse(customerBranchDTO.getAddress().getHouse());
                            customerBranch.setAddress(address);
                        }
                        customerBranches.add(customerBranch);
                    });
            if (!CollectionUtils.isEmpty(customerBranches)) {
                customerBranchRepository.saveAll(customerBranches);
            }
        }
    }

    /**
     * Sync branches from the tax committee.
     */
    @Transactional
    public void sync(Long id) {
        companyRepository.findById(id).map(company -> {
                    rabbitMqCustomerBranchProducer.send(new SyncCustomerBranchPayload(company.getTin(), company.getId()));
                    return id;
                }
        ).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    /**
     * Sync branches from the project Smartpos.
     */
    @Transactional
    public void syncTradeBranches() {
        Long customerId = getCurrentCompanyId();
        if (customerId == null) {
            throw validationService.badRequestExceptionThrow("Register your company").get();
        }
        companyRepository.findById(customerId).map(company -> {
                    rabbitMqCustomerBranchProducer.sendTradeSync(new SyncCustomerBranchPayload(company.getTin(), company.getId()));
                    return customerId;
                }
        ).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", customerId));
    }

    @Transactional
    public void createDefaultCustomerBranch() {
        rabbitMqCustomerBranchProducer.sendCreateDefaultBranch(new SyncCustomerBranchPayload());
    }

    @Transactional
    public void createDefaultCustomerBranchDivideCompaniesList(SyncCustomerBranchPayload payload) {
        long count = companyRepository.count();
        for (int i = 0; i < count; i += COMMON_BATCH_SIZE) {
            rabbitMqCustomerBranchProducer.sendCreateDefaultBranchItem(new SyncCustomerBranchPayload(i));
        }
    }

    @Transactional
    public void createDefaultCustomerBranch(SyncCustomerBranchPayload payload) {
        List<CustomerBranch> customerBranches = new ArrayList<>();
        for (Company company : companyRepository.findAllByLimit(COMMON_BATCH_SIZE, payload.getIndex())) {
            if (partnerRepository.existsByCustomerIdAndStatus(company.getId(), CompanyStatus.ACTIVE)) {
                if (!customerBranchRepository.existsByCustomerIdAndMainIsTrue(company.getId())) {
                    if (!customerBranchRepository.existsByCustomerId(company.getId())) {
                        CustomerBranchDTO customerBranchDTO = new CustomerBranchDTO();
                        customerBranchDTO.setCustomerId(company.getId());
                        customerBranchDTO.setName(company.getName());
                        customerBranchDTO.setPhone(company.getPhone());
                        customerBranchDTO.setMain(true);
                        if (company.getAddress() != null) {
                            customerBranchDTO.setAddress(addressMapper.toDTO(company.getAddress()));
                        }
                        try {
                            this.create(customerBranchDTO);
                        } catch (Exception e) {
                            log.error("Creating default customer branch error: {}", e.getMessage());
                        }
                    } else {
                        CustomerBranch customerBranch = customerBranchRepository.findFirstByCustomerIdAndStatusOrderById(company.getId(), CommonStatus.ACTIVE).orElse(null);
                        if (customerBranch != null) {
                            customerBranch.setMain(true);
                            customerBranches.add(customerBranch);
                        }
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(customerBranches)) {
            customerBranchRepository.saveAll(customerBranches);
        }
    }

    private void validateBranchAndWarehouseOwnership(CustomerBranchDTO customerBranchDTO) {
        Long companyId = SecurityUtils.getCompanyId();
        Long branchId = customerBranchDTO.getBranchId();
        Long warehouseId = customerBranchDTO.getWarehouseId();
        if (branchId != null
                && !branchRepository.existsByIdAndCompanyIdAndDeletedIsFalse(branchId, companyId)) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.BRANCH_NOT_BELONG_TO_COMPANY)).get();
        }
        if (warehouseId != null
                && !warehouseRepository.existsByIdAndBranchIdAndDeletedIsFalse(warehouseId, branchId)) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.WAREHOUSE_NOT_BELONG_TO_COMPANY)).get();
        }
    }

    boolean validateCustomerBranchBelongsToCustomer(Long customerBranchId, Long customerId) {
        return customerBranchRepository.existsByIdAndCustomerIdAndDeletedIsFalse(customerBranchId, customerId);
    }
}
