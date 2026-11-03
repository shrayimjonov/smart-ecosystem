package uz.uzkassa.cts.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TotalDTO implements Serializable {

    int deviceTotal;

    int fiscalModuleTotal;

    int employeeTotal;

    int customerBranchTotal;

    int fileTotal;

    @JsonIgnore
    int customerTotal;
}
