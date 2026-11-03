package uz.uzkassa.cts.dto.orderspecification.request;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class OrderSpecificationChangeStatusDto implements Serializable {
    @NotNull
    private ContractStatus contractStatus;

    @NotNull
    private Long customerId;
}
