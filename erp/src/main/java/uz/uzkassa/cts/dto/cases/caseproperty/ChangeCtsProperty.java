package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.domain.report.ContractReport;
import uz.uzkassa.cts.dto.cases.request.CtsChangeDto;
import uz.uzkassa.cts.dto.device.DeviceTransferDTO;
import uz.uzkassa.cts.dto.fm.*;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationCreateDTO;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.ContractReportRepository;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Component
public class ChangeCtsProperty implements CaseAction {
    @Autowired
    @JsonIgnore
    private CaseService caseService;
    @Autowired
    @JsonIgnore
    private CustomerService customerService;
    @Autowired
    @JsonIgnore
    private CustomerBranchService customerBranchService;
    @Autowired
    @JsonIgnore
    private DeviceService deviceService;
    @Autowired
    @JsonIgnore
    private FiscalModuleService fiscalModuleService;
    @Autowired
    @JsonIgnore
    private CompanyFiscalModuleService companyFiscalModuleService;
    @Autowired
    @JsonIgnore
    private CustomerFiscalModuleService customerFiscalModuleService;
    @Autowired
    @JsonIgnore
    private CustomerDeviceService customerDeviceService;
    @Autowired
    @JsonIgnore
    private CustomerBranchDeviceService customerBranchDeviceService;
    @Autowired
    @JsonIgnore
    private FiscalizationService fiscalizationService;
    @Autowired
    @JsonIgnore
    private OfdApplicationService ofdApplicationService;
    @Autowired
    @JsonIgnore
    private FiscalModuleReportService fiscalModuleReportService;
    @Autowired
    @JsonIgnore
    private ServicePaymentHistoryCronService servicePaymentHistoryCronService;
    @Autowired
    @JsonIgnore
    private CustomerEmployeeRepository customerEmployeeRepository;
    @Autowired
    @JsonIgnore
    private ContractReportRepository contractReportRepository;
    @Autowired
    @JsonIgnore
    private FiscalizationRepository fiscalizationRepository;
    @Autowired
    @JsonIgnore
    private RabbitMqSmartPosProducer rabbitMqSmartPosProducer;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private ValidationService validationService;


    @NotNull
    private Long customerId;
    @NotNull
    private Long customerBranchId;
    @NotNull
    private Long deviceId;
    @NotNull
    private String fiscalModuleSerialNumber;
    @NotNull
    @Enumerated(EnumType.STRING)
    private ChangeCtsType changeCtsType;
    private String reason;

