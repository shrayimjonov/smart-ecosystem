package uz.uzkassa.cts.dto.onetimeorder.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OneTimeRequestDto implements Serializable {

    @NotNull
    private Long id;
    @NotNull
    private Long contractId;
    @NotNull
    private Long customerId;
    @NotNull
    private Long serviceId;
    @NotNull
    private boolean calculateWithVat = false;
    private Integer quantity;
    @JsonIgnore
    private String serialNumberKKM;
    @JsonIgnore
    private String serialNumberFM;
}
