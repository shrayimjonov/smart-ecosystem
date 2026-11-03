package uz.uzkassa.cts.dto.fm;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FiscalModuleRegisterDTO implements Serializable {

    Long id;

    @NotNull
    @NotEmpty
    String serialNumber;

    Long companyId;
}
