package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceValidationDTO {
    private String sellerTin;
    private String sellerName;
    private List<IncomeOfProductDetailValidationDTO> products;
}
