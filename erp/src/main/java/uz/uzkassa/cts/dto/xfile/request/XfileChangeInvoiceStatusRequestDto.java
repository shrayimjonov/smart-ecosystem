package uz.uzkassa.cts.dto.xfile.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.uzkassa.cts.enums.XfileStatus;

import java.io.Serializable;

@Data
public class XfileChangeInvoiceStatusRequestDto implements Serializable {
    @NotNull
    @NotEmpty
    private String facturaId;
    @NotNull
    private XfileStatus status;
}
