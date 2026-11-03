package uz.uzkassa.smartpossupply.dto.companycommittent;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyCommittentDTO implements Serializable {

    Long id;

    Long committentId;

    Long companyId;

}
