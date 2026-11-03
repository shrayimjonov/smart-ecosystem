package uz.uzkassa.cts.dto.serviceorderinvoice.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ServiceInvoiceRequestDto implements Serializable {
    @NotNull
    private Long specificationId;
}
