package uz.uzkassa.smartpossupply.dto.branch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.user.OwnerDTO;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchListDTO extends CommonDTO {

    CommonStatus status;

    BranchType type;

    AddressDetailDTO address;

    OwnerDTO owner;

    @JsonProperty("isMain")
    boolean isMain;

    String branchNumber;

    String officialName;

    LocalDateTime registrationDate;

    CommonDTO currentShift;
}
