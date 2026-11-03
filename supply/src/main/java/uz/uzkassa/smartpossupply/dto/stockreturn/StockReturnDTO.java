package uz.uzkassa.smartpossupply.dto.stockreturn;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.stock.StockCustomerDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReturnDTO implements Serializable {

    Long id;

    String stockNumber;

    @NotNull
    Long warehouseId;

    @NotNull
    Long branchId;

    Long facturaId;

    StockCustomerDTO customer;

    String description;

    LocalDateTime returnDate;

    StockStatus status;

    Long contractId;

    boolean hasContract;

    CurrencyType currencyType;

    BigDecimal exchangeRate;

    @NotEmpty
    List<StockReturnProductDTO> products = new ArrayList<>();
}
