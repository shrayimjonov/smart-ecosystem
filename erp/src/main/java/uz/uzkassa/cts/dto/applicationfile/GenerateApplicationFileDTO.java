package uz.uzkassa.cts.dto.applicationfile;

import jakarta.validation.constraints.NotNull;
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
public class GenerateApplicationFileDTO implements Serializable {

    @NotNull
    Long customerBranchId;

    String deviceSerial;

    String fiscalModuleSerial;

    Long fiscalizationId;
}
