package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.CustomerRequestType;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.service.CustomerBranchDeviceService;
import uz.uzkassa.cts.service.CustomerRequestService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.util.List;

@Getter
@Setter
@Component
public class ChangeDeviceTypeProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private FiscalizationRepository fiscalizationRepository;
    @Autowired
    @JsonIgnore
    private CustomerBranchDeviceService customerBranchDeviceService;
    @Autowired
    @JsonIgnore
    private CustomerRequestService customerRequestService;


    @NotNull
    private Long deviceId;
    @NotNull
    private Long customerId;
    @NotBlank
    private String deviceSerialNumber;
    @NotBlank
    private String deviceType;

    @Override
    @Transactional
    public void accept() {
        if (!DeviceType.FISCAL.getCode().equals(deviceType)) {
            /*FISCAL TO TERMINAL*/
            Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(this.customerId, this.deviceId);
            if (fiscalization.getOfdApplication() != null
                    && !ApplicationType.DEREGISTER.equals(fiscalization.getOfdApplication().getApplicationType())
                    && OfdStatus.ACCEPT.equals(fiscalization.getOfdApplication().getOfdStatus())) {
                throw new BadRequestException("Сначала вам нужно снять устройство.");
            }
            customerBranchDeviceService.changeDeviceType(customerId, deviceId, DeviceType.valueOf(deviceType));
            /*
             * CREATE
             * IN CUSTOMER REQUEST TO SHOW CALL CENTER
             * */
            CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
            customerRequestDTO.setCustomerId(customerId);
            customerRequestDTO.setRequestType(CustomerRequestType.FISCAL_TERMINAL);
            customerRequestService.create(customerRequestDTO);
        } else {
            /*TERMINAL TO FISCAL*/

            /*
             * CREATE
             * IN CUSTOMER REQUEST TO SHOW CALL CENTER
             * */
            CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
            customerRequestDTO.setCustomerId(customerId);
            customerRequestDTO.setRequestType(CustomerRequestType.TERMINAL_FISCAL);
            customerRequestService.create(customerRequestDTO);
            customerBranchDeviceService.changeDeviceType(customerId, deviceId, DeviceType.valueOf(deviceType));
        }
    }

    @Override
    public List<String> generateTemplate() {
        throw new UnsupportedOperationException("Шаблон не предусмотрен");
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return true;
    }
}
