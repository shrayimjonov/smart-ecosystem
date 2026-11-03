package uz.uzkassa.silen.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.StockInStatus;
import uz.uzkassa.silen.enumeration.TransferType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInDetailsDTO implements Serializable {
    String id;

    String number;

    SelectItem customer;

    SelectItem shipment;

    SelectItem store;

    LocalDateTime transferDate;

    int qty;

    List<StoreInItemDTO> items;

    TransferType transferType;

    StockInStatus status;
}
