package uz.uzkassa.cts.dto.sale.accessory;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SaleAccessoryListDTO implements Serializable {
    private Long saleContractId;
    private List<SaleAccessoryDTO> accessories;
}
