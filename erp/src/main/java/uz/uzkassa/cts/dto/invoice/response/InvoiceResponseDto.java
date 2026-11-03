package uz.uzkassa.cts.dto.invoice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.cts.enums.InvoiceStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class InvoiceResponseDto implements Serializable {

    private Long id;
    private String number;
    private BigDecimal totalPrice;
    private String totalPriceInWord;
    private Long contractId;
    private Long companyId;
    private Long customerId;
    private Long orderSpecificationId;
    private Long orderPowerAttorneyId;
    private InvoiceStatus status;
    private List<InvoiceItemResponseDto> invoiceItems = new ArrayList<>();

    private String specFileUrl;
    private String specFileName;
    private String specContentType;

}
