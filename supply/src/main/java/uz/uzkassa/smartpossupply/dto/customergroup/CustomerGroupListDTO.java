package uz.uzkassa.smartpossupply.dto.customergroup;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerGroupListDTO extends CommonDTO{

    Integer customerCount;

    LocalDateTime createdDate;

    CommonDTO creator;

    CommonStatus status;

    String description;
}
