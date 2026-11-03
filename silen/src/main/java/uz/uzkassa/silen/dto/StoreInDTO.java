package uz.uzkassa.silen.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.StockInStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInDTO {

    String id;

    String number;

    Long customerId;

    String tin;

    Long storeId;

    String shipmentId;

    Long invoiceId;

    StockInStatus status;

    String organizationId;

    LocalDateTime transferDate;

    List<StoreInItemDTO> items;
}
