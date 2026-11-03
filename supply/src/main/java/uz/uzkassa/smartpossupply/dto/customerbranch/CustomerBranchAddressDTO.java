package uz.uzkassa.smartpossupply.dto.customerbranch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDetailDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 13:51
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchAddressDTO extends CommonDTO {

    AddressDetailDTO address;

    String phone;

    BigDecimal currentBalance;

    List<EquipmentDetailDTO> equipments;

    public CustomerBranchAddressDTO(Long id, String name, AddressDetailDTO address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public CustomerBranchAddressDTO(Long id, String name, AddressDetailDTO address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public CustomerBranchAddressDTO(Long id, String name, AddressDetailDTO address, String phone, BigDecimal currentBalance) {
        super(id, name);
        this.address = address;
        this.phone = phone;
        this.currentBalance = currentBalance;
    }

    public CustomerBranchAddressDTO(Long id, String name, AddressDetailDTO address, String phone, BigDecimal currentBalance, List<EquipmentDetailDTO> equipments) {
        super(id, name);
        this.address = address;
        this.phone = phone;
        this.currentBalance = currentBalance;
        this.equipments = equipments;
    }
}
