package uz.uzkassa.cts.dto.customerbranchapplication.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationFieldDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchApplicationDetailAdminDTO implements Serializable {

    Long id;

    CommonDTO customerBranch;

    CommonDTO reason;

    List<CustomerBranchApplicationFieldDTO> fields;
}
