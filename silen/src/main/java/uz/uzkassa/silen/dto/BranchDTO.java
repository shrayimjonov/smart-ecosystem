package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.invoice.DistrictDTO;
import uz.uzkassa.silen.dto.invoice.RegionDTO;
import uz.uzkassa.silen.enumeration.BranchStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchDTO implements Serializable {
    String id;

    String name;

    BranchStatus status;

    String districtId;

    DistrictDTO district;

    String regionId;

    RegionDTO region;

    String address;

    Double lat;

    Double lon;

    boolean main = false;

    String branchCode;

    String managerId;

    UserDTO manager;

    String organizationId;

    SelectItem organization;
}
