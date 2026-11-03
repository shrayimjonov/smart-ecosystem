package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockCommonDTO extends CommonDTO {

    TransferType transferType;

    StockStatus status;

    public StockCommonDTO(Long id, String name, TransferType transferType, StockStatus status) {
        super(id, name);
        this.transferType = transferType;
        this.status = status;
    }
}
