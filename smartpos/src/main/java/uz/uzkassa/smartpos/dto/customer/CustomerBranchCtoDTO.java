package uz.uzkassa.smartpos.dto.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBranchCtoDTO implements Serializable {

    Long id;

    String name;

    String address;

    String region;

    String city;
}
