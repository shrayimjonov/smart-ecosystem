package uz.uzkassa.cts.dto.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBranchLookUpDTO implements Serializable {

    String customerName;

    List<CustomerBranchItemsDTO> customerBranches;
}
