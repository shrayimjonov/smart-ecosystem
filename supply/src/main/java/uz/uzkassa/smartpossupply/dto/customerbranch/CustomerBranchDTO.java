package uz.uzkassa.smartpossupply.dto.customerbranch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 13:51
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchDTO implements Serializable {

    String name;

    String branchCode;

    String marketCode;

    AddressDTO address;

    Long customerId;

    Long branchId;

    Long warehouseId;

    String phone;

    boolean main;

    boolean orderingBlocked;

    List<EquipmentDTO> equipments;

    String mobileId;

    String externalId;
}
