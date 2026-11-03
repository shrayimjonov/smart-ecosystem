package uz.uzkassa.cts.dto.sale.contract;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SaleContractAccessoryListWithVatDTO implements Serializable {

    private String number;
    private LocalDateTime createdDate;
    private String companyName;
    private String businessType;
    private Integer totalQty;
    private BigDecimal totalWithoutVatPrice;
    private BigDecimal totalVatPrice;
    private BigDecimal totalPrice;
    private String ownerName;
    List<SaleContractAccessoryListCalDTO> listAccessory;
}
