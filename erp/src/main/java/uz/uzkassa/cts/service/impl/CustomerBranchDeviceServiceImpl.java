package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.applicationfile.GenerateApplicationFileDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customerBranchDevicePaymentType.CustomerBranchDevicePaymentTypeDTO;
import uz.uzkassa.cts.dto.customerbranchdevice.CustomerBranchDeviceListAdminDTO;
import uz.uzkassa.cts.dto.customerbranchdevice.CustomerBranchDeviceRequestDTO;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.*;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdGenerateApplicationFileDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdGenerateApplicationFileRequestDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CustomerBranchDeviceListFilter;
import uz.uzkassa.cts.mappings.CustomerBranchDeviceMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqCustomerBranchDeviceProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class CustomerBranchDeviceServiceImpl extends BaseServiceImpl<CustomerBranchDevice, Long> implements CustomerBranchDeviceService {

    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerDeviceRepository customerDeviceRepository;
    private final CustomerBranchRepository customerBranchRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final DeviceModelRepository deviceModelRepository;
    private final DeviceRepository deviceRepository;
    private final OfdService ofdService;
    private final WarehouseHistoryService warehouseHistoryService;
    private final CompanyDeviceService companyDeviceService;
    private final DeviceService deviceService;
    private final BranchDeviceService branchDeviceService;
    private final CustomerDeviceService customerDeviceService;
    private final DeviceSuspendHistoryOldService deviceSuspendHistoryOldService;
    private final ActOfTransmissionDetailRepository actOfTransmissionDetailRepository;
    private final ServicePaymentHistoryRepository servicePaymentHistoryRepository;
    private final LauncherService launcherService;
    private final FreePeriodHistoryService freePeriodHistoryService;
    private final RabbitMqCustomerBranchDeviceProducer rabbitMqCustomerBranchDeviceProducer;
    private final CustomerBranchDeviceMapper customerBranchDeviceMapper;
    private final CustomerBranchDevicePaymentTypeService customerBranchDevicePaymentTypeService;
    private final CacheService cacheService;
    private final LogHistoryService logHistoryService;

    public CustomerBranchDeviceServiceImpl(CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                           CustomerDeviceRepository customerDeviceRepository,
                                           CustomerBranchRepository customerBranchRepository,
                                           FiscalizationRepository fiscalizationRepository,
                                           OfdService ofdService,
                                           DeviceModelRepository deviceModelRepository,
                                           DeviceRepository deviceRepository,
                                           WarehouseHistoryService warehouseHistoryService,
                                           CompanyDeviceService companyDeviceService,
                                           DeviceService deviceService,
                                           BranchDeviceService branchDeviceService,
                                           CustomerDeviceService customerDeviceService,
                                           DeviceSuspendHistoryOldService deviceSuspendHistoryOldService,
                                           FileUploadService fileUploadService,
                                           ActOfTransmissionDetailRepository actOfTransmissionDetailRepository,
                                           ServicePaymentHistoryRepository servicePaymentHistoryRepository,
                                           LauncherService launcherService,
                                           FreePeriodHistoryService freePeriodHistoryService,
                                           RabbitMqCustomerBranchDeviceProducer rabbitMqCustomerBranchDeviceProducer,
                                           CustomerBranchDeviceMapper customerBranchDeviceMapper,
                                           CustomerBranchDevicePaymentTypeService customerBranchDevicePaymentTypeService, CacheService cacheService, LogHistoryService logHistoryService) {
        super(customerBranchDeviceRepository);
        this.customerBranchRepository = customerBranchRepository;
        this.customerDeviceRepository = customerDeviceRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.deviceModelRepository = deviceModelRepository;
        this.deviceRepository = deviceRepository;
        this.ofdService = ofdService;
        this.warehouseHistoryService = warehouseHistoryService;
        this.companyDeviceService = companyDeviceService;
        this.branchDeviceService = branchDeviceService;
        this.customerDeviceService = customerDeviceService;
        this.deviceService = deviceService;
        this.deviceSuspendHistoryOldService = deviceSuspendHistoryOldService;
        this.actOfTransmissionDetailRepository = actOfTransmissionDetailRepository;
        this.servicePaymentHistoryRepository = servicePaymentHistoryRepository;
        this.launcherService = launcherService;
        this.freePeriodHistoryService = freePeriodHistoryService;
        this.rabbitMqCustomerBranchDeviceProducer = rabbitMqCustomerBranchDeviceProducer;
        this.customerBranchDeviceMapper = customerBranchDeviceMapper;
        this.customerBranchDevicePaymentTypeService = customerBranchDevicePaymentTypeService;
        this.cacheService = cacheService;
        this.logHistoryService = logHistoryService;
    }

    @Override
    public void add(CustomerBranchDeviceDTO customerBranchDeviceDTO) {
        CustomerBranch customerBranch = customerBranchRepository.findById(customerBranchDeviceDTO.getCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        customerBranchDeviceDTO.getDevices().forEach(deviceModelSharedDTO -> {

            Device device = deviceRepository.findByModelIdAndSerialNumber(deviceModelSharedDTO.getModelId(), deviceModelSharedDTO.getSerialNumber());
            if (device == null) {
                throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
            }

            if (customerBranchDeviceRepository.totalByDeviceId(device.getId()).intValue() > 0) {
                throw validationService.entityAlreadyExistExceptionThrow(DEVICE, SERIAL_NUMBER, device.getSerialNumber()).get();
            }

            CustomerDevice customerDevice = customerDeviceRepository.findFirstByCustomerIdAndDeviceIdAndStatus(customerBranch.getCustomerId(), device.getId(), DeviceStatus.REGISTERED);
            if (customerDevice == null) {
                throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
            }

            customerDevice.setStatus(DeviceStatus.TRANSFERRED);
            customerDeviceRepository.save(customerDevice);
            warehouseHistoryService.create(device.getSerialNumber(), customerDevice.getId(), EntityTypeEnum.CUSTOMER_DEVICE, DeviceStatus.TRANSFERRED);

            CustomerBranchDevice customerBranchDevice = new CustomerBranchDevice();
            customerBranchDevice.setCustomerBranchId(customerBranch.getId());
            customerBranchDevice.setDeviceId(device.getId());
            customerBranchDevice.setStatus(DeviceStatus.REGISTERED);

            ActOfTransmissionDetail actOfTransmissionDetail = actOfTransmissionDetailRepository.findFirstByDeviceIdAndActOfTransmission_Contract_CustomerId(customerBranch.getCustomerId(), device.getId());
            if (actOfTransmissionDetail != null) {
                customerBranchDevice.setPaymentType(DevicePaymentType.LEASING);
            }
            customerBranchDeviceRepository.save(customerBranchDevice);
            warehouseHistoryService.create(device.getSerialNumber(), customerBranchDevice.getId(), EntityTypeEnum.CUSTOMER_BRANCH_DEVICE, DeviceStatus.REGISTERED);

            logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, customerDevice.getCustomer().getName(), customerBranch.getName());
            //Unblock launcher KKM
            launcherService.unlock(Collections.singletonList(device.getSerialNumber()));
        });

    }


    public Page<CustomerBranchDeviceListDto> getList(CustomerBranchDeviceListFilter filter) {
        Specification<CustomerBranchDevice> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Path<LocalDate> startDatePath = root.get(CustomerBranchDevice_.START_DATE);
            Path<LocalDate> dueDatePath = root.get(CustomerBranchDevice_.DUE_DATE);

            Join<CustomerBranch, CustomerBranchDevice> customerBranchJoin = root.join(CustomerBranchDevice_.CUSTOMER_BRANCH, JoinType.INNER);
            Join<Customer, CustomerBranch> customerJoin = customerBranchJoin.join(CustomerBranch_.CUSTOMER, JoinType.INNER);
            Path<Long> companyPath = customerJoin.get(Customer_.COMPANY_ID);
            ArrayList<Predicate> list = new ArrayList<>();

            if (filter.getCompanyId() != null) {

                list.add(criteriaBuilder.equal(companyPath, filter.getCompanyId()));
            }
            if (!isSuperAdmin()) {

                list.add(criteriaBuilder.equal(companyPath, getCompanyId()));
            }

            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<Customer> customerPath = root.join(CustomerBranchDevice_.CONTRACT, JoinType.INNER)
                        .join(Contract_.CUSTOMER, JoinType.INNER);
                Path<Device> devicePath = root.join(CustomerBranchDevice_.DEVICE, JoinType.INNER);
                Path<String> tinPath = customerPath.get(Customer_.TIN);
                Path<String> oldTinPath = customerPath.get(Customer_.OLD_TIN);
                Path<String> deviceSerialNumberPath = devicePath.get(Device_.SERIAL_NUMBER);

                Predicate tinPred = criteriaBuilder.like(tinPath, filter.getSearchForQuery());
                Predicate oldTinPred = criteriaBuilder.like(oldTinPath, filter.getSearchForQuery());
                Predicate deviceSerialNumberPred = criteriaBuilder.like(criteriaBuilder.lower(deviceSerialNumberPath), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(tinPred, oldTinPred, deviceSerialNumberPred));
            }

            if (filter.getStartDateBegin() != null)
                list.add(criteriaBuilder.greaterThanOrEqualTo(startDatePath, filter.getStartDateBegin()));
            if (filter.getStartDateEnd() != null)
                list.add(criteriaBuilder.lessThanOrEqualTo(startDatePath, filter.getStartDateEnd()));
            if (filter.getDueDateBegin() != null)
                list.add(criteriaBuilder.greaterThanOrEqualTo(dueDatePath, filter.getDueDateBegin()));
            if (filter.getDuetDateEnd() != null)
                list.add(criteriaBuilder.lessThanOrEqualTo(dueDatePath, filter.getDuetDateEnd()));
            if (filter.getTariffId() != null)
                list.add(criteriaBuilder.equal(root.get(CustomerBranchDevice_.TARIFF_ID), filter.getTariffId()));
            if (filter.getSuspendStatus() != null)
                list.add(criteriaBuilder.equal(root.get(CustomerBranchDevice_.SUSPEND_STATUS), filter.getSuspendStatus()));
            if (filter.getStatus() != null)
                list.add(criteriaBuilder.equal(root.get(CustomerBranchDevice_.STATUS), DeviceStatus.valueOf(filter.getStatus())));

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<CustomerBranchDevice> customerBranchDevices = customerBranchDeviceRepository.findAll(specification, filter.getOrderedPageable());

        return customerBranchDevices.map(customerBranchDeviceMapper::toLstDto);
    }

    @Deprecated
    @Override
    @Transactional(readOnly = true)
    public Page<CustomerBranchDeviceListDTO> list(BaseFilter filter) {
        List<CustomerBranchDeviceListDTO> result = new ArrayList<>();
        ResultList<CustomerBranchDevice> resultList = customerBranchDeviceRepository.getList(filter);
        resultList.getList().forEach(customerBranchDevice -> {
            CustomerBranchDeviceListDTO deviceListDTO = new CustomerBranchDeviceListDTO();
            deviceListDTO.setDeviceSerial(customerBranchDevice.getDevice().getSerialNumber());
            Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndDeviceId(filter.getCustomerId(), filter.getCustomerBranchId(), customerBranchDevice.getDeviceId());
            if (fiscalization != null) {
                deviceListDTO.setFiscalModuleSerial(fiscalization.getFiscalModule().getSerialNumber());
            }
            deviceListDTO.setCustomerBranch(customerBranchDevice.getCustomerBranch().getDTO());
            result.add(deviceListDTO);
        });
        return new PageImpl<>
                (result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SerialNumberDTO> lookUp(BaseFilter filter) {
        ResultList<SerialNumberDTO> resultList = customerBranchDeviceRepository.getLookUp(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SerialNumberDTO> getItems(BaseFilter filter) {
        return customerBranchDeviceRepository.getItems(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDeviceListDTO> getFreeDevices(Long customerId) {
        return customerBranchDeviceRepository.getFreeDevices(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadApplicationFile(GenerateApplicationFileDTO generateApplicationFileDTO) {

        if (StringUtils.isBlank(generateApplicationFileDTO.getDeviceSerial()) || StringUtils.isBlank(generateApplicationFileDTO.getFiscalModuleSerial())) {
            throw validationService.badRequestExceptionThrow(DEVICE_AND_FISCAL_MODULE, generateApplicationFileDTO.getDeviceSerial(), generateApplicationFileDTO.getFiscalModuleSerial()).get();
        }
        CustomerBranch customerBranch = customerBranchRepository.findById(generateApplicationFileDTO.getCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
        if (customerBranch.getOfdId() == null) {
            throw validationService.entityNotFoundExceptionThrow("Confirmation").get();
        }
        String modelCode = SecurityUtils.getModelCode(generateApplicationFileDTO.getDeviceSerial());
        DeviceModel deviceModel = deviceModelRepository.findFirstByCode(modelCode);
        if (deviceModel == null) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE_MODEL).get();
        }
        if (deviceModel.getOfdId() == null) {
            throw validationService.entityNotFoundExceptionThrow(CATEGORY).get();
        }

        OfdGenerateApplicationFileRequestDTO fileGenerateRequestDTO = new OfdGenerateApplicationFileRequestDTO();
        fileGenerateRequestDTO.setOfficeId(customerBranch.getOfdId());
        fileGenerateRequestDTO.setCcmSerialNumber(generateApplicationFileDTO.getDeviceSerial());
        fileGenerateRequestDTO.setTerminalId(generateApplicationFileDTO.getFiscalModuleSerial());
        fileGenerateRequestDTO.setCcmCategoryId(deviceModel.getOfdId());
        fileGenerateRequestDTO.setPhone(StringUtils.isNotEmpty(customerBranch.getPhone()) ? customerBranch.getPhone() : customerBranch.getCustomer().getPhone());
        try {
            log.info("Request to get TAX FILE: {}", fileGenerateRequestDTO);
            OfdGenerateApplicationFileDTO ofdGenerateApplicationFileDTO = ofdService.generateApplicationFile(fileGenerateRequestDTO);
            log.info("Response TAX FILE: {}", ofdGenerateApplicationFileDTO);
            return new ByteArrayResource(Base64.getDecoder().decode(ofdGenerateApplicationFileDTO.getSourceBase64()));
        } catch (
                Exception e) {
            log.error("----------------------------------- downloadApplicationFile ------------------------");
            log.error(e.getMessage());
            if (e instanceof IntegrationException) {
                throw new IntegrationException(e.getMessage(), ((IntegrationException) e).getError());
            }
            throw new IntegrationException("----------------------------------- downloadApplicationFile ------------------------" + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerBranchDeviceListDto> getCustomerBranchDevicesByCustomerId(Long id) {
        List<CustomerBranchDevice> devices = customerBranchDeviceRepository.findByCustomerId(id);
        return devices.stream().map(customerBranchDeviceMapper::toLstDto).collect(Collectors.toList());
    }

    @Override
    public CustomerBranchDeviceListDto changeServiceAllowToTrue(Long customerBranchDeviceId) {
        CustomerBranchDevice customerBranchDevice = findById(customerBranchDeviceId);
        Tariff tariff = customerBranchDevice.getTariff();
        if (tariff.getPeriodType() == PeriodType.YEARLY)
            if (!SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode()))
                throw validationService.badRequestExceptionThrow(DEVICE_TARIFF_PLAN_CHANGE).get();
            else {
                ServicePaymentHistory history = servicePaymentHistoryRepository.findFirstByCustomerBranchDeviceIdAndStartDateAndDueDate(
                        customerBranchDeviceId,
                        customerBranchDevice.getStartDate(),
                        customerBranchDevice.getDueDate());

                ServiceOrderInvoice invoice = history.getServiceOrderInvoice();
                if (invoice != null) {
                    if (XfileStatus.REJECTED == invoice.getXfileStatus() ||
                            XfileStatus.CANCELLED == invoice.getXfileStatus()) {
                        customerBranchDevice.setDueDate(customerBranchDevice.getStartDate());
                        servicePaymentHistoryRepository.delete(history);
                    }
                }
            }
        customerBranchDevice.setServiceAllowed(true);
        customerBranchDevice.setTariffId(null);
        customerBranchDevice.setContractId(null);
        customerBranchDevice = save(customerBranchDevice);
        return customerBranchDeviceMapper.toLstDto(customerBranchDevice);
    }

    @Override
    public CustomerBranchDeviceListDto changeDevicePaymentType(CustomerBranchDeviceChangePaymentTypeDto paymentTypeDto) {
        CustomerBranchDevice customerBranchDevice = findById(paymentTypeDto.getCustomerBranchDeviceId());
        customerBranchDevice.setPaymentType(paymentTypeDto.getPaymentTypeEnum());
        save(customerBranchDevice);
        return customerBranchDeviceMapper.toLstDto(customerBranchDevice);
    }

    @Override
    public void delete(Long id) {
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndDeviceId(customerBranchDevice.getCustomerBranch().getCustomerId(), customerBranchDevice.getCustomerBranchId(), customerBranchDevice.getDeviceId());
        if (fiscalization != null) {
            if (fiscalization.getOfdApplication() != null && !ApplicationType.DEREGISTER.equals(fiscalization.getOfdApplication().getApplicationType())) {
                throw validationService.badRequestExceptionThrow(FISCALIZATION_DELETE_VALIDATION_MESSAGE, customerBranchDevice.getDevice().getSerialNumber()).get();
            }

            //Update Fiscalization
            fiscalization.setDeleted(true);
            fiscalization.setStatus(DeviceStatus.DELETED);
            fiscalizationRepository.save(fiscalization);
            cacheService.evictFiscalCache(fiscalization.getHashKey());
        }

        //Delete from a customer branch device
        customerBranchDevice.setDeleted(true);
        customerBranchDevice.setStatus(DeviceStatus.DELETED);
        customerBranchDeviceRepository.save(customerBranchDevice);
        warehouseHistoryService.create(customerBranchDevice.getDevice().getSerialNumber(), id, EntityTypeEnum.CUSTOMER_BRANCH_DEVICE, DeviceStatus.DELETED);

        logHistoryService.logHistory(customerBranchDevice.getDevice().getSerialNumber(), false, Action.DELETE, customerBranchDevice.getCustomerBranch().getName(), null);

        //Delete from customer device
        customerDeviceService.deleteByDeviceId(customerBranchDevice.getDeviceId());

        //Delete from branch device
        branchDeviceService.deleteByDeviceId(customerBranchDevice.getDeviceId());

        //Delete from a company device
        companyDeviceService.deleteByDeviceId(customerBranchDevice.getDeviceId());

        //Update device status
        deviceService.updateStatus(customerBranchDevice.getDevice(), DeviceStatus.NOT_REGISTERED);
    }

    @Override
    public Page<BaseDeviceFiscalModuleDTO> listByCustomerBranch(BaseFilter filter) {

        ResultList<BaseDeviceFiscalModuleDTO> resultList = customerBranchDeviceRepository.getListByCustomerBranch(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public CustomerBranchDeviceListDto suspend(Long customerBranchDeviceId) {
        CustomerBranchDevice customerBranchDevice = findById(customerBranchDeviceId);
        customerBranchDevice.setSuspendStatus(DeviceSuspendStatus.SUSPENDED);
        // suspend date for blocking device
        LocalDate suspendDate = LocalDate.now();
        if (PeriodType.YEARLY.equals(customerBranchDevice.getTariff().getPeriodType()))
            customerBranchDevice.setSuspendDate(suspendDate);
        else {
            suspendDate = customerBranchDevice.getDueDate();
            customerBranchDevice.setSuspendDate(suspendDate);
        }
        save(customerBranchDevice);

        DeviceSuspendHistory history = new DeviceSuspendHistory();
        history.setActive(true);
        history.setCustomerBranchDeviceId(customerBranchDeviceId);
        history.setCustomerBranchId(customerBranchDevice.getCustomerBranchId());
        history.setDeviceSerialNumber(customerBranchDevice.getDevice().getSerialNumber());
        history.setTariffId(customerBranchDevice.getTariffId());
        history.setContractId(customerBranchDevice.getContractId());
        history.setMonth(suspendDate.getMonth());
        history.setStartDate(suspendDate);
        history.setYear(suspendDate.getYear());

        deviceSuspendHistoryOldService.save(history);

        return customerBranchDeviceMapper.toLstDto(customerBranchDevice);
    }

    @Override
    @Transactional
    public CustomerBranchDeviceListDto activate(Long customerBranchDeviceId) {
        DeviceSuspendHistory history = deviceSuspendHistoryOldService.getLastByCustomerBranchDeviceId(customerBranchDeviceId);
        if (history == null)
            throw validationService.entityNotFoundExceptionThrow("device.not.suspended").get();

        if (history.getDueDate() != null)
            throw validationService.entityNotFoundExceptionThrow("device.not.suspended").get();

        LocalDate today = LocalDate.now();
        if (!today.isAfter(history.getStartDate())) {
            throw validationService.badRequestExceptionThrow("device.cannot.be.activated.before.stop.date").get();
        }
        history.setDueDate(today);
        history.setActive(false);
        deviceSuspendHistoryOldService.save(history);

        CustomerBranchDevice customerBranchDevice = findById(customerBranchDeviceId);
        customerBranchDevice.setSuspendStatus(DeviceSuspendStatus.ACTIVE);
        customerBranchDevice.setSuspendDate(null);
        customerBranchDevice.setStartDate(today);

        if (PeriodType.YEARLY == customerBranchDevice.getTariff().getPeriodType()) {
            long suspendDays = DateUtil.differenceBetweenLocalDate(history.getStartDate(), history.getDueDate());
            if (suspendDays > 0)
                customerBranchDevice.setDueDate(customerBranchDevice.getDueDate().plusDays(suspendDays));
        } else {
            customerBranchDevice.setDueDate(DateUtil.calculateDueDate(today));
        }

        save(customerBranchDevice);

        return customerBranchDeviceMapper.toLstDto(customerBranchDevice);
    }

    @Transactional(readOnly = true)
    public Long getListCountToSync() {
        return (!isSuperAdmin() && getCompanyId() != null) ? customerDeviceRepository.countByCompanyId(getCompanyId()) : customerDeviceRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<SyncCustomerDeviceListDTO> getListToSync(BaseFilter filter) {
        return new PageImpl<>
                (customerBranchDeviceRepository.getListToSync(filter), filter.getOrderedPageable(), 0L);
    }

    @Override
    public void transferDeviceToOtherBranch(CustomerBranchDeviceTransferDTO customerBranchDeviceTransferDTO) {
        Device device = deviceRepository.findById(customerBranchDeviceTransferDTO.getDeviceId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
        CustomerBranchDevice fromCustomerBranchDevice = customerBranchDeviceRepository.findFirstByCustomerBranchIdAndDeviceId(customerBranchDeviceTransferDTO.getFromCustomerBranchId(), customerBranchDeviceTransferDTO.getDeviceId());
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndDeviceId(fromCustomerBranchDevice.getCustomerBranch().getCustomerId(), fromCustomerBranchDevice.getCustomerBranchId(), fromCustomerBranchDevice.getDeviceId());
        if (fiscalization != null) {
            if (fiscalization.getOfdApplication() != null && ApplicationType.DEREGISTER.equals(fiscalization.getOfdApplication().getApplicationType()) && OfdStatus.ACCEPT.equals(fiscalization.getOfdApplication().getOfdStatus())) {
                throw validationService.badRequestExceptionThrow(FISCALIZATION_DELETE_VALIDATION_MESSAGE, fromCustomerBranchDevice.getDevice().getSerialNumber()).get();
            } else if (fiscalization.getOfdApplication() != null && ApplicationType.REGISTER.equals(fiscalization.getOfdApplication().getApplicationType()) && OfdStatus.REJECT.equals(fiscalization.getOfdApplication().getOfdStatus())) {
                throw validationService.badRequestExceptionThrow(FISCALIZATION_DELETE_VALIDATION_MESSAGE, fromCustomerBranchDevice.getDevice().getSerialNumber()).get();
            }
        }
        CustomerBranchDeviceDTO customerBranchDeviceDTO = new CustomerBranchDeviceDTO();
        customerBranchDeviceDTO.setCustomerBranchId(customerBranchDeviceTransferDTO.getToCustomerBranchId());
        DeviceModelSharedDTO deviceModelDTO = new DeviceModelSharedDTO();
        deviceModelDTO.setSerialNumber(device.getSerialNumber());
        deviceModelDTO.setModelId(device.getModelId());
        customerBranchDeviceDTO.setDevices(Collections.singletonList(deviceModelDTO));
        add(customerBranchDeviceDTO);
    }

    @Override
    public CustomerBranchDeviceTariffDto getCustomerDeviceById(Long customerId, Long deviceId) {
        return customerBranchDeviceRepository.findOneByCustomerIdAndDeviceId(customerId, deviceId)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
    }

    @Override
    public String getTariffByCustomerAndDevice(Long customerId, String serialNumber) {
        Device device = deviceRepository.findBySerialNumber(serialNumber);
        return customerBranchDeviceRepository.findOneByCustomerIdAndDeviceId(customerId, device.getId())
                .map(CustomerBranchDeviceTariffDto::getTariffName)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
    }

    @Override
    @Transactional
    public CustomerBranchDeviceListDto setFreePeriodDate(CustomerBranchDeviceFreePeriodDto dto) {
        return customerBranchDeviceRepository.findById(dto.getCustomerBranchDeviceId())
                .map(f -> {
                    setFreePeriod(f, dto.getFreePeriodDate());
                    return f;
                })
                .map(customerBranchDeviceMapper::toLstDto)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
    }

    @Override
    @Transactional
    public void setFreePeriodDateByCustomerId(CustomerBranchDeviceFreePeriodByCustomerIdDto dto) {
        customerBranchDeviceRepository.findByCustomerId(dto.getCustomerId())
                .forEach(f -> setFreePeriod(f, dto.getFreePeriodDate()));
    }

    private void setFreePeriod(CustomerBranchDevice f, LocalDate date) {
        f.setFreePeriodDate(date);
        customerBranchDeviceRepository.save(f);

        FreePeriodHistory history = new FreePeriodHistory();
        history.setFreePeriodDate(date);
        history.setCustomerId(f.getCustomerBranch().getCustomer().getId());
        history.setCustomerBranchDeviceId(f.getId());
        freePeriodHistoryService.save(history);
    }


    @Override
    @Transactional
    public void changePaymentStatus(CustomerBranchDeviceRequestDTO dto) {
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findFirstByCustomerBranch_CustomerIdAndDeviceId(dto.getCustomerId(), dto.getDeviceId());
        if (customerBranchDevice != null) {
            CustomerBranchDevicePaymentTypeDTO paymentTypeDTO = new CustomerBranchDevicePaymentTypeDTO();
            paymentTypeDTO.setPaymentTypes(dto.getPaymentTypes());
            paymentTypeDTO.setCustomerBranchDeviceId(customerBranchDevice.getId());
            paymentTypeDTO.setBankTariffId(dto.getBankTariffId());
            customerBranchDevicePaymentTypeService.update(paymentTypeDTO);
        }
    }

    @Override
    public void updatePaymentStatus(CustomerBranchDeviceExcelDTO data) {
        if (CollectionUtils.isEmpty(data.getData()) && StringUtils.isEmpty(data.getPaymentStatus()))
            return;
        data.getData()
                .forEach(dto -> {
                    if (data.getTypeTin().equals("OLD")) {
                        customerBranchDeviceRepository
                                .findByOldTinAndDeviceSerialNumber(dto.getTin(), dto.getSerialNumber())
                                .ifPresent(customerBranchDevice -> {
                                    CustomerBranchDevicePaymentTypeDTO paymentTypeDTO = new CustomerBranchDevicePaymentTypeDTO();
                                    paymentTypeDTO.setCustomerBranchDeviceId(customerBranchDevice.getId());
                                    paymentTypeDTO.setPaymentTypes(Collections.singleton(PaymentType.valueOf(data.getPaymentStatus())));
                                    customerBranchDevicePaymentTypeService.update(paymentTypeDTO);
                                });
                    } else {
                        customerBranchDeviceRepository
                                .findByTinAndDeviceSerialNumber(dto.getTin(), dto.getSerialNumber())
                                .ifPresent(customerBranchDevice -> {
                                    CustomerBranchDevicePaymentTypeDTO paymentTypeDTO = new CustomerBranchDevicePaymentTypeDTO();
                                    paymentTypeDTO.setCustomerBranchDeviceId(customerBranchDevice.getId());
                                    paymentTypeDTO.setPaymentTypes(Collections.singleton(PaymentType.valueOf(data.getPaymentStatus())));
                                    customerBranchDevicePaymentTypeService.update(paymentTypeDTO);
                                });
                    }
                });
    }

    @Override
    @Transactional
    public void importExcel(MultipartFile file, String paymentStatus, String typeTin) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();
            List<CommonTinSerialNumberDTO> listData = new ArrayList<>();
            String[] data = new String[2];
            while (rowIterator.hasNext()) {
                CommonTinSerialNumberDTO dto = new CommonTinSerialNumberDTO();
                Row row = rowIterator.next();
                if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.NUMERIC) {
                    data[0] = NumberToTextConverter.toText(row.getCell(0).getNumericCellValue());
                } else if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING) {
                    data[0] = row.getCell(0).getStringCellValue();
                } else {
                    continue;
                }
                if (row.getCell(1) != null) {
                    data[1] = String.valueOf(row.getCell(1)).toUpperCase();
                } else {
                    continue;
                }
                try {
                    dto.setTin(data[0].trim());
                    dto.setSerialNumber(data[1].trim());
                    listData.add(dto);
                } catch (
                        Exception e) {
                    log.error("ERROR: Customer Branch Device payment status import excel: {}", e.getMessage());
                }
            }
            rabbitMqCustomerBranchDeviceProducer.updatePaymentStatus(new CustomerBranchDeviceExcelDTO(listData, paymentStatus, typeTin));
        } catch (
                Exception exception) {
            log.error("ERROR: Customer Branch Device payment status import excel: {}", exception.getMessage());
        }
    }

    @Override
    public Long getTariffId(String tin, String serialNumber) {
        return customerBranchDeviceRepository
                .findOneByTinAndDeviceSerialNumber(tin, serialNumber)
                .map(CustomerBranchDevice::getTariffId).orElse(null);
    }

    @Override
    public void changeDeviceType(Long customerId, Long deviceId, DeviceType deviceType) {
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findFirstByCustomerBranch_CustomerIdAndDeviceId(customerId, deviceId);
        if (customerBranchDevice != null) {
            customerBranchDevice.setDeviceType(deviceType);
            save(customerBranchDevice);
            customerBranchRepository
                    .findById(customerBranchDevice.getCustomerBranchId())
                    .ifPresent(customerBranch -> {
                        customerBranch.setDeviceType(deviceType);
                        customerBranchRepository.save(customerBranch);
                    });
        }
    }

    @Override
    public Page<CustomerBranchDeviceListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<CustomerBranchDevice> resultList = customerBranchDeviceRepository.getResultList(filter);
        List<CustomerBranchDeviceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(customerBranchDevice -> {
                    CustomerBranchDeviceListAdminDTO customerBranchDeviceListDTO = new CustomerBranchDeviceListAdminDTO();
                    customerBranchDeviceListDTO.setId(customerBranchDevice.getId());
                    customerBranchDeviceListDTO.setDevice(customerBranchDevice.getDevice().toSerialNumberDTO());
                    customerBranchDeviceListDTO.setStatus(customerBranchDevice.getStatus());
                    customerBranchDeviceListDTO.setCompany(customerBranchDevice.getCustomerBranch().getCustomer().getCompany().toShortDTO());
                    customerBranchDeviceListDTO.setBranch(customerBranchDevice.getCustomerBranch().getCustomer().getBranch().toCommonDTO());
                    customerBranchDeviceListDTO.setCustomer(customerBranchDevice.getCustomerBranch().getCustomer().toCommonDTO());
                    customerBranchDeviceListDTO.setCustomerBranch(customerBranchDevice.getCustomerBranch().toCommonDTO());
                    customerBranchDeviceListDTO.setCreatedDate(customerBranchDevice.getCreatedDate());
                    customerBranchDeviceListDTO.setCreator(customerBranchDevice.getCreator().toCommonDTO());
                    return customerBranchDeviceListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public DeviceDetailAdminDTO getForAdmin(Long id) {
        return customerBranchDeviceRepository.findById(id).map(customerBranchDevice -> deviceService.getForAdmin(customerBranchDevice.getDeviceId())).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return customerBranchDeviceRepository.getStatistics(filter);
    }
}
