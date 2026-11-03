package uz.uzkassa.smartpossupply.dto.stockout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockOutDTO extends GrandTotalDTO {

    Long id;

    String stockNumber;

    @NotNull
    Long warehouseId;

    @NotNull
    Long branchId;

    Long vehicleId;

    Long customerId;

    Long orderId;

    Long stockInId;

    Long priceListId;

    String description;

    Long contractId;

    LocalDateTime transferDate;

    Long deliveryBranchId;

    AppType appType;

    PaymentType paymentType;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    @NotEmpty
    List<StockOutProductDTO> products = new ArrayList<>();

    boolean allowWithLimit;

    @JsonIgnore
    public void addProduct(StockOutProductDTO stockProductDTO) {
        products.add(stockProductDTO);
    }
}
