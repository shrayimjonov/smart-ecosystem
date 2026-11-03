package uz.uzkassa.horeca.dto.order.kanban;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.enums.OrderItemStatus;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.05.2024
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderItemListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 435158934663L;

    Long id;

    CommonDTO menuItem;

    OrderItemStatus status;

    BigDecimal qty;

    CommonDTO chef;

    MultiUnitListDTO multiUnit;

    String comment;

    LocalDateTime orderItemDate;

    LocalDateTime lastModifiedDate;

    CommonDTO creator;

    CommonDTO updater;
}
