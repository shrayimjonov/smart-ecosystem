package uz.uzkassa.cts.dto.customerbranchapplication.base;

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
public class CustomerBranchApplicationDTO implements Serializable {

    Long id;

    Long customerBranchId;

    List<CustomerBranchApplicationFieldDTO> fields;
}
