package uz.uzkassa.cts.dto.ofdapplication;

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
public class OfdApplicationMergeDTO implements Serializable {

    @NotNull
    Long fiscalizationId;

    @NotNull
    Long ofdId;
}
