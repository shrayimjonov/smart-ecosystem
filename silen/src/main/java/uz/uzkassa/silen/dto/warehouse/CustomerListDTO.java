package uz.uzkassa.silen.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.domain.Order;
import uz.uzkassa.silen.enumeration.OrganizationType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.EnumSet;

/**
 * A DTO for the {@link Order} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerListDTO implements Serializable {
    Long id;
    String customerName;
    String customerTin;
    EnumSet<OrganizationType> types;
    boolean isComitent;
    BigDecimal comission;
}
