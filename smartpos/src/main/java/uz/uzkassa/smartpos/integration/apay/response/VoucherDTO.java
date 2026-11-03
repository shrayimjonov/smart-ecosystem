package uz.uzkassa.smartpos.integration.apay.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;

import java.util.List;

@Getter
@Setter
public class VoucherDTO {
    private String phone;
    private List<ReceiptDetailDTO> products;
}
