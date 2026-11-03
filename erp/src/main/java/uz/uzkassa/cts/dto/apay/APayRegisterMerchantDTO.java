package uz.uzkassa.cts.dto.apay;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.domain.CustomerBranch;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class APayRegisterMerchantDTO implements Serializable {
    private Long ctoId;

    @NotNull
    @NotEmpty
    String referenceId;

    @NotNull
    @NotEmpty
    String name;

    String phone;

    @NotNull
    ApayAddressDTO address;

    @NotNull
    ApayActivityTypeDTO activityType;

    public APayRegisterMerchantDTO(CustomerBranch customerBranch) {
        this.ctoId = customerBranch.getId();
        this.referenceId = customerBranch.getId().toString();
        this.name = customerBranch.getName();
        this.phone = customerBranch.getPhone();
        if (customerBranch.getAddress() != null) {
            this.address = new ApayAddressDTO(customerBranch.getAddress());
        }
        if (customerBranch.getActivityType() != null) {
            this.activityType = new ApayActivityTypeDTO(customerBranch.getActivityType());
        }
    }
}
