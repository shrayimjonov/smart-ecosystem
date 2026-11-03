package uz.uzkassa.smartpos.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerCtoDTO implements Serializable {

    Long id;

    String name;

    String tin;

    String businessType;

    CustomerBranchOwnerCtoDTO owner;

    List<CustomerBranchCtoDTO> branches;

}
