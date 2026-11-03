package uz.uzkassa.cts.dto.apay;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayBranchDTO implements Serializable {

    String id;

    String name;

    String phone;

    ApayAddressDTO address;

    ApayActivityTypeDTO activityType;
}
