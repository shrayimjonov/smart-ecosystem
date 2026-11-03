package uz.uzkassa.smartpossupply.dto.marketplacesettings;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.DeliveryType;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyDeliverySettingsDTO implements Serializable {

    @NotNull
    DeliveryType type;

    Integer fromDay;

    Integer toDay;

    Set<Long> warehouseIds;

    boolean selectAll = false;
}
