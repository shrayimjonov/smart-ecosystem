package uz.uzkassa.smartpossupply.dto.offlineorder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfflineOrderListDTO {

    List<OfflineOrderDTO> orders;
}
