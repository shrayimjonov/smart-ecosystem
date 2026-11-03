package uz.uzkassa.cts.dto.invoice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class InvoiceItemResponseDto implements Serializable {

    private Integer quantity;
    private String modelName;
    private BigDecimal price;
    private BigDecimal deliveryCost;
    private BigDecimal vatPercent;
    private BigDecimal vat;
    private BigDecimal deliveryCostWithVAT;
    private Long invoiceId;
    private List<InvoiceItemDetailResponseDto> invoiceItemDetails = new ArrayList<>();

}
