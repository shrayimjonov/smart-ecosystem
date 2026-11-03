package uz.uzkassa.smartpos.dto.branch;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BranchListMapDTO implements Serializable {

    Long id;

    String name;

    String phone;

    String longitude;

    String latitude;

    String customer;

    String tin;

    String activityType;

}
