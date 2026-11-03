package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptBranchDTO extends CommonDTO {

    AddressDetailDTO address;

    public ReceiptBranchDTO(Long id, String name, AddressDetailDTO address) {
        super(id, name);
        this.address = address;
    }
}
