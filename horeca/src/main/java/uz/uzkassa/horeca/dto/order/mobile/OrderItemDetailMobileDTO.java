package uz.uzkassa.horeca.dto.order.mobile;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.enums.OrderItemStatus;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemDetailMobileDTO extends OrderItemCommentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 22390990L;

    Long id;

    CommonDTO menuItem;

    MultiUnitListDTO multiUnit;

    BigDecimal qty;

    BigDecimal price;

    Vat vat;

    BigDecimal totalPrice;

    OrderItemStatus status;

    LocalDateTime orderDate;

    FileDTO photo;

    CommonDTO waiter;

    CommonDTO updater;

    LocalDateTime lastModifiedDate;

    boolean hasMark;

    Set<String> marks = new HashSet<>();

    BigDecimal vatPrice;

    BigDecimal vatRate;
}
