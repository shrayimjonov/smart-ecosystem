package uz.uzkassa.cts.filter;

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
public class SyncDeviceFilter implements Serializable {

    @NotNull
    Long companyId;

    @NotNull
    Long modelId;

    @NotNull
    Long total;
}
