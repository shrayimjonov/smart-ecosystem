package uz.uzkassa.cts.dto.orderspecification.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrderSpecificationDetailsResponseDto implements Serializable {

    private Long id;
    private String modelName;
    private Long count;
    private BigDecimal priceWithoutVATFor1pc;
    private BigDecimal amountWithoutVAT;
    private BigDecimal valueAddedTax;
    private BigDecimal totalAmountWithVAT;
    private String totalAmountInWords;
}
