package uz.uzkassa.smartpossupply.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDTO;
import uz.uzkassa.smartpossupply.enums.TrailerType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseDTO implements Serializable {

    Long id;

    @NotNull
    String name;

    @NotNull
    Long branchId;

    Long ownerId;

    @JsonProperty("isMain")
    boolean isMain = false;

    Long parentId;

    String truckRegNo;

    String truckModel;

    String trailerRegNo;

    String trailerModel;

    TrailerType trailerType;

    String transportCompanyTin;

    String transportCompanyName;

    AddressDTO address;

    @JsonIgnore
    Long vehicleId;
}
