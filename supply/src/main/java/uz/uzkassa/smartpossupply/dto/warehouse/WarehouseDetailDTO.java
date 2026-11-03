package uz.uzkassa.smartpossupply.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.user.OwnerDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.TrailerType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseDetailDTO extends CommonDTO {

    @JsonProperty("isMain")
    boolean isMain;

    CommonDTO branch;

    CommonStatus status;

    OwnerDTO owner;

    String truckRegNo;

    String truckModel;

    String trailerRegNo;

    String trailerModel;

    TrailerType trailerType;

    CommonDTO parent;

    String transportCompanyTin;

    String transportCompanyName;

    AddressDetailDTO address;

    List<WarehouseDetailDTO> children = new ArrayList<>();

    public void addChild(WarehouseDetailDTO dto) {
        children.add(dto);
    }
}
