package uz.uzkassa.cts.dto.customerBranchScanner;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class CustomerBranchScannerDTO implements Serializable {

    @NotNull
    String code;
    @NotEmpty
    @NotNull
    String name;
}
