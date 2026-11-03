package uz.uzkassa.smartpossupply.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferProductDTO implements Serializable {

    Long id;

    Long stockTransferProductId;

    Long productSerialId;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal totalPrice;

    Long orderId;

    BigDecimal scannedQty;

    BigDecimal reservationQty;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    List<CheckedCodeDTO> aggregationCodes;

    List<CheckedCodeDTO> markCodes;

    List<CheckedCodeDTO> aggregationBlockCodes;
}
