package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.StockInStatus;
import uz.uzkassa.silen.enumeration.TransferType;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInFilter extends Filter {

    Long storeId;

    StockInStatus status;

    String userId;

    String shipmentId;

    TransferType transferType;

}
