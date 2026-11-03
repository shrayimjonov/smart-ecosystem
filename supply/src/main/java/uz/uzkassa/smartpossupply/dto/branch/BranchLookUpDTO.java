package uz.uzkassa.smartpossupply.dto.branch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchLookUpDTO extends CommonDTO {

    @JsonProperty("isMain")
    boolean isMain = false;

    String branchNumber;

    String officialName;
}
