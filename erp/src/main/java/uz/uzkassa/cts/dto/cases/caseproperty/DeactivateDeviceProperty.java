package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.CustomerDevice;
import uz.uzkassa.cts.repository.CustomerDeviceRepository;
import uz.uzkassa.cts.service.CustomerDeviceService;
import uz.uzkassa.cts.service.ValidationService;

import java.util.List;

@Getter
@Setter
@Component
public class DeactivateDeviceProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CustomerDeviceRepository customerDeviceRepository;
    @Autowired
    @JsonIgnore
    private CustomerDeviceService customerDeviceService;
    @Autowired
    @JsonIgnore
    private ValidationService validationService;

    @NotNull
    private Long deviceId;
    @NotNull
    private Long reasonId;
    private String reason;
    @NotNull
    private Long customerId;

    @Override
    public void accept() {
        customerDeviceRepository.findFirstByDeviceIdOrderByIdDesc(deviceId).ifPresentOrElse(customerDevice -> {
            customerDeviceService.delete(customerDevice.getId());
        }, () -> validationService.entityNotFoundExceptionThrow(CustomerDevice.class.getSimpleName()));
    }

    @Override
    public List<String> generateTemplate() {
        return List.of();
    }

    @Override
    public boolean is2Step() {
        return false;
    }
}
