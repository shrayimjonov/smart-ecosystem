package uz.uzkassa.smartpossupply.dto.marketplacesettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.DeliveryType;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyDeliverySettingsDetailDTO implements Serializable {

    Long id;

    DeliveryType type;

    Integer fromDay;

    Integer toDay;

    Set<CommonDTO> warehouses;

    boolean selectAll = false;
}
