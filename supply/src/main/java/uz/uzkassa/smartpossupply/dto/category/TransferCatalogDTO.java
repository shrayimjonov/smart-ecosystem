package uz.uzkassa.smartpossupply.dto.category;

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
public class TransferCatalogDTO implements Serializable {

    @NotNull
    Long fromBranchId;

    @NotNull
    @NotEmpty
    Set<Long> toBranchIds;
}
