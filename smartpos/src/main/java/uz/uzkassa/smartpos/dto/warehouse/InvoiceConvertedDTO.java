package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class InvoiceConvertedDTO {
    private List<ReceiptDetailDTO> products;
    private BigDecimal totalIncomeSumma;
}
