package uz.uzkassa.smartpossupply.dto.stockin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockCustomerDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockInProductDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInDTO extends GrandTotalDTO {

    Long id;

    @NotNull
    Long warehouseId;

    String stockNumber;

    @NotNull
    Long branchId;

    Long facturaId;

    Long partyId;

    Long aicId;

    Long acceptanceTransferActId;

    StockCustomerDTO supplier;

    Long companyCommittentId;

    String description;

    LocalDateTime transferDate;

    StockStatus status;

    boolean hasContract;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    @NotEmpty
    List<StockInProductDTO> products = new ArrayList<>();
    
    @JsonIgnore
    public void addProduct(StockInProductDTO stockInProductDTO) {
        products.add(stockInProductDTO);
    }
}
