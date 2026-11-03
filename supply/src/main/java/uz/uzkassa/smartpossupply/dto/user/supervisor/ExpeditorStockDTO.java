package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorStockDTO implements Serializable {
    String stockNumber;
    StockStatus status;
    CommonDTO customerBranch;
    LocalDateTime createdDate;
    CommonDTO order;
    PaymentType paymentType;
    CommonDTO factura;
}
