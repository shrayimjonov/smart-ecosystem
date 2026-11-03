package uz.uzkassa.cts.dto.serviceorderinvoice.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RevertInvoiceRequestDto {
    @NotNull
    private List<Long> invoiceId;
}