    @Override
    @Transactional
    public void accept() {
        Device device = deviceService.findById(deviceId);
        if (ChangeCtsType.CAME == changeCtsType) {
            // Добавление ФМ в реестр ФМ
            Long fiscalModuleId = fiscalModuleService.create(new FiscalModuleDTO(fiscalModuleSerialNumber));

            // Добавление ФМ в карточку клиента
            DeviceTransferDTO deviceTransferDTO = new DeviceTransferDTO();
            deviceTransferDTO.setToBranchId(SecurityUtils.getBranchId());
            deviceTransferDTO.setDevices(List.of(fiscalModuleId));
            companyFiscalModuleService.transfer(deviceTransferDTO);

            CustomerFiscalModuleDTO customerFiscalModuleDTO = new CustomerFiscalModuleDTO();
            customerFiscalModuleDTO.setBranchId(SecurityUtils.getBranchId());
            customerFiscalModuleDTO.setCustomerId(customerId);
            customerFiscalModuleDTO.setCustomerBranchId(customerBranchId);
            customerFiscalModuleDTO.setSerialNumber(fiscalModuleSerialNumber);
            customerFiscalModuleService.add(customerFiscalModuleDTO);

            FiscalizationDTO fiscalizationDTO = new FiscalizationDTO();
            fiscalizationDTO.setCustomerId(customerId);
            fiscalizationDTO.setCustomerBranchId(customerBranchId);
            fiscalizationDTO.setDeviceModelId(device.getModelId());
            fiscalizationDTO.setDeviceSerial(device.getSerialNumber());
            fiscalizationDTO.setFiscalModuleSerial(fiscalModuleSerialNumber);
            Long fiscalizationId = fiscalizationService.fiscalization(fiscalizationDTO);

            // Создание заявления
            OfdApplicationCreateDTO ofdApplicationCreateDTO = new OfdApplicationCreateDTO();
            ofdApplicationCreateDTO.setCustomerId(customerId);
            ofdApplicationCreateDTO.setDeviceId(deviceId);
            ofdApplicationCreateDTO.setFiscalModuleId(fiscalModuleId);
            ofdApplicationService.create(ofdApplicationCreateDTO);

            // Обновление статуса фискализации на "Фискал Актив"
            fiscalizationService.updateStatus(fiscalizationId, DeviceStatus.FISCAL_ACTIVE);

            // Очистка КЕШ Фискализации
            FiscalClearCacheDTO fiscalClearCacheDTO = new FiscalClearCacheDTO();
            fiscalClearCacheDTO.setDeviceSerial(device.getSerialNumber());
            fiscalClearCacheDTO.setFiscalModuleSerial(fiscalModuleSerialNumber);
            fiscalizationService.clearFiscalCache(fiscalClearCacheDTO);

            // Слияние со SmartPos
            FiscalizationSyncDTO fiscalizationSyncDTO = new FiscalizationSyncDTO();
            fiscalizationSyncDTO.setFiscalizationId(fiscalizationId);
            fiscalizationService.syncWithSmartpos(fiscalizationSyncDTO);
        } else if (ChangeCtsType.LEFT == changeCtsType) {
            Customer customer = customerService.findById(customerId);

            // Проверка ККМ
            Optional<ContractReport> optionalContractReport = contractReportRepository.findBySerialNumberAndSaleStatusIn(device.getSerialNumber(), List.of(ContractReportInvoiceSaleStatus.SOLD));
            if (optionalContractReport.isEmpty()) {
                throw validationService.badRequestExceptionThrow(DEVICE_CONTRACT_STATUS).get();
            }

            // Добавление ФМ в отчеты ФМ, с отметкой что ФМ ушел
            FiscalModuleReportRequestDTO fiscalModuleReportRequestDTO = new FiscalModuleReportRequestDTO();
            fiscalModuleReportRequestDTO.setCustomerId(customerId);
            fiscalModuleReportRequestDTO.setDeviceSerialNumber(device.getSerialNumber());
            fiscalModuleReportRequestDTO.setFmSerialNumber(fiscalModuleSerialNumber);
            fiscalModuleReportRequestDTO.setStatus(ArrivalStatus.LEFT);
            fiscalModuleReportService.create(fiscalModuleReportRequestDTO);

            // Перерасчет тарифа (Снятие тарифа) и Блокировка ККМ (Launcher)
            if (CustomerAppType.CTS == customer.getAppType()) {
                servicePaymentHistoryCronService.recalculateHistory(customer.getTin(), device.getSerialNumber(), LocalDate.now());
            }

            // Смена статуса OFD (ofdApplicationType)
            Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndDeviceId(customerId, customerBranchId, deviceId);
            ofdApplicationService.changeApplicationType(fiscalization.getOfdApplicationId(), ApplicationType.DEREGISTER_DEVICE);

            // Снятие ККМ с Smartpos
            rabbitMqSmartPosProducer.removeDevice(new SmartPosRemoveDevicePayload(device.getSerialNumber()));
        }
    }

    @Override
    public List<String> generateTemplate() {
        if (ChangeCtsType.CAME == changeCtsType) {
            return Collections.singletonList(thymeleafService.ctsChangeStatement(map()));
        }
        return null;
    }

    private CtsChangeDto map() {
        Customer customer = customerService.findById(customerId);
        CustomerBranch customerBranch = customerBranchService.findById(customerBranchId);
        Device device = deviceService.findById(deviceId);
        CtsChangeDto dto = new CtsChangeDto();
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customerId, Role.DIRECTOR).ifPresent(customerEmployee -> dto.setDirector(customerEmployee.getName()));
        dto.setCompanyName(customer.getCompanyName());
        dto.setTin(customer.getTin());
        dto.setCustomerBranchName(customerBranch.getName());
        dto.setCustomerBranchAddress(customerBranch.getAddress().getFullAddressAddress());
        dto.setFiscalModuleSerialNumber(fiscalModuleSerialNumber);
        dto.setDeviceSerialNumber(device.getSerialNumber());
        dto.setDate(LocalDate.now());
        return dto;
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
