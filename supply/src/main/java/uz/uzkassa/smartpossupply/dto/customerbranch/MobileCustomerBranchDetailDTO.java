package uz.uzkassa.smartpossupply.dto.customerbranch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDetailDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 14:37
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileCustomerBranchDetailDTO extends CommonDTO {

    String customerBranchNumber;

    AddressDetailDTO address;

    CommonDTO customer;

    CommonDTO branch;

    CommonDTO warehouse;

    CommonStatus status;

    String phone;

    List<EquipmentDetailDTO> equipments;

    boolean orderingBlocked;

    LocalDateTime createdDate;
}
