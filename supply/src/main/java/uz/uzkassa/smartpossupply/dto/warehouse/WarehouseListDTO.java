package uz.uzkassa.smartpossupply.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.user.OwnerDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseListDTO extends CommonDTO {

    CommonDTO branch;

    OwnerDTO owner;

    CommonStatus status;

    @JsonProperty("isMain")
    boolean isMain;

    CommonDTO parent;

    String truckRegNo;

    String truckModel;

    boolean hasChild;
}
