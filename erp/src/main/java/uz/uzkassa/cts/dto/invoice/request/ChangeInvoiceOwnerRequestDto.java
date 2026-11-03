package uz.uzkassa.cts.dto.invoice.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChangeInvoiceOwnerRequestDto implements Serializable {
    @NotNull
    private Long invoiceId;
    @NotNull
    private Long customerId;
}
