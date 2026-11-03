package uz.uzkassa.smartpossupply.dto.stocktransfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorStockTransferDetailDTO implements Serializable {

    Long id;

    StockTransferStatus status;

    LocalDateTime transferDate;

    String truckRegNo;

    String truckModel;

    CommonDTO parentTruck;

    String trailerRegNo;

    String trailerModel;

    CommonDTO toWarehouse;

    CommonDTO trip;

    String transferNumber;

    List<ExpeditorStockTransferProductDetailDTO> products = new ArrayList<>();

    @JsonIgnore
    public void addProduct(ExpeditorStockTransferProductDetailDTO expeditorStockTransferProductDetailDTO) {
        products.add(expeditorStockTransferProductDetailDTO);
    }
}
