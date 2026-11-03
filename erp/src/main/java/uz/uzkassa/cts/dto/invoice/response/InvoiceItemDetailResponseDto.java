package uz.uzkassa.cts.dto.invoice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class InvoiceItemDetailResponseDto implements Serializable {

    private Long invoiceItemId;
    private String serialNumber;
    private Long deviceModelId;
    private String modelName;
    private Long branchDevicesId;
}
