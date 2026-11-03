package uz.uzkassa.cts.dto.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBranchListMapDTO implements Serializable {

    Long id;

    String name;

    String brand;

    String phone;

    String longitude;

    String latitude;

    String customer;

    String tin;

    String activityType;

}
