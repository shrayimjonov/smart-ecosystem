package uz.uzkassa.smartpos.integration.apay;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class QuickRegisterMerchantDto implements Serializable {

    @NotNull
    @NotEmpty
    String referenceId;

    @NotNull
    @NotEmpty
    String name;

    String phone;

    @NotNull
    AddressFullDTO address;

    @NotNull
    ActivityTypeDTO activityType;
}
