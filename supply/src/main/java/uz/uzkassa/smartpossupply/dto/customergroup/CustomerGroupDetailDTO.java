package uz.uzkassa.smartpossupply.dto.customergroup;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerGroupDetailDTO {

    List<CompanyBaseDTO> customers;
}
