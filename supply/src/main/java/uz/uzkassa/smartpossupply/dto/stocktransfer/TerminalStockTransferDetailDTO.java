package uz.uzkassa.smartpossupply.dto.stocktransfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.enums.StockTransferType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalStockTransferDetailDTO implements Serializable {

    Long id;

    LocalDateTime transferDate;

    String transferNumber;

    StockTransferStatus status;

    CommonDTO fromBranch;

    CommonDTO toBranch;

    CommonDTO fromWarehouse;

    CommonDTO toWarehouse;

    String description;

    BigDecimal totalQty;

    BigDecimal totalPrice;

    LocalDateTime lastModifiedDate;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    StockTransferType stockTransferType;

    BigDecimal totalBlockQty;

    List<TerminalStockTransferProductDetailDTO> products = new ArrayList<>();

    CommonDTO trip;

    @JsonIgnore
    public void addProduct(TerminalStockTransferProductDetailDTO stockTransferProductDTO) {
        products.add(stockTransferProductDTO);
    }
}
