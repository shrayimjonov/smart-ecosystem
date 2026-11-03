package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.ActivityType;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.cases.request.CustomerBranchUpdateDto;
import uz.uzkassa.cts.dto.customer.CustomerBranchUpdateDTO;
import uz.uzkassa.cts.dto.ofd.CustomerBranchSendDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqCustomerBranchSendProducer;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.service.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class BranchDataChangeProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CustomerBranchOldService customerBranchOldService;
    @Autowired
    @JsonIgnore
    private ThymeleafService thymeleafService;
    @Autowired
    @JsonIgnore
    private CustomerOldService customerOldService;
    @Autowired
    @JsonIgnore
    private ActivityTypeService activityTypeService;
    @Autowired
    @JsonIgnore
    private DeviceService deviceService;
    @Autowired
    @JsonIgnore
    private RabbitMqCustomerBranchSendProducer rabbitMqCustomerBranchSendProducer;
    @Autowired
    @JsonIgnore
    private CustomerEmployeeRepository customerEmployeeRepository;


    @NotNull
    private Long branchId;
    @NotNull
    private Long customerId;
    @NotNull
    private Long deviceId;
    @NotBlank
    private String deviceSerialNumber;
    private Long neighborhoodId;
    private String name;
    private Long activityTypeId;
    private Long parentActivityTypeId;
    private String activityTypeName;
    private String parentActivityTypeName;

    @Override
    public void accept() {
        customerBranchOldService.update(this.map());
        // PODTVERJDENIYEE TT
        // api/ofd/customer/branch
        rabbitMqCustomerBranchSendProducer.sendServiceOrderInvoice(acceptTTToDto());
    }

    private CustomerBranchSendDTO acceptTTToDto() {
        CustomerBranch branch = customerBranchOldService.findById(this.branchId);
        CustomerBranchSendDTO dto = new CustomerBranchSendDTO();
        dto.setCustomerId(this.customerId);
        dto.setCustomerBranchId(this.branchId);
        dto.setOfdId(branch.getOfdId());
        return dto;
    }

    private CustomerBranchUpdateDTO map() {
        CustomerBranchUpdateDTO customerBranchUpdateDTO = new CustomerBranchUpdateDTO();
        customerBranchUpdateDTO.setName(name);
        customerBranchUpdateDTO.setId(branchId);
        customerBranchUpdateDTO.setActivityTypeId(activityTypeId);
        BaseAddressDTO address = new BaseAddressDTO();
        address.setNeighborhoodId(neighborhoodId);
        customerBranchUpdateDTO.setAddress(address);
        return customerBranchUpdateDTO;
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(thymeleafService.branchDataChangeStatement(mapTemplate()));
    }

    private CustomerBranchUpdateDto mapTemplate() {
        Customer customer = customerOldService.findById(customerId);
        CustomerBranch customerBranch = customerBranchOldService.findById(branchId);
        CustomerBranchUpdateDto dto = new CustomerBranchUpdateDto();
        dto.setTin(customer.getTin());
        dto.setDate(LocalDate.now());
        dto.setCompanyName(customer.getCompanyName());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).ifPresent(customerEmployee -> dto.setDirector(customerEmployee.getName()));
        dto.setActivityType(customer.getActivityType().getNameRu());
        dto.setBranchName(customerBranch.getName());
        dto.setNewBranchName(this.name);
        if (this.deviceId != null) {
            Device device = deviceService.findById(this.deviceId);
            dto.setDeviceNumber(device.getSerialNumber());
        }
        dto.setActivityType(customerBranch.getActivityType().getNameRu());
        if (this.activityTypeId != null) {
            ActivityType activityType = activityTypeService.findById(this.activityTypeId);
            dto.setNewActivityType(activityType.getNameRu());
        }
        dto.setBusinessType(customer.getBusinessType().getName());
        return dto;
    }

    @Override
    public boolean is2Step() {
        return false;
    }
}
