package uz.uzkassa.cts.dto.customerbranchapplication.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchApplicationFieldDTO implements Serializable {

    String fieldName;

    String oldName;
    String oldValue;

    String newName;
    String newValue;
}
