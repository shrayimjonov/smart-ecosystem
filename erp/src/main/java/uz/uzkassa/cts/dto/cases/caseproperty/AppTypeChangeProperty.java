package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.dto.cases.CaseSmartposAppTypeChange;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.integrations.SmartposClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.service.CustomerOldService;
import uz.uzkassa.cts.service.ServicePaymentHistoryCronService;
import uz.uzkassa.cts.utils.DateUtil;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Component
public class AppTypeChangeProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CustomerOldService customerOldService;
    @Autowired
    @JsonIgnore
    private CustomerBranchDeviceRepository customerBranchDeviceRepository;
    @Autowired
    @JsonIgnore
    private RabbitMqDeviceSwitcherProducer rabbitMqDeviceSwitcherProducer;
    @Autowired
    @JsonIgnore
    private ServicePaymentHistoryCronService servicePaymentHistoryCronService;
    @Autowired
    @JsonIgnore
    private SmartposClient smartposClient;


    @NotNull
    private Long customerId;
    @NotBlank
    private String appType;

    @Override
    public void accept() {
        Customer customer = customerOldService.findById(customerId);
        customer.setAppType(CustomerAppType.valueOf(appType));
        customerOldService.save(customer);
        if (CustomerAppType.valueOf(appType) == CustomerAppType.CTS) {
            List<CustomerBranchDevice> customerBranchDeviceList = customerBranchDeviceRepository.findByCustomerId(customerId);
            for (CustomerBranchDevice customerBranchDevice : customerBranchDeviceList) {
                customerBranchDevice.setStartDate(LocalDate.now());
                customerBranchDevice.setDueDate(DateUtil.calculateDueDate());
                customerBranchDevice.setStatus(DeviceStatus.BLOCKED);
            }
            customerBranchDeviceRepository.saveAll(customerBranchDeviceList);
            rabbitMqDeviceSwitcherProducer.paymentHistoryCreate(customer.getTin());
        } else {
            customerBranchDeviceRepository
                    .findByCustomerId(customerId)
                    .forEach(customerBranchDevice ->
                            servicePaymentHistoryCronService
                                    .recalculateHistory(customer.getTin(), customerBranchDevice.getDevice().getSerialNumber(), LocalDate.now())
                    );
        }

        smartposClient.changeAppType(new CaseSmartposAppTypeChange(
                customer.getId(),
                customer.getTin(),
                customer.getOldTin(),
                customer.getAppType().getCode())
        );
    }

    @Override
    public List<String> generateTemplate() {
        throw new UnsupportedOperationException("Шаблон не предусмотрен");
    }

    @JsonIgnore
    @Override
    public boolean is2Step() {
        return false;
    }
}
