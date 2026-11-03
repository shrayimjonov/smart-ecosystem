package uz.uzkassa.smartpossupply.dto.stocktransfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferDTO implements Serializable {

    Long id;

    String transferNumber;

    @NotNull
    Long fromWarehouseId;

    @NotNull
    Long toWarehouseId;

    @NotNull
    Long fromBranchId;

    @NotNull
    Long toBranchId;

    String description;

    LocalDateTime transferDate;

    Long expeditorId;

    String stockTransferType;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    BigDecimal totalQty;

    BigDecimal totalPrice;

    int orderCount;

    @NotEmpty
    List<StockTransferProductDTO> products = new ArrayList<>();

    Long tripId;

    @JsonIgnore
    public void addProduct(StockTransferProductDTO stockTransferProductDTO) {
        products.add(stockTransferProductDTO);
    }
}
