package uz.uzkassa.smartpossupply.dto.customergroup;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerGroupDTO implements Serializable {

    Long id;

    @NotNull
    String name;

    String description;

    @NotEmpty
    Set<Long> customerIds;
}
