package uz.uzkassa.horeca.dto.stocktransfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.StockTransferStatus;
import uz.uzkassa.horeca.enums.StockTransferType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5463453L;

    Long id;

    StockTransferType transferType;

    String transferNumber;

    LocalDateTime transferDate;

    StockTransferStatus status;

    String description;

    CommonDTO fromBranch;

    CommonDTO fromWarehouse;

    CommonDTO toBranch;

    CommonDTO toWarehouse;

    BigDecimal totalPrice;

    BigDecimal totalQty;

    String rejectReason;

    List<StockTransferItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockTransferItemDetailDTO item) {
        items.add(item);
    }
}
