package uz.uzkassa.smartpos.dto.integration.onec;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OneCIncomeProductDto {
    private Long id;
    private String name;
    private String barcode;
    private String vatBarcode;
}
