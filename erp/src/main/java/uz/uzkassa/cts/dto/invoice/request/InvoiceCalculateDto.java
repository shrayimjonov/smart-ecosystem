package uz.uzkassa.cts.dto.invoice.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class InvoiceCalculateDto implements Serializable {
    private BigDecimal customerStatement;
    private Integer canBuyPc;
    private Integer totalCanBuyBySpecification;
    private BigDecimal price1pc;
}
