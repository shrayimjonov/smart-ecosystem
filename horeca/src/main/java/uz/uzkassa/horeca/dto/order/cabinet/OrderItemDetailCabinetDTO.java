package uz.uzkassa.horeca.dto.order.cabinet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.menu.MenuItemDetailItemDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.enums.OrderItemStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDetailCabinetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 22390990L;

    Long id;

    MenuItemDetailItemDTO menuItem;

    MultiUnitListDTO multiUnit;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal totalPrice;

    String comment;

    OrderItemStatus status;

    LocalDateTime orderDate;

    LocalDateTime lastModifiedDate;

    boolean hasMark;

    CommonDTO creator;

    CommonDTO updater;

    CommonDTO hall;

    CommonDTO table;

    CommonDTO order;

    Set<String> marks = new HashSet<>();

    BigDecimal vatRate;

    BigDecimal vatPrice;
}
