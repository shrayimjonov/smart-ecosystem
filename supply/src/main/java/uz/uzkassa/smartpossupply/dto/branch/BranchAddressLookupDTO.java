package uz.uzkassa.smartpossupply.dto.branch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

/**
 * Created by: Rayimjonov Shuxratjon
 * Date: 19.10.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchAddressLookupDTO extends CommonDTO {

    AddressDetailDTO address;

    String officialName;

    String branchNumber;

    boolean main;
}
