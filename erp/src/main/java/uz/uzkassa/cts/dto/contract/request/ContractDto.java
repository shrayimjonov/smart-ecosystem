package uz.uzkassa.cts.dto.contract.request;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class ContractDto implements Serializable {

    private Long id;

    @NotNull
    private Long customerId;

}
