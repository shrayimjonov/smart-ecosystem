package uz.uzkassa.smartpossupply.dto.stocktransfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillLinkDTO;
import uz.uzkassa.smartpossupply.enums.StockTransferReason;
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
public class StockTransferDetailDTO implements Serializable {

    Long id;

    LocalDateTime transferDate;

    String transferNumber;

    StockTransferStatus status;

    CommonDTO fromBranch;

    CommonDTO toBranch;

    CommonDTO fromWarehouse;

    CommonDTO fromParentWarehouse;

    CommonDTO toWarehouse;

    CommonDTO toParentWarehouse;

    String description;

    BigDecimal totalQty;

    BigDecimal totalPrice;

    StockTransferReason reason;

    String otherReason;

    CommonDTO stock;

    LocalDateTime lastModifiedDate;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    Integer totalQtyInPackage;

    StockTransferType stockTransferType;

    CommonDTO expeditor;

    List<WaybillLinkDTO> waybills;

    List<CommonDTO> planRoutes;

    List<CommonDTO> agents;

    List<StockTransferProductDetailDTO> products = new ArrayList<>();

    CommonDTO trip;

    @JsonIgnore
    public void addProduct(StockTransferProductDetailDTO stockTransferProductDTO) {
        products.add(stockTransferProductDTO);
    }
}
