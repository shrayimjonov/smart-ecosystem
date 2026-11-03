package uz.uzkassa.cts.dto.apay;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApayRegisterCustomerBranchesDTO implements Serializable {
    @NotNull
    private Long customerId;
    private List<Long> customerBranchIds;
}
